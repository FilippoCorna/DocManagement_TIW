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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project3.dao.UserDAO;
import it.polimi.tiw.project3.beans.User;



/**
 * Servlet implementation class Login
 */
@WebServlet("/Login") 
public class Login extends HttpServlet {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
	    String password = request.getParameter("password");

		if (username == null || password == null) {
			response.sendError(505, "Parameters incomplete");
			return;
		}
	    
		//Check if parameters are correct and then create a new session and bond 
		//it with this user
		UserDAO userDAO = new UserDAO(connection);
		try {
			User user = userDAO.checkUser(username, password);
			if (user != null) {
				HttpSession session = request.getSession();
				session.setAttribute("currentUser", user);
				session.setAttribute("movePhase", false);
				String path = getServletContext().getContextPath() + "/ShowHomePage";
				response.sendRedirect(path);
			}
			else {
				String message = "Username o password errati.";
				
				
				// Redirect to the Public page and notify the failure of the registration
				response.sendRedirect("Welcome?loginMessage="+ message);		}
		} catch (SQLException e) {
			response.sendError(500, "Database access failed");
		}
	}
	
	
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
		
}


