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

import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.responses.Responses;
import models.entities.Offering;
import models.entities.Student;
import models.logic.DataBase;
import models.statics.Constants;
import models.statics.Exceptions;
import models.utils.Utils;

@WebServlet(name = "CoursesServlet", value = "/courses")
public class Courses extends HttpServlet {

	ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.sendRedirect("/login");
			return;
		}
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/courses.jsp");
		HashMap<String, Object> student = null;
		try {
			student = Utils.getStudentData(DataBase.AuthManager.getLoggedInUser().getId());
		} catch (Exceptions.StudentNotFound e) {
			responseError(request, response, Responses.StudentNotFound);
			return;
		}
		ArrayList<HashMap<String, Object>> courses = searchCourses("");
		request.setAttribute("courses", courses);
		request.setAttribute("searchBox", "");
		request.setAttribute("student", student);
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String action = request.getParameter("action");
		String studentId = DataBase.AuthManager.getLoggedInUser().getId();
		RequestDispatcher requestDispatcher;
		HashMap<String, Object> result;
		switch (action) {
			case "remove":
				removeCourse(request, response, studentId);
				return;
			case "submit":
				submitPlan(request, response, studentId);
				return;
			case "reset":
				resetPlan(request, response, studentId);
				return;
			case "search":
				response.sendRedirect("/courses");
				return;
			case "clear":
				response.sendRedirect("/courses");
				return;
			case "add":
				addCourse(request, response, studentId);
				return;
			case "plan":
				response.sendRedirect("/plan");
				return;
			default:
				requestDispatcher = request.getRequestDispatcher("/courses.jsp");
				requestDispatcher.forward(request, response);
		}
	}

	public void responseError(
		HttpServletRequest request,
		HttpServletResponse response,
		HashMap<String, Object> data
	)
		throws IOException, ServletException {
		request.setAttribute("result", data);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
		requestDispatcher.forward(request, response);
	}

	public ArrayList<HashMap<String, Object>> searchCourses(String s) {
		ArrayList<HashMap<String, Object>> matchedOfferings = new ArrayList<>();

		ArrayList<Offering> allOfferings = DataBase.OfferingManager.getAll();
		for (Offering o : allOfferings) {
			String courseName = o.getCourse().getName();
			if (courseName.contains(s)) {
				HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
				matchedOfferings.add(oData);
			}
		}
		return matchedOfferings;
	}

	public void resetPlan(
		HttpServletRequest request,
		HttpServletResponse response,
		String studentId
	)
		throws IOException, ServletException {
		Student student;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			responseError(request, response, Responses.StudentNotFound);
			return;
		}
		student.resetPlan();
		response.sendRedirect("/courses");
	}

	public void removeCourse(
		HttpServletRequest request,
		HttpServletResponse response,
		String studentId
	)
		throws IOException, ServletException {
		String code = request.getParameter("code");
		String classCode = request.getParameter("classCode");

		Offering offering;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			responseError(request, response, Responses.OfferingNotFound);
			return;
		}

		Student student;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			responseError(request, response, Responses.StudentNotFound);
			return;
		}

		try {
			student.removeOfferingFromList(offering.getCourse().getCode());
		} catch (Exceptions.offeringNotFound e) {
			responseError(request, response, Responses.OfferingNotFound);
			return;
		}

		response.sendRedirect("/courses");
	}

	public void submitPlan(
		HttpServletRequest request,
		HttpServletResponse response,
		String studentId
	)
		throws IOException, ServletException {
		Student student;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			responseError(request, response, Responses.StudentNotFound);
			return;
		}

		int numUnits = student.getNumberChosenUnits();
		if (numUnits < Constants.MIN_ALLOWED_UNITS || numUnits > Constants.MAX_ALLOWED_UNITS) {
			responseError(request, response, Responses.MaxMinUnits);
			return;
		}

		student.finalizeOfferings();

		response.sendRedirect("/plan");
	}

	public void addCourse(
		HttpServletRequest request,
		HttpServletResponse response,
		String studentId
	)
		throws IOException, ServletException {
		String code = request.getParameter("code");
		String classCode = request.getParameter("classCode");

		Offering offering;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			responseError(request, response, Responses.OfferingNotFound);
			return;
		}

		Student student;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			responseError(request, response, Responses.StudentNotFound);
			return;
		}

		boolean hasPassedPrerequisites;
		try {
			hasPassedPrerequisites = student.hasPassedPrerequisites(code);
		} catch (Exceptions.offeringNotFound e) {
			responseError(request, response, Responses.InternalServerError);
			return;
		}

		if (!hasPassedPrerequisites) {
			responseError(request, response, Responses.NotPassedPrerequisites);
			return;
		}

		if (student.hasPassed(offering.getCourse().getCode())) {
			responseError(request, response, Responses.CoursePassedBefore);
			return;
		}

		ArrayList<Offering> chosenOfferings = student.getChosenOfferings();
		for (Offering o : chosenOfferings) {
			if (offering.hasOfferingTimeCollision(o)) {
				responseError(request, response, Responses.CourseTimeCollision);
				return;
			}
			boolean hasExamTimeCollision = offering.hasExamTimeCollision(o);
			if (hasExamTimeCollision) {
				responseError(request, response, Responses.ExamTimeCollision);
				return;
			}
		}

		student.addOfferingToList(offering);

		response.sendRedirect("/courses");
	}
}
