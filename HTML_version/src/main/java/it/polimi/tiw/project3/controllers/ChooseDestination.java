package it.polimi.tiw.project3.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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

import it.polimi.tiw.project3.beans.Document;
import it.polimi.tiw.project3.dao.SubfolderDAO;
import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.dao.DocumentDAO;

@WebServlet("/ChooseDestination")
public class ChooseDestination  extends HttpServlet{
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
		
		HttpSession session = request.getSession(false);
		User user = null;
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		//Back to Public Page to login
		if(user == null) {
			response.sendRedirect(getServletContext().getContextPath());
		}
		
		String subfolderIdString = request.getParameter("subfolderId");
		String documentIdString = request.getParameter("documentId");
		//String documentIdString = (String)request.getAttribute("documentId");
		
		Integer subfolderId;
		Integer documentId;
		
		try {
		subfolderId = Integer.parseInt(subfolderIdString);
		documentId = Integer.parseInt(documentIdString);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(505, "Bad parameters");
			return;
		}
		
		DocumentDAO documentDAO = new DocumentDAO(connection);
		SubfolderDAO subDAO = new SubfolderDAO(connection);
		List<Document> docx = null;
		try {
			docx = documentDAO.findDocumentById(documentId);
			boolean check = subDAO.checkUserSubfolder(user.getId(), subfolderId);
			if(docx.get(0).getUser() != user.getId() || !check) {
				response.sendError(401);
				return;	
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(500);
			return;	
		}
		
		try {
			documentDAO.moveDocument(documentId, subfolderId);
			
			//Redirect to the Home page 
			String path = getServletContext().getContextPath() + "/ShowHomePage";
			response.sendRedirect(path);
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(500, "Database access failed");
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