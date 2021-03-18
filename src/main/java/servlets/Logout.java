package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.logic.DataBase;

@WebServlet(name = "LogoutServlet", value = "/logout")
public class Logout extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		//TODO erase student form dataBase
		// String studentId = (String) request.getAttribute("studentId");
		// call remove function
		DataBase.setLoggedInUserId(null);
		response.sendRedirect(request.getContextPath() + "/login");
	}
}
