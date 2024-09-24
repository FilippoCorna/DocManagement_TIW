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
import it.polimi.tiw.project3.beans.User;
import it.polimi.tiw.project3.dao.DocumentDAO;
import it.polimi.tiw.project3.dao.SubfolderDAO;

/**
 * Servlet implementation class ShowDocument
 */
@WebServlet("/ShowDocument")
public class ShowDocument extends HttpServlet {
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
		String path = "WEB-INF/Documento.html";
		HttpSession session = request.getSession(false);
		User user = null;
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		String documentId = request.getParameter("documentId");
		String subfolderId = request.getParameter("subfolderId");
		String subfolderName = request.getParameter("name");
		
		
		//Back to Public Page to login
		if(user == null || session == null) {
			response.sendRedirect(getServletContext().getContextPath());
			return;
		}
	
		int id;
		Integer subfolder;
		try {
			id = Integer.parseInt(documentId);
			subfolder = Integer.parseInt(subfolderId);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(505, "Bad parameters");
			return;
		}
		
		//check if user has this subfolder
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
		
		DocumentDAO documentDAO = new DocumentDAO(connection);
		try {
			List<Document> documents = documentDAO.findDocumentById(id);
			Document document = null ;
			if(!documents.isEmpty())
				document = documents.get(0);
			
			//Check if user has this document
			if(document== null || document.getUser() != user.getId()) {
				response.sendError(505, "Not your document");
				return;
			}
			else {
				
				//Redirect to the Document page with user document info
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("user", user);
				ctx.setVariable("document", document);
				ctx.setVariable("subfolder", subfolder);
				ctx.setVariable("subfolderName", subfolderName);
				templateEngine.process(path, ctx, response.getWriter());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(500, "Database access failed");
		}
	}

	

}
