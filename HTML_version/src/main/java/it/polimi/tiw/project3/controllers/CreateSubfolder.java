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

import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.dao.FolderDAO;
import it.polimi.tiw.project3.dao.SubfolderDAO;

/*
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
*/

@WebServlet("/CreateSubfolder")
public class CreateSubfolder extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

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
		User user=null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
			if (session.isNew() || user == null) {
				String loginpath = getServletContext().getContextPath();
				response.sendRedirect(loginpath);
				return;
			}
	
		// Get parameter from request
		String name = request.getParameter("name");
		String folderIdString = request.getParameter("folderId"); 
		
		//If user did not choose a folder as father
		if(folderIdString == null) {
			
			String message = "Scegli una cartella";
			
			String subfolderCreated = message;
			String path = "GoToGestioneContenuti";
			String messageType = "Error";
			response.sendRedirect(path+"?subfolderCreated="+ subfolderCreated
					+"&messageType="+messageType);
			return;
		}
		
		int folderId = Integer.parseInt(folderIdString);
		
		if (name == null) {
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		FolderDAO folderDAO = new FolderDAO(connection);
		
		//Check if subfolder will be created in a currentUser's folder
		boolean correctOperation = false;
		
		try {
			correctOperation = folderDAO.checkUserFolder(user.getId(), folderId);
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(500, "Database access failed");
			return;
		}
		
		if(correctOperation) {
			SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		
		
			try {
				subfolderDAO.createSubfolder(name, folderId);
			
				String subfolderCreated ="Sottocartella creata";
				String path = "GoToGestioneContenuti";
				String messageType = "message";
				response.sendRedirect(path+"?subfolderCreated="+ subfolderCreated
						+"&messageType="+messageType);
			
			//mettere con thymeleaf una cosa che ti dice sottocartella creata(?) FATTO
			
			} catch (SQLException e) {
				String message = e.getCause().getMessage();
				//evita di poter creare una nuova cartella con lo stesso nome FATTO
				if(message.startsWith("Duplicate entry")) {
					message = "Nome già usato. Scegline uno nuovo.";
					
					String subfolderCreated = message;
					String path = "GoToGestioneContenuti";
					String messageType = "Error";
					response.sendRedirect(path+"?subfolderCreated="+ subfolderCreated
							+"&messageType="+messageType);
					return;
				}
				else
					message ="Bad insertion input";
			
				response.sendError(505, message);
			}
		}
			else
				response.sendError(505, "Invalid operation");
			
			//o ancora meglio trovare un modo per crearla ma rinominandola con un numero dopo
			//es. provo a creare sottocartella col nome "subfold" ma esiste già, dovrebbe crearmela con nome "subfold1"
			
		}
	}

