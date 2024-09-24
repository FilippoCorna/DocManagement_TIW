package it.polimi.tiw.Project3_RIA.controllers;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.Project3_RIA.beans.Document;
import it.polimi.tiw.Project3_RIA.beans.User;
import it.polimi.tiw.Project3_RIA.dao.DocumentDAO;
import it.polimi.tiw.Project3_RIA.dao.SubfolderDAO;

/**
 * Servlet implementation class ShowDocumentsPage
 */
@WebServlet("/Documents")
public class ShowDocumentsPage extends HttpServlet {
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
	 * Show document in subfolder with chosen subfolderId and name.
	 * Check if currentUser has access at this subfolder. 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri non corretti");
			return;
		}
		
		SubfolderDAO subfolderDAO = new SubfolderDAO(connection);
		
		try {
			
			boolean correctOperation = subfolderDAO.checkUserSubfolder(user.getId(),id);
			if(correctOperation) {
				
				DocumentDAO documentDAO = new DocumentDAO(connection);
				try {
					List<Document> documents = documentDAO.findDocumentsBySubfolder(id);
					
					
					Gson gson = new GsonBuilder()
							   .setDateFormat("yyyy MMM dd").create();
					String json = gson.toJson(documents);
					 
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(json);
					
				    return;
					
				} catch (SQLException e) {
					e.printStackTrace();
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Impossibile recuperare i documenti");
					return;
				}
			}
			
			else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Operazione non permessa");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Accesso al database fallito");
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
