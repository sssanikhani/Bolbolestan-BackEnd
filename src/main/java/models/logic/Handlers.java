package models.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import models.entities.Offering;
import models.entities.Student;
import models.statics.Constants;
import models.statics.Exceptions;
import models.statics.Responses;

public class Handlers {

	private static ObjectMapper mapper = new ObjectMapper();

	public static HashMap<String, Object> getStudentData(String studentId) {
		HashMap<String, Object> response;

		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			// TODO
		}

		response = mapper.convertValue(student, HashMap.class);
		return response;
	}

	public static HashMap<String, Object> addCourse(
		String studentId,
		String code,
		String classCode
	) {
		HashMap<String, Object> response = null;

		Offering offering = null;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			response = Responses.OfferingNotFound;
			return response;
		}

		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			response = Responses.StudentNotFound;
			return response;
		}

		boolean hasPassedPrerequisites = false;
		try {
			hasPassedPrerequisites = student.hasPassedPrerequisites(offering.getCode());
		} catch (Exception e) {
			response = Responses.OfferingNotFound;
			return response;
		}
		if (!hasPassedPrerequisites) {
			response = Responses.NotPassedPrerequisites;
			return response;
		}

		if(student.hasPassed(offering.getCode())) {
			response = Responses.CoursePassedBefore;
			return response;
		}

		ArrayList<Offering> chosenOfferings = student.getChosenOfferings();
		for (Offering o : chosenOfferings) {
			if (offering.hasOfferingTimeCollision(o)) {
				response = Responses.CourseTimeCollision;
				return response;
			}
			boolean hasExamTimeCollision = offering.hasExamTimeCollision(o);
			if (hasExamTimeCollision) {
				response = Responses.ExamTimeCollision;
				return response;
			}
		}

		student.addOfferingToList(offering);
		return response;
	}

	public static HashMap<String, Object> removeCourse(
		String studentId,
		String code,
		String classCode
	) {
		HashMap<String, Object> response = null;

		Offering offering = null;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			response = Responses.OfferingNotFound;
			return response;
		}

		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			response = Responses.StudentNotFound;
			return response;
			// TODO
		}

		try {
			student.removeOfferingFromList(offering.getCode());
		} catch (Exceptions.offeringNotFound e) {
			// TODO
			response = Responses.OfferingNotFound;
			return response;
		}
		return response;
	}

	public static HashMap<String, Object> submitPlan(String studentId) {
		HashMap<String, Object> response = null;
		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			response = Responses.StudentNotFound;
			return response;
		}

		int numUnits = student.getNumberChosenUnits();
		if (numUnits < Constants.MIN_ALLOWED_UNITS || numUnits > Constants.MAX_ALLOWED_UNITS) {
			response = Responses.MaxMinUnits;
			return response;
		}
		try {
			student.validateOfferingCapacities();
		} catch (Exception e) {
			response = Responses.OfferingCapacity;
			return response;
		}

		student.finalizeOfferings();
		return response;
	}

	public static HashMap<String, Object> reset(String studentId) {
		HashMap<String, Object> response = null;
		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			response = Responses.StudentNotFound;
			return response;
		}
		student.resetPlan();
		return response;
	}

	public static HashMap<String, Object> search(String s) {
		HashMap<String, Object> response = new HashMap<>();
		ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
		ArrayList<Offering> offeringsList = new ArrayList<>();
		try {
			offeringsList = DataBase.OfferingManager.getAll();
		} catch (Exception e) {
			return Responses.InternalServerError;
		}

		for (Offering o : offeringsList) {
			if (o.getName().contains(s)) {
				HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
				offeringsDataList.add(oData);
			}
		}
		response.put("courses", offeringsDataList);
		return response;
	}
}
