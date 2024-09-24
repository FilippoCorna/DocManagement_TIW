package it.polimi.tiw.Project3_RIA.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.Project3_RIA.beans.User;

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   

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
			String path ="PublicPage.html";
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
			
		}
		else {
			String path = "Home.html";
			response.sendRedirect(path);
			
		}
		
	
	}

	

}
