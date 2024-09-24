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

@WebServlet("/ChooseDestination")
public class ChooseDestination  extends HttpServlet{
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		User user = null;
		if(session != null)
			user = (User) session.getAttribute("currentUser");
		
		//Back to Public Page to login
		if(user == null) {
			response.sendRedirect(getServletContext().getContextPath());
		}
		
		String subfolderIdString = request.getParameter("subfolderId");
		String documentIdString = request.getParameter("documentId");
		//String documentIdString = (String)request.getAttribute("documentId");
		
		Integer subfolderId;
		Integer documentId;
		
		if(subfolderIdString == null || documentIdString == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri Incorretti");
			return;
		}
		
		try {
		subfolderId = Integer.parseInt(subfolderIdString);
		documentId = Integer.parseInt(documentIdString);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri Incorretti");
			return;
		}
		
		DocumentDAO documentDAO = new DocumentDAO(connection);
		SubfolderDAO subDAO = new SubfolderDAO(connection);
		List<Document> docx = null;
		try {
			docx = documentDAO.findDocumentById(documentId);
			boolean check = subDAO.checkUserSubfolder(user.getId(), subfolderId);
			if(docx.get(0).getUser() != user.getId() || !check) {
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
		
		try {
			documentDAO.moveDocument(documentId, subfolderId);
			
			//Redirect to the Home page 
			
	 
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println("Operazione effettuata con successo");
			
		    return;
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Accesso al database fallito");
			return;		}
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}