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
 * Servlet implementation class ShowDocumentsPage
 */
@WebServlet("/Documents")
public class ShowDocumentsPage extends HttpServlet {
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
	 * Show document in subfolder with chosen subfolderId and name.
	 * Check if currentUser has access at this subfolder. 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = "WEB-INF/Documents.html";
		HttpSession session = request.getSession(false);
		String subFolderId = request.getParameter("subfolderId");
		String subfolderName = request.getParameter("name");
		User user = null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		//Back to Public Page to login
		if(user == null || session == null) {
			response.sendRedirect(getServletContext().getContextPath());
			return;
		}
	
		Integer id;
		
		try {
		id = Integer.parseInt(subFolderId);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(505, "Bad parameters");
			return;
		}
		
		SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		
		try {
			
			boolean correctOperation = subfolderDAO.checkUserSubfolder(user.getId(),id);
			if(correctOperation) {
				
				DocumentDAO documentDAO = new DocumentDAO(connection);
				try {
					List<Document> documents = documentDAO.findDocumentsBySubfolder(id);
					
					//Redirect to the Documents page with user documents in selected subfolder
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("documents", documents );
					ctx.setVariable("user", user);
					ctx.setVariable("subfolderName", subfolderName);
					ctx.setVariable("subfolderId", id);
					templateEngine.process(path, ctx, response.getWriter());
					
					
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(500, "Database access failed");
				}
			}
			
			else {
				response.sendError(505, "Operation not available");

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(500, "Database access failed");
		}
		
		
		
	}

	@Override
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				
			}
		}
	}
 
}
