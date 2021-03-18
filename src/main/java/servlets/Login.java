package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.logic.DataBase;

@WebServlet(name = "LoginServlet", value = "/login")
public class Login extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/login.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String studentId = request.getParameter("studentId");
		if (DataBase.StudentManager.exists(studentId)) {
			DataBase.setLoggedInUserId(studentId);
			response.sendRedirect("/");
		} else {
			response.sendRedirect(request.getServletPath());
		}
	}
}
