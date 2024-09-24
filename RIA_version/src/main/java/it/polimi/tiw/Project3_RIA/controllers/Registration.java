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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.Project3_RIA.dao.UserDAO;



/**
 * Servlet implementation class Registration
 */ 
@WebServlet("/Registration")
@MultipartConfig
public class Registration extends HttpServlet {
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
	 * Create a new row in user table in db
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password1 = null;
		String password2 = null; 
		String email = null; 
		
		System.out.print(request.getParameter("username"));
		
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password1 = StringEscapeUtils.escapeJava(request.getParameter("password1"));
		password2 = StringEscapeUtils.escapeJava(request.getParameter("password2"));
		
		if (username == null || password1 == null || 
				password2 == null || email == null|| 
				email.isEmpty() || username.isEmpty() 
				|| password1.isEmpty()|| password1.isEmpty() ) {
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credenziali nulle");
			return;
		}
		
		if(!email.contains("@") || !email.contains(".") || email.startsWith("@") ||
				email.startsWith(".") || email.endsWith("@") || email.endsWith(".")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("E-Mail errata");
			return;
		}
				
		if(!password1.equals(password2)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Password diverse");
			return;
		}
		
		// query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		try {
			userDao.createUser(username, password1, email);
		} catch (SQLException e) {
			
			if(e.getMessage() != null && e.getMessage().startsWith("Duplicate")) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("username gia' preso");
				return;
			}
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Errore interno al server, riprovare piu' tardi");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Registrazione effettuata");

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
