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

import it.polimi.tiw.Project3_RIA.dao.UserDAO;
import it.polimi.tiw.Project3_RIA.dao.SubfolderDAO;
import it.polimi.tiw.Project3_RIA.beans.Document;
import it.polimi.tiw.Project3_RIA.beans.User;
import it.polimi.tiw.Project3_RIA.dao.DocumentDAO;



@WebServlet("/CreateDocument")
@MultipartConfig
public class CreateDocument extends HttpServlet{

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
		User user = null;
		
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
			if (session.isNew() || user == null) {
				String loginpath = getServletContext().getContextPath();
				response.sendRedirect(loginpath);
				return;
			}
	
		// Get parameter from request
		String name = request.getParameter("docname");
		String type = request.getParameter("type");
		String summary = request.getParameter("summary");
		String subfolderIdString = (request.getParameter("subfolderId")); 
		int subfolderId;
		 
		
		
		if (name == null || subfolderIdString == null || name.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri Incorretti");
			return;
		}
		
		try {
		subfolderId = Integer.parseInt(subfolderIdString);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri Incorretti");
			return;
		}
		
		SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		boolean correctOperation = false;
		try {
			correctOperation = subfolderDAO.checkUserSubfolder(user.getId(), subfolderId);
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Accesso al database fallito");
			return;

		}
		
		if(correctOperation) {
			DocumentDAO DocumentDAO = new DocumentDAO(connection);
		
			int userId = ((User)session.getAttribute("currentUser")).getId();
		
			try {
				DocumentDAO.createDocument(name, type, summary, userId, subfolderId);
				Document doc = DocumentDAO.findDocumentByName(name,subfolderId);
				
			
				// Redirect to the Gestione Contenuti and notify the succession creating folder
				Gson gson = new GsonBuilder()
						   .setDateFormat("yyyy MMM dd").create();
				String json = gson.toJson(doc);
				 
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
				
			    return;
			
			
			} catch (SQLException e) {
	
				String message = e.getCause().getMessage();
				//evita di poter creare una nuova cartella con lo stesso nome FATTO
				if(message.startsWith("Duplicate entry")) {
					message = "Nome già	usato. Scegline uno nuovo.";
					
					String documentCreated = message;
					
				
				
				
					// Redirect to the Gestione Contenuti and notify the failure creating folder
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println(message);
					return;
				}
				else
					message ="Bad insertion input";
			
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(message);
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



