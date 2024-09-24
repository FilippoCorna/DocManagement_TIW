package it.polimi.tiw.project3.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project3.beans.Folder;
import it.polimi.tiw.project3.beans.Subfolder;
import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.dao.FolderDAO;
import it.polimi.tiw.project3.dao.SubfolderDAO;

/**
 * Servlet implementation class GoToGestioneContenuti
 */
@WebServlet("/GoToGestioneContenuti")
public class GoToGestioneContenuti extends HttpServlet {
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
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "WEB-INF/GestioneContenuti.html";
		String folderCreated = request.getParameter("folderCreated");
		String subfolderCreated = request.getParameter("subfolderCreated");
		String documentCreated = request.getParameter("documentCreated");
		String messageType = request.getParameter("messageType");




		HttpSession session = request.getSession(false);
		User user = null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		//Back to Public Page to login
		if(user == null || session == null) {
			response.sendRedirect(getServletContext().getContextPath());
			return;
		}
		
		//Looking for user's folder and subfolders.
		//Give to each folder a list with its subfolders 
		else {
			FolderDAO folderDAO = new FolderDAO(connection);
			try {
				List<Folder> folders = folderDAO.findFoldersByUser(user.getId());
				
				//If user has no folder
				if(folders.isEmpty() || folders == null) {
					
					// Redirect to the Home page with user folders and subfolders
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("user", user);
					templateEngine.process(path, ctx, response.getWriter());
				}
				
				else {
				
				//looking for subfolders	
				SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
				Map<String,List<Subfolder>> subfolders = new HashMap();
				for(Folder folder: folders) {
					List<Subfolder> subfoldersPerFolder = subfolderDAO.
												findFoldersByFolder(folder.getId());
					folder.setSubfolders(subfoldersPerFolder);
				
				}  
				 
				
		     
				
	
				// Redirect to the Gestione Contenuti with a creation's message 
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				
				//startsWith serve perchè altrimenti non mi mostra bene la 'à'
				if(folderCreated != null) {
					if(folderCreated.startsWith("Nome")) 
						folderCreated = "Nome già usato. Scegline uno nuovo.";
				
					ctx.setVariable("folderCreated", folderCreated);
					}
				else
					ctx.setVariable("folderCreated", "");
				
				if(documentCreated != null) {
					if(documentCreated.startsWith("Nome")) 
						documentCreated = "Nome già usato. Scegline uno nuovo.";
					ctx.setVariable("documentCreated", documentCreated);
					}
				else
					ctx.setVariable("documentCreated", "");


				
				if(subfolderCreated != null) {
					if(subfolderCreated.startsWith("Nome")) 
						subfolderCreated = "Nome già usato. Scegline uno nuovo.";
					ctx.setVariable("subCreated", subfolderCreated);
					}
				else
					ctx.setVariable("subCreated", "");

				String Error = "";
				String ErrorClass = "message";
				if(messageType!= null && messageType.equals("Error")) {
					Error = "ERROR";
					ErrorClass = "Warning";
					}
				
				ctx.setVariable("folders", folders);
				ctx.setVariable("messageType", messageType);
				ctx.setVariable("Error", Error);
				ctx.setVariable("ErrorClass", ErrorClass);
				ctx.setVariable("user", user);
				templateEngine.process(path, ctx, response.getWriter());
			} 
				}
			catch(SQLException e) {
				e.printStackTrace();
				response.sendError(500, "Database access failed");
			} 
			
			}
	}

	

}
