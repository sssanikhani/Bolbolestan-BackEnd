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

	private static Handlers instance;

	private Handlers() {}

	public static Handlers getInstance() {
		if (instance == null) instance = new Handlers();
		return instance;
	}

	private static ObjectMapper mapper = new ObjectMapper();

	public static void startup() {
        try {
			System.out.println("Trying to retrieve data from external DataBase...");
			DataBase.OfferingManager.updateFromExternalServer();
			DataBase.StudentManager.updateFromExternalServer();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: failed to connect with external server");
		}
	}

	public HashMap<String, Object> courses() {
		HashMap<String, Object> response;

		ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
		ArrayList<Offering> offeringsList = new ArrayList<>();
		try {
			offeringsList = DataBase.OfferingManager.getAll();
		} catch (Exception e) {
			// TODO
		}

		for (Offering o : offeringsList) {
			HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
			offeringsDataList.add(oData);
		}

		response = new HashMap<String, Object>();
		response.put("courses", offeringsDataList);
		return response;
	}

	public static HashMap<String, Object> studentProfile(String studentId) {
		HashMap<String, Object> response;

		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			//TODO
		}
		response = mapper.convertValue(student, HashMap.class);
		return response;
	}

	public static HashMap<String, Object> singleCourse(String code, String classCode) {
		HashMap<String, Object> response;

		Offering offering = null;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			// TODO
		}
		response = mapper.convertValue(offering, HashMap.class);
		return response;
	}

	public HashMap<String, Object> getStudentData(String studentId) {
		HashMap<String, Object> response;

		Student student = new Student();
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			// TODO
		}

		response = mapper.convertValue(student, HashMap.class);
		return response;
	}

	public static HashMap<String, Object> plan(String studentId) {
		HashMap<String, Object> response;

		Student student = null;
		try {
			student = DataBase.StudentManager.get(studentId);
		} catch (Exceptions.StudentNotFound e) {
			// TODO
		}

		ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
		for (Offering o : student.getLastPlan()) {
			HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
			offeringsDataList.add(oData);
		}

		response = new HashMap<String, Object>();
		response.put("courses", offeringsDataList);
		return response;
	}

	public static HashMap<String, Object> submit(String studentId) {
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

	public static void okSubmit() {
		// TODO
	}

	public static void failSubmit() {
		//TODO
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

	public HashMap<String, Object> reset(String studentId) {
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

	public HashMap<String, Object> search(String s) {
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
