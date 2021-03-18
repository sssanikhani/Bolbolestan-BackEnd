package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.logic.DataBase;
import models.logic.Handlers;

@WebServlet(name = "PlanServlet", value = "/plan")
public class Plan extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (DataBase.getLoggedInUserId() == null) {
			response.sendRedirect(request.getContextPath() + "/login");
		} else {
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/plan.jsp");
			HashMap<String, Object> student = Handlers.getStudentData(
				DataBase.getLoggedInUserId()
			);
			ArrayList<HashMap<String, Object>> submittedOfferings = (ArrayList<HashMap<String, Object>>) student.get(
				"lastPlan"
			);
			request.setAttribute("courses", submittedOfferings);
			request.setAttribute("student", student);
			requestDispatcher.forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {}
}
