package it.polimi.tiw.project3.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project3.dao.FolderDAO;
import it.polimi.tiw.project3.dao.SubfolderDAO;
import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.beans.Document;
import it.polimi.tiw.project3.beans.Folder;
import it.polimi.tiw.project3.beans.Subfolder;
import it.polimi.tiw.project3.dao.DocumentDAO;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


@WebServlet("/MoveDocument")
public class MoveDocument extends HttpServlet {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "WEB-INF/MoveDocument.html";
		HttpSession session = request.getSession(false);
		User user = null;
		
		if(session != null) {
			user = (User) session.getAttribute("currentUser");
		}
		
		String documentId = request.getParameter("documentId");
		String subfolderId = request.getParameter("subfolderId");
		String subfolderName = request.getParameter("name");
		
		Integer subfolder, document;
		try {
			subfolder = Integer.parseInt(subfolderId);
			document = Integer.parseInt(documentId);

		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(505, "Bad parameters");
			return;
		}
		
		
		
		
	
		//Back to Public Page to login
		if(user == null) {
			response.sendRedirect(getServletContext().getContextPath());
		}else {
			
			//Check if user has this subfolder
			SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
			boolean correctOperation = false;
			try {
				correctOperation = subfolderDAO.checkUserSubfolder(user.getId(), subfolder);
				if(!correctOperation) {
					response.sendError(505, "Not your subfolder");
					return;
					}
			} catch (SQLException e1) {
				e1.printStackTrace();
				response.sendError(500, "Database access failed");
				return;
			}
			
			//Check if user has this document
			DocumentDAO documentDAO = new DocumentDAO(connection);
			correctOperation = false;
			List<Document> doc;
			try {
				doc = documentDAO.findDocumentById(document);
				if(doc == null || doc.isEmpty())
					correctOperation = false;
				else
					correctOperation = doc.get(0).getUser() == user.getId();
			} catch (SQLException e1) {
				e1.printStackTrace();
				response.sendError(500, "Database access failed");
			}
			
			if(correctOperation) {
				FolderDAO folderDAO = new FolderDAO(connection);
				try {
					List<Folder> folders = folderDAO.findFoldersByUser(user.getId());
				
		
					//looking for subfolders					
					Map<String,List<Subfolder>> subfolders = new HashMap();
					for(Folder folder: folders) {
						List<Subfolder> subfoldersPerFolder = subfolderDAO.
												findFoldersByFolder(folder.getId());
						folder.setSubfolders(subfoldersPerFolder);
				
					}
				
					Subfolder notAllowedSubfolder = new Subfolder();
					notAllowedSubfolder = subfolderDAO.findSuboldersBySubfolderId(subfolder);
	
					// Redirect to the MoveDocumentPage with user folders and subfolders
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("folders", folders);
					ctx.setVariable("notAllowedSubfolder", notAllowedSubfolder);
					ctx.setVariable("notAllowedsubfolderName", subfolderName);
					ctx.setVariable("subfolderId", subfolder);
					ctx.setVariable("documentId",documentId);
					ctx.setVariable("user", user);
				
				//passare il documentId alla servelt "successiva" ChooseDestination
				/*
				request.setAttribute("documentId",documentId);
				RequestDispatcher rd = request.getRequestDispatcher("/ChooseDestination");
				rd.forward(request,response);*/
				
					templateEngine.process(path, ctx, response.getWriter()); 
			
				}
				
				catch(SQLException e) {
					e.printStackTrace();
					response.sendError(500, "Database access failed");
				} 
			}
			else {
				response.sendError(505, "Not your document");
			}
				
			}
		
		/*
		<a th:if="${subfolder.getId() != notAllowedSubfolderId}">
				<b th:href ="@{/ChooseDestination(subfolderId=${subfolder.getId()} , name=${subfolder.getName()})}">Sposta qui </b>
			</a>
			*/
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