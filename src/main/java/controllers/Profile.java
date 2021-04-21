package controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.logic.DataBase;
import models.statics.Exceptions;
import models.statics.Responses;
import models.utils.Utils;

@WebServlet(name = "ProfileServlet", value = "/profile")
public class Profile extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.sendRedirect("/login");
			return;
		}

		HashMap<String, Object> student;
		try {
			student = Utils.getStudentData(DataBase.AuthManager.getLoggedInUser().getId());
		} catch (Exceptions.StudentNotFound e) {
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
			request.setAttribute("result", Responses.StudentNotFound);
			requestDispatcher.forward(request, response);
			return;
		}
		request.setAttribute("student", student);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/profile.jsp");
		requestDispatcher.forward(request, response);
	}
}
