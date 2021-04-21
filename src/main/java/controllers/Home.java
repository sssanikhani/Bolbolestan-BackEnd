package controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.logic.DataBase;

@WebServlet(name = "HomeServlet", value = "")
public class Home extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.sendRedirect(request.getContextPath() + "/login");
		} else {
			request.setAttribute("studentId", DataBase.AuthManager.getLoggedInUser().getId());
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/home.jsp");
			requestDispatcher.forward(request, response);
		}
	}
}
