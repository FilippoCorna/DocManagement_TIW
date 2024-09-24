package it.polimi.tiw.Project3_RIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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

import it.polimi.tiw.Project3_RIA.beans.Document;
import it.polimi.tiw.Project3_RIA.beans.Subfolder;
import it.polimi.tiw.Project3_RIA.beans.User;
import it.polimi.tiw.Project3_RIA.dao.FolderDAO;
import it.polimi.tiw.Project3_RIA.dao.SubfolderDAO;
import it.polimi.tiw.Project3_RIA.dao.DocumentDAO;



@WebServlet("/DeleteDocument")

public class DeleteDocument extends HttpServlet{
	
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
		
		String documentId = request.getParameter("documentId");
		String subfolderId = request.getParameter("subfolderId");
		
		//Back to Public Page to login
			if(user == null || session == null) {
				response.sendRedirect(getServletContext().getContextPath());
				return;
			}
		
		int id;
		try {
			id = Integer.parseInt(documentId);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Parametri non corretti");
			return;
		}
		
		//check if user has this document
		DocumentDAO documentDAO = new DocumentDAO(connection);
		boolean correctOperation = false;
		try {
			correctOperation = documentDAO.checkUserDocument(user.getId(), id);
			if(!correctOperation) {
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
		
		//delete document
		try {
			documentDAO.deleteDocumentById(id);
			
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("Documento cancellato");
			return;
		    
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Accesso al database fallito");
			return;
		}
		
	}

}
