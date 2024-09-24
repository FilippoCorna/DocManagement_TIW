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

import it.polimi.tiw.project3.dao.UserDAO;
import it.polimi.tiw.project3.dao.SubfolderDAO;
import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.dao.DocumentDAO;

/*
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
*/

@WebServlet("/CreateDocument")
public class CreateDocument extends HttpServlet{

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
		User user = null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
			if (session.isNew() || user == null) {
				String loginpath = getServletContext().getContextPath();
				response.sendRedirect(loginpath);
				return;
			}
	
		// Get parameter from request
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String summary = request.getParameter("summary");
		String subfolderIdString = (request.getParameter("subfolderId")); 
		int subfolderId;
		 
		
		
		if (name == null || subfolderIdString == null) {
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		try {
		subfolderId = Integer.parseInt(subfolderIdString);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(505, "Bad parameters");
			return;
		}
		
		SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		boolean correctOperation = false;
		try {
			correctOperation = subfolderDAO.checkUserSubfolder(user.getId(), subfolderId);
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(500, "Database access failed");
			return;

		}
		
		if(correctOperation) {
			DocumentDAO DocumentDAO = new DocumentDAO(connection);
		
			int userId = ((User)session.getAttribute("currentUser")).getId();
		
			try {
				DocumentDAO.createDocument(name, type, summary, userId, subfolderId);
			
				String documentCreated = "Documento creato!";
				String path= "GoToGestioneContenuti";
				String messageType ="message";
			
			
			
				// Redirect to the Gestione Contenuti and notify the succession creating folder
				response.sendRedirect(path+"?documentCreated="+ documentCreated+
						"&messageType=" + messageType);
			
				//mettere con thymeleaf una cosa che ti dice documento creata(?)FATTO
			
			} catch (SQLException e) {
	
				String message = e.getCause().getMessage();
				//evita di poter creare una nuova cartella con lo stesso nome FATTO
				if(message.startsWith("Duplicate entry")) {
					message = "Nome già	usato. Scegline uno nuovo.";
					
					String documentCreated = message;
					String path= "GoToGestioneContenuti";
					String messageType ="Error";
				
				
				
					// Redirect to the Gestione Contenuti and notify the failure creating folder
					response.sendRedirect(path+"?documentCreated="+ documentCreated+
							"&messageType=" + messageType);
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
			
		}
	}



