package it.polimi.tiw.project3.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
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

import it.polimi.tiw.project3.beans.User;

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
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
	 * When user has a session send him to his home page,
	 * otherwise send him to PublicPage to login or to create a new account. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);	//controlla se esiste una sessione ma non crearla se non esiste
		User user = null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		//If User has no session go to Public Page
		if(user == null) {
			String path ="/WEB-INF/PublicPage.html";
			
			String mesType = request.getParameter("mesType");
			String message = request.getParameter("message");
			String messageLogin = request.getParameter("loginMessage");
			
			if(message != null && message.startsWith("Nome"))
				message = "nome già usato. Scegline uno nuovo";
			
			// Redirect to the Public page and notify the success of the registration
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			if(message != null)
				ctx.setVariable("registrationMessage", message);
			if(mesType != null)
				ctx.setVariable("mesType", mesType);
			if(messageLogin != null)
				ctx.setVariable("loginMessage", messageLogin);

			templateEngine.process(path, ctx, response.getWriter());
			
		}
		else {
			String path = "ShowHomePage";
			response.sendRedirect(path);
			
		}
		
	
	}

	

}
