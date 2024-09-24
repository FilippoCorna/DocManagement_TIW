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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.Project3_RIA.beans.Subfolder;
import it.polimi.tiw.Project3_RIA.beans.User;
import it.polimi.tiw.Project3_RIA.dao.FolderDAO;
import it.polimi.tiw.Project3_RIA.dao.SubfolderDAO;


@MultipartConfig
@WebServlet("/CreateSubfolder")
public class CreateSubfolder extends HttpServlet{

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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		User user=null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
			if (session.isNew() || user == null) {
				String loginpath = getServletContext().getContextPath();
				response.sendRedirect(loginpath);
				return;
			}
	
		// Get parameter from request
		String name = request.getParameter("name");
		String folderIdString = request.getParameter("folderId"); 
		

		
		//If user did not choose a folder as father
		if(folderIdString == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Cartella padre mancante");
			return;
		}
		int folderId;
		
		try {
			folderId = Integer.parseInt(folderIdString);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri non corretti");
			return;
		}
		
		
		if (name == null || name.equals("") || name.length() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri non corretti");
			return;
		}
		
		FolderDAO folderDAO = new FolderDAO(connection);
		
		//Check if subfolder will be created in a currentUser's folder
		boolean correctOperation = false;
		
		try {
			correctOperation = folderDAO.checkUserFolder(user.getId(), folderId);
		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("accesso al database fallito");
			return;
		}
		
		if(correctOperation) {
			SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		
		
			try {
				subfolderDAO.createSubfolder(name, folderId);
				Subfolder sub = subfolderDAO.findSubfoldersByName(name,folderId);
			
				Gson gson = new GsonBuilder()
						   .setDateFormat("yyyy MMM dd").create();
				String json = gson.toJson(sub);
				 
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				
			    return;
			
			
			} catch (SQLException e) {
				e.printStackTrace();
				String message = e.getCause().getMessage();
				//evita di poter creare una nuova cartella con lo stesso nome FATTO
				if(message.startsWith("Duplicate entry")) {
					message = "Nome gia' usato. Scegline uno nuovo.";
					
					
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println(message);
					return;
				}
				else
					
			
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Parametri non corretti");
				return;
			}
		}
			else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Operazione non permessa");
				return;	
				}		
			
		}
	}

