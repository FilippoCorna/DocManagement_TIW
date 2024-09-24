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
 * Servlet implementation class ShowHomePage  
 */
@WebServlet("/ShowHomePage")
public class ShowHomePage extends HttpServlet {
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
	 * Show home page with folders and subfolders list.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = "WEB-INF/Home.html";
		HttpSession session = request.getSession(false);
		User user = null;
		
		if(session != null) {
			user = (User) session.getAttribute("currentUser");
		}
		
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
				 
				
		     
				
	
				// Redirect to the Home page with user folders and subfolders
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("folders", folders);
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
