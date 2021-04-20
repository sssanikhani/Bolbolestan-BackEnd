package models.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.entities.Course;
import models.entities.Grade;
import models.entities.Offering;
import models.entities.Student;
import models.serializers.OfferingSerializer;
import models.statics.Exceptions;
import models.utils.Utils;

public class DataBase {

	private static String externalServerUrl = "http://138.197.181.131:5100/api/";

	private static String loggedInUserId = null;
	private static String lastSearchFilter = "";

	public static String getLastSearchFilter() {
		return lastSearchFilter;
	}

	public static void setLastSearchFilter(String _lastSearchFilter) {
		lastSearchFilter = _lastSearchFilter;
	}

	public static String getLoggedInUserId() {
		return loggedInUserId;
	}

	public static void setLoggedInUserId(String _loggedInUserId) {
		loggedInUserId = _loggedInUserId;
	}

	public static class OfferingManager {

		private static HashMap<String, HashMap<String, Offering>> codeOfferingsMap = new HashMap<>();

		static String retrieveAllUrl = externalServerUrl + "courses";

		public static void updateFromExternalServer()
			throws IOException, InterruptedException {
			HashMap<String, Object> webRes = Utils.sendRequest(
				"GET",
				retrieveAllUrl,
				null,
				null
			);
			String data = (String) webRes.get("data");

			ArrayList<Offering> list = OfferingSerializer.deserializeList(data);

			codeOfferingsMap.clear();
			for (Offering o : list) {
				String code = o.getCourse().getCode();
				String classCode = o.getClassCode();
				codeOfferingsMap.computeIfAbsent(code, k -> new HashMap<>());
				codeOfferingsMap.get(code).put(classCode, o);
			}
		}

		public static ArrayList<Offering> getAll() {
			ArrayList<Offering> list = new ArrayList<>();
			for (HashMap<String, Offering> group : codeOfferingsMap.values()) {
				list.addAll(group.values());
			}
			return list;
		}

		public static ArrayList<Offering> getCodeOfferings(String code)
			throws Exceptions.offeringNotFound {
			HashMap<String, Offering> codeMap = codeOfferingsMap.get(code);
			if (codeMap == null) throw new Exceptions.offeringNotFound();
			return new ArrayList<>(codeMap.values());
		}

		public static Offering get(String code, String classCode)
			throws Exceptions.offeringNotFound {
			HashMap<String, Offering> group = codeOfferingsMap.get(code);
			if (group == null) throw new Exceptions.offeringNotFound();
			Offering offering = group.get(classCode);
			if (offering == null) throw new Exceptions.offeringNotFound();

			return offering;
		}
	}

	public static class StudentManager {

		private static HashMap<String, Student> students = new HashMap<>();

		static String retrieveAllUrl = externalServerUrl + "students";
		static String retrieveGradesUrl = externalServerUrl + "grades";

		public static void updateFromExternalServer()
			throws IOException, InterruptedException, Exceptions.StudentNotFound {
			HashMap<String, Object> webRes = Utils.sendRequest(
				"GET",
				retrieveAllUrl,
				null,
				null
			);
			String data = (String) webRes.get("data");

			ObjectMapper mapper = new ObjectMapper();
			ArrayList<Student> list = mapper.readValue(data, new TypeReference<>() {});

			students.clear();
			for (Student s : list) {
				String id = s.getId();
				students.put(id, s);
				ArrayList<Grade> grades = getAllGradesFromExternalServer(id);
				for (Grade g : grades) {
					s.addGrade(g);
				}
			}
		}

		public static ArrayList<Student> getAll() {
			return new ArrayList<>(students.values());
		}

		public static boolean exists(String studentId) {
			Student student = students.get(studentId);
			return student != null;
		}

		public static Student get(String studentId) throws Exceptions.StudentNotFound {
			Student student = students.get(studentId);
			if (student == null) throw new Exceptions.StudentNotFound();
			return student;
		}

		public static ArrayList<Grade> getAllGradesFromExternalServer(String studentId)
			throws IOException, InterruptedException, Exceptions.StudentNotFound {
			Student student = students.get(studentId);
			if (student == null) throw new Exceptions.StudentNotFound();

			String url = retrieveGradesUrl + "/" + studentId;
			HashMap<String, Object> webRes = Utils.sendRequest("GET", url, null, null);
			String data = (String) webRes.get("data");

			ObjectMapper mapper = new ObjectMapper();

			return mapper.readValue(data, new TypeReference<>() {});
		}
	}

	public static class CourseManager {
		private static HashMap<String, Course> courses = new HashMap<>();

		public static Course get(String code) {
			return courses.get(code);
		}

		public static Course getOrCreate(String code) {
			Course course = courses.get(code);
			if (course == null) {
				course = new Course(code);
				courses.put(code, course);
			}
				
			return course;
		}

		public static void updateOrCreate(String code, String name, String type, int units) {
			Course course = getOrCreate(code);
			course.setName(name);
			course.setType(type);
			course.setUnits(units);
		}
	}
}
