package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.responses.Responses;
import models.logic.DataBase;
import models.statics.Exceptions;
import models.utils.Utils;

@WebServlet(name = "PlanServlet", value = "/plan")
public class Plan extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.sendRedirect(request.getContextPath() + "/login");
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

		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/plan.jsp");
		ArrayList<HashMap<String, Object>> submittedOfferings = (ArrayList<HashMap<String, Object>>) student.get(
			"lastPlan"
		);

		HashMap<String, Object> plan = new HashMap<>();
		String[] weekDays = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday" };
		String[] times = {
			"7:30-9:00",
			"9:00-10:30",
			"10:30-12:00",
			"14:00-15:30",
			"16:00-17:30",
		};
		for (String day : weekDays) {
			plan.put(day, new HashMap<String, String>());
			HashMap<String, String> dayMap = (HashMap<String, String>) plan.get(day);
			for (String time : times) {
				dayMap.put(time, null);
			}
		}

		for (HashMap<String, Object> c : submittedOfferings) {
			HashMap<String, Object> classTime = (HashMap<String, Object>) c.get("classTime");
			ArrayList<String> days = (ArrayList<String>) classTime.get("days");
			String time = (String) classTime.get("time");
			for (String d : days) {
				HashMap<String, String> dayMap = (HashMap<String, String>) plan.get(d);
				dayMap.put(time, (String) c.get("name"));
			}
		}

		request.setAttribute("plan", plan);
		request.setAttribute("student", student);
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {}
}
