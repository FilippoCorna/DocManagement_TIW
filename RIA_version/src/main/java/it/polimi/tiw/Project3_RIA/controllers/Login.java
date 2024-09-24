package it.polimi.tiw.Project3_RIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.Project3_RIA.dao.UserDAO;
import it.polimi.tiw.Project3_RIA.beans.User;



/**
 * Servlet implementation class Login
 */
@WebServlet("/Login") 
@MultipartConfig
public class Login extends HttpServlet {
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


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
	    String password = StringEscapeUtils.escapeJava(request.getParameter("password"));

		if (username == null || password == null || username.isEmpty() 
				|| password.isEmpty()) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credenziali nulle");
			return;
		}
	    
		//Check if parameters are correct and then create a new session and bond 
		//it with this user
		UserDAO userDAO = new UserDAO(connection);
		try {
			User user = userDAO.checkUser(username, password);
			
			// If the user exists, add info to the session and go to home page, otherwise
			// return an error status code and message
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Credenziali errate");
			} 
			else {
				request.getSession().setAttribute("currentUser", user);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(username); 
				
			}
		}catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore interno al server, riprovare piu' tardi");
			return;		} 
	}
	
	
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
		
}


