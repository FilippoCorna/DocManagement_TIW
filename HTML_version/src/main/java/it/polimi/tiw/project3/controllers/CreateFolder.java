package it.polimi.tiw.project3.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project3.dao.FolderDAO;
import it.polimi.tiw.project3.beans.User;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


@WebServlet("/CreateFolder")
public class CreateFolder extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine; 

       
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			
			ServletContext servletContext = getServletContext();
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
			templateResolver.setTemplateMode(TemplateMode.HTML);
			this.templateEngine = new TemplateEngine();
			this.templateEngine.setTemplateResolver(templateResolver);
			templateResolver.setSuffix(".html");

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
	}   

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
			if (session.isNew() || session.getAttribute("currentUser") == null) {
				String loginpath = getServletContext().getContextPath();
				response.sendRedirect(loginpath);
				return;
			}
	
		// Get parameter from request
		String name = request.getParameter("name");
		
		if (name == null) {
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		FolderDAO folderDAO = new FolderDAO(connection);
		
		int userId = ((User)session.getAttribute("currentUser")).getId();
		
		try {
			folderDAO.createFolder(name, userId);
			String folderCreated = "Cartella creata!";
			String path= "GoToGestioneContenuti";
			String messageType = "message";
			
			
			
			// Redirect to the Gestione Contenuti and notify the succession creating folder
			response.sendRedirect(path+"?folderCreated="+ folderCreated+
					"&messageType="+messageType);
			
			//mettere con thymeleaf una cosa che tidice cartella creata(?) FATTO
			
		} catch (SQLException e) {
			String message = e.getCause().getMessage();
			//evita di poter creare una nuova cartella con lo stesso nome
			if(message.startsWith("Duplicate entry")) {
				message = "Nome già usato. Scegline uno nuovo.";
				
				String folderCreated = message;
				String path= "GoToGestioneContenuti";
				String messageType = "Error";
				
				
				
				// Redirect to the Gestione Contenuti and notify the succession creating folder
				response.sendRedirect(path+"?folderCreated="+ folderCreated+
						"&messageType="+messageType);
				return;
			}
			else
				message ="Bad insertion input";
			
			response.sendError(505, message);
			
			//o ancora meglio trovare un modo per crearla ma rinominandola con un numero dopo
			//es. provo a creare cartella col nome "fold" ma esiste già, dovrebbe crearmela con nome "fold1"
			
		}
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}	
}
