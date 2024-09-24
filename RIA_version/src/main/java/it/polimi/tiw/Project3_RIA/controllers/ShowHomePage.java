package it.polimi.tiw.Project3_RIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.Project3_RIA.beans.Folder;
import it.polimi.tiw.Project3_RIA.beans.Subfolder;
import it.polimi.tiw.Project3_RIA.beans.User;
import it.polimi.tiw.Project3_RIA.dao.FolderDAO;
import it.polimi.tiw.Project3_RIA.dao.SubfolderDAO;

/**
 * Servlet implementation class ShowHomePage  
 */
@WebServlet("/ShowHomePage")
public class ShowHomePage extends HttpServlet {
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
	 * Show home page with folders and subfolders list.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
			FolderDAO folderDAO = new FolderDAO(connection);
			List<Folder> folders = null;
			try {
				folders = folderDAO.findFoldersByUser(user.getId());
			}
			catch(SQLException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Impossibile recuperare le cartelle");
				return;
			}  
				
				//If user has no folder 
				if(folders.isEmpty() || folders == null) { 
					
					folders = new ArrayList<Folder>();
					
					Gson gson = new GsonBuilder()
							   .setDateFormat("yyyy MMM dd").create();
					String json = gson.toJson(folders);
					
					// Redirect to the Home page with user folders and subfolders
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
					return;
				}
				
		
				
				try {
				//looking for subfolders	
				SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
				Map<String,List<Subfolder>> subfolders = new HashMap<String,List<Subfolder>>();
				for(Folder folder: folders) {
					List<Subfolder> subfoldersPerFolder = subfolderDAO.
												findFoldersByFolder(folder.getId());
					folder.setSubfolders(subfoldersPerFolder);
				}
				
				Gson gson = new GsonBuilder()
						   .setDateFormat("yyyy MMM dd").create();
				String json = gson.toJson(folders);
				
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				
			    }
				catch(SQLException e) {
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Impossibile recuperare le sottocartelle");
					return;
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
