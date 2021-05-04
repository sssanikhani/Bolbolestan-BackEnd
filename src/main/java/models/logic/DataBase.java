package models.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.entities.Course;
import models.entities.Grade;
import models.entities.Offering;
import models.entities.Student;
import models.entities.Term;
import models.serializers.GradeSerializer;
import models.serializers.OfferingSerializer;
import models.statics.Exceptions;
import models.utils.Utils;

public class DataBase {

	private static String externalServerUrl = "http://138.197.181.131:5100/api/";

	public static class AuthManager {

		private static Student loggedInUser = null;

		public static Student getLoggedInUser() {
			return loggedInUser;
		}

		public static void login(String id) throws Exceptions.StudentNotFound {
			loggedInUser = StudentManager.get(id);
		}

		public static void logout() {
			loggedInUser = null;
		}

		public static boolean isLoggedIn() {
			return loggedInUser != null;
		}
	}

	public static class OfferingManager {

		private static HashMap<String, HashMap<String, Offering>> codeOfferingsMap = new HashMap<>();
		private static String retrieveAllUrl = externalServerUrl + "courses";

		private static String insertQuery =
			"INSERT INTO Offering" +
				" (course_code, class_code, instructor, capacity)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			"ON DUPLICATE KEY UPDATE" +
			" instructor=?, capacity=?;";
		private static String insertRegisteredStudentIDs = 
			"INSERT INTO RegisteredStudents" +
				" (course_code, class_code, student_id)" +
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" course_code=course_code;";
		private static String insertWaitingStudentIDs =
			"INSERT INTO WaitingStudents" +
				" (course_code, class_code, student_id, insert_time)" +
			" VALUES" +
				" (?, ?, ?, NOW())" +
			" ON DUPLICATE KEY UPDATE" +
				" course_code=course_code;";

		private static String removeStudentsQuery =
			"DELETE" +
			" FROM ?" +
			" WHERE course_code=? AND class_code=?;";

		private static String selectQuery = 
			"SELECT *" +
			" FROM Offering O" +
			" WHERE O.course_code = ? AND O.class_code = ?;";

		private static String selectClassTime =
			"SELECT OT.time, OD.day" +
			" FROM OfferingClassTime OT, OfferingDays OD" +
			" WHERE OT.course_code = OD.course_code" +
			" AND OT.class_code = OD.class_code" +
			" AND OT.course_code = ? AND OT.class_code = ?";
		
		private static String selectRegisteredStudentIDs =
			"SELECT R.student_id" +
			" FROM RegisteredStudents R" +
			" WHERE R.course_code = ? AND R.class_code = ?;";
		
		private static String selectWaitingStudentIDs =
			"SELECT W.student_id" +
			" FROM WaitingStudents W" +
			" WHERE W.course_code = ? AND W.class_code = ?" +
			" ORDER BY W.insert_time ASC;";
		
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
				codeOfferingsMap.putIfAbsent(code, new HashMap<>());
				codeOfferingsMap.get(code).put(classCode, o);
			}
		}

		public static void initUpdate() {
			ArrayList<Offering> list = new ArrayList<>();
			for (HashMap<String, Offering> group : codeOfferingsMap.values()) {
				list.addAll(group.values());
			}
			bulkUpdate(list);
		}

		public static void bulkUpdate(ArrayList<Offering> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement stm = con.prepareStatement(insertQuery);
				for (Offering o : list) {
					stm.setString(1, o.getCourse().getCode());
					stm.setString(2, o.getClassCode());
					stm.setString(3, o.getInstructor());
					stm.setInt(4, o.getCapacity());
					stm.setString(5, o.getInstructor());
					stm.setInt(6, o.getCapacity());
					stm.addBatch();
				}
				stm.executeBatch();
				con.commit();
				stm.close();
				con.close();
				OfferingClassTimeManager.bulkUpdate(list);
				OfferingExamTimeManager.bulkUpdate(list);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static void updateStudents(Offering o) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement removeStudentsStm = con.prepareStatement(removeStudentsQuery);
				PreparedStatement addWaitingStudentStm = con.prepareStatement(insertWaitingStudentIDs);
				PreparedStatement addRegisteredStudentStm = con.prepareStatement(insertRegisteredStudentIDs);
				removeStudentsStm.setString(2, o.getCourse().getCode());
				removeStudentsStm.setString(3, o.getClassCode());
				removeStudentsStm.setString(1, "WaitingStudents");
				removeStudentsStm.addBatch();
				removeStudentsStm.setString(1, "RegisteredStudents");
				removeStudentsStm.addBatch();
				for (String studentId : o.getWaitingStudents()) {
					addWaitingStudentStm.setString(1, o.getCourse().getCode());
					addWaitingStudentStm.setString(2, o.getClassCode());
					addWaitingStudentStm.setString(3, studentId);
					addWaitingStudentStm.addBatch();
				}
				for (String studentId : o.getWaitingStudents()) {
					addRegisteredStudentStm.setString(1, o.getCourse().getCode());
					addRegisteredStudentStm.setString(2, o.getClassCode());
					addRegisteredStudentStm.setString(3, studentId);
					addRegisteredStudentStm.addBatch();
				}
				removeStudentsStm.executeBatch();
				addWaitingStudentStm.executeBatch();
				addRegisteredStudentStm.executeBatch();
				con.commit();
				removeStudentsStm.close();
				addWaitingStudentStm.close();
				addRegisteredStudentStm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// TODO: Change
		public static ArrayList<Offering> getAll() {
			ArrayList<Offering> list = new ArrayList<>();
			for (HashMap<String, Offering> group : codeOfferingsMap.values()) {
				list.addAll(group.values());
			}
			return list;
		}

		// TODO: Change
		public static ArrayList<Offering> search(String query) {
			ArrayList<Offering> filtered = new ArrayList<>();
			for (Offering o : getAll()) {
				String name = o.getCourse().getName();
				if (name.contains(query)) filtered.add(o);
			}
			return filtered;
		}

		public static ArrayList<Offering> getCodeOfferings(String code)
			throws Exceptions.offeringNotFound {
			HashMap<String, Offering> codeMap = codeOfferingsMap.get(code);
			if (codeMap == null) throw new Exceptions.offeringNotFound();
			return new ArrayList<>(codeMap.values());
		}

		// TODO
		public static Offering get(String code, String classCode)
			throws Exceptions.offeringNotFound {
			HashMap<String, Offering> group = codeOfferingsMap.get(code);
			if (group == null) throw new Exceptions.offeringNotFound();
			Offering offering = group.get(classCode);
			if (offering == null) throw new Exceptions.offeringNotFound();

			return offering;
		}

		public static ArrayList<Offering> getIfIn(ArrayList<String[]> codes) {
			ArrayList<Offering> result = new ArrayList<>();
			// TODO
			return result;
		}
	}

	public static class StudentManager {

		private static HashMap<String, Student> students = new HashMap<>();
		static String retrieveAllUrl = externalServerUrl + "students";
		static String retrieveGradesUrl = externalServerUrl + "grades";

		private static String insertQuery =
			"INSERT INTO Student" +
				" (id, name, second_name, email, password, birth_date, field, faculty, level, status, image)" +
			" VALUES" +
				" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" name=?, second_name=?, email=?, password=?, birth_date=?, field=?, faculty=?, level=?, status=?, image=?;";
		private static String getQuery =
			"SELECT *" +
			" FROM Student S" +
			" WHERE S.id=?";

		private static String removeGradesQuery =
			"DELETE" + 
			" FROM Grade" +
			" WHERE student_id=?;";
		
		private static String removeTermsQuery =
			"DELETE" +
			" FROM Term" +
			" WHERE student_id=?;";

		private static String insertTermQuery =
			"INSERT INTO Term" +
				" (student_id, term)" +
			" VALUES" +
				" (?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" student_id=student_id;";

		private static String insertGradeQuery =
			"INSERT INTO Grade" +
				" (student_id, course_code, term, grade)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" grade=?;";

		private static String removeOfferingsQuery =
			"DELETE FROM ?" +
			" WHERE student_id=?;";

		private static String insertChosenOfferingQuery =
			"INSERT INTO StudentChosenOfferings" +
				" (student_id, course_code, class_code)" +
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" student_id=student_id;";
		private static String getChosenOfferingQuery =
			"SELECT *" +
			" FROM StudentChosenOfferings" +
			" WHERE student_id=?;";
		
		private static String insertSubmittedOfferingQuery =
			"INSERT INTO StudentLastPlan" +
				" (student_id, course_code, class_code)" +
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" student_id=student_id;";
		private static String getSubmittedOfferingQuery =
			"SELECT *" +
			" FROM StudentChosenOfferings" +
			" WHERE student_id=?;";

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

		public static void initUpdate() {
			ArrayList<Student> list = new ArrayList<>(students.values());
			bulkUpdate(list);
		}

		public static void bulkUpdate(ArrayList<Student> list) {
			Connection con;
			PreparedStatement stm;
			try {
				con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				stm = con.prepareStatement(insertQuery);
				for (Student s : list) {
					stm.setString(1, s.getId());
					stm.setString(2, s.getName());
					stm.setString(3, s.getSecondName());
					stm.setString(4, s.getEmail());
					stm.setString(5, s.getPassword());
					stm.setString(6, s.getBirthDate());
					stm.setString(7, s.getField());
					stm.setString(8, s.getFaculty());
					stm.setString(9, s.getLevel());
					stm.setString(10, s.getStatus());
					stm.setString(11, s.getImg());
					stm.setString(12, s.getName());
					stm.setString(13, s.getSecondName());
					stm.setString(14, s.getEmail());
					stm.setString(15, s.getPassword());
					stm.setString(16, s.getBirthDate());
					stm.setString(17, s.getField());
					stm.setString(18, s.getFaculty());
					stm.setString(19, s.getLevel());
					stm.setString(20, s.getStatus());
					stm.setString(21, s.getImg());
					stm.addBatch();
				}
				stm.executeBatch();
				con.commit();
				stm.close();
				con.close();
				updateTerms(list);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static void updateTerms(ArrayList<Student> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement removeGradeStm = con.prepareStatement(removeGradesQuery);
				PreparedStatement removeTermStm = con.prepareStatement(removeTermsQuery);
				PreparedStatement termStm = con.prepareStatement(insertTermQuery);
				PreparedStatement gradeStm = con.prepareStatement(insertGradeQuery);
				for (Student s : list) {
					removeGradeStm.setString(1, s.getId());
					removeGradeStm.addBatch();
					removeTermStm.setString(1, s.getId());
					removeTermStm.addBatch();
					for (Term t : s.getTermsReport()) {
						termStm.setString(1, s.getId());
						termStm.setInt(2, t.getTerm());
						termStm.addBatch();
						for (Grade g : t.getGrades()) {
							gradeStm.setString(1, s.getId());
							gradeStm.setString(2, g.getCourse().getCode());
							gradeStm.setInt(3, g.getTerm());
							gradeStm.setFloat(4, g.getGrade());
							gradeStm.setFloat(5, g.getGrade());
							gradeStm.addBatch();
						}
					}
				}
				removeGradeStm.executeBatch();
				removeTermStm.executeBatch();
				termStm.executeBatch();
				gradeStm.executeBatch();
				con.commit();
				removeGradeStm.close();
				removeTermStm.close();
				termStm.close();
				gradeStm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static void updateOfferings(Student s) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement removeStm = con.prepareStatement(removeOfferingsQuery);
				PreparedStatement addChosenStm = con.prepareStatement(insertChosenOfferingQuery);
				PreparedStatement addLastStm = con.prepareStatement(insertSubmittedOfferingQuery);
				removeStm.setString(2, s.getId());
				removeStm.setString(1, "StudentChosenOfferings");
				removeStm.addBatch();
				removeStm.setString(1, "StudentLastPlan");
				removeStm.addBatch();
				for (Offering o : s.getChosenOfferings()) {
					addChosenStm.setString(1, s.getId());
					addChosenStm.setString(2, o.getCourse().getCode());
					addChosenStm.setString(3, o.getClassCode());
					addChosenStm.addBatch();
				}
				for (Offering o : s.getLastPlan()) {
					addLastStm.setString(1, s.getId());
					addLastStm.setString(2, o.getCourse().getCode());
					addLastStm.setString(3, o.getClassCode());
					addLastStm.addBatch();
				}
				removeStm.executeBatch();
				addChosenStm.executeBatch();
				addLastStm.executeBatch();
				con.commit();
				removeStm.close();
				addChosenStm.close();
				addLastStm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static Student get(String studentId) throws Exceptions.StudentNotFound {
			Student student = new Student();
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(getQuery);
				stm.setString(1, studentId);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					student.setId(rs.getString("id"));
					student.setName(rs.getString("name"));
					student.setSecondName(rs.getString("second_name"));
					student.setEmail(rs.getString("email"));
					student.setPassword(rs.getString("password"));
					student.setBirthDate(rs.getString("birth_date"));
					student.setField(rs.getString("field"));
					student.setFaculty(rs.getString("faculty"));
					student.setLevel(rs.getString("level"));
					student.setStatus(rs.getString("status"));
				} else {
					throw new Exceptions.StudentNotFound();
				}
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			retrieveGrades(student);
			retrieveOfferings(student);
			return student;
		}

		public static void retrieveGrades(Student s) {
			ArrayList<Grade> grades = GradeManager.get(s.getId());
			for (Grade g : grades) {
				s.addGrade(g);
			}
		}

		public static void retrieveOfferings(Student s) {
			ArrayList<String[]> chosenCodes = new ArrayList<>();
			ArrayList<String[]> lastCodes = new ArrayList<>();
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement chosenStm = con.prepareStatement(getChosenOfferingQuery);
				PreparedStatement lastStm = con.prepareStatement(getSubmittedOfferingQuery);
				chosenStm.setString(1, s.getId());
				ResultSet chosenRS = chosenStm.executeQuery();
				while (chosenRS.next()) {
					String code = chosenRS.getString("course_code");
					String classCode = chosenRS.getString("class_code");
					String[] codes = {code, classCode};
					chosenCodes.add(codes);
				}
				lastStm.setString(1, s.getId());
				ResultSet lastRS = lastStm.executeQuery();
				while (lastRS.next()) {
					String code = lastRS.getString("course_code");
					String classCode = lastRS.getString("class_code");
					String[] codes = {code, classCode};
					lastCodes.add(codes);
				}
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			ArrayList<Offering> chosen = OfferingManager.getIfIn(chosenCodes);
			ArrayList<Offering> last = OfferingManager.getIfIn(lastCodes);
			HashMap<String, Offering> chosenMap = new HashMap<>();
			HashMap<String, Offering> lastMap = new HashMap<>();
			for (Offering o : chosen) {
				chosenMap.put(o.getCourse().getCode(), o);
			}
			for (Offering o : last) {
				lastMap.put(o.getCourse().getCode(), o);
			}
			s._setChosenOfferings(chosenMap);
			s._setLastPlan(lastMap);
		}

		public static ArrayList<Grade> getAllGradesFromExternalServer(String studentId)
			throws IOException, InterruptedException, Exceptions.StudentNotFound {
			Student student = students.get(studentId);
			if (student == null) throw new Exceptions.StudentNotFound();

			String url = retrieveGradesUrl + "/" + studentId;
			HashMap<String, Object> webRes = Utils.sendRequest("GET", url, null, null);
			String data = (String) webRes.get("data");

			ArrayList<Grade> grades = GradeSerializer.deserializeList(data);
			return grades;
		}
	}

	public static class CourseManager {

		private static HashMap<String, Course> courses = new HashMap<>();

		private static String insertQuery =
			"INSERT INTO Course" +
				" (code, name, type, units)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" name=?, type=?, units=?;";

		private static String insertPrerequisite =
			"INSERT INTO Prerequisites" +
				" (code, precode)" +
			" VALUES" +
				" (?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" code=code;";

		// TODO
		public static Course get(String code) {
			return courses.get(code);
		}

		public static void initUpdate() {
			ArrayList<Course> list = new ArrayList<>(courses.values());
			bulkUpdate(list);
		}

		public static void bulkUpdate(ArrayList<Course> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement stm = con.prepareStatement(insertQuery);
				for (Course c : list) {
					stm.setString(1, c.getCode());
					stm.setString(2, c.getName());
					stm.setString(3, c.getType());
					stm.setInt(4, c.getUnits());
					stm.setString(5, c.getName());
					stm.setString(6, c.getType());
					stm.setInt(7, c.getUnits());
					stm.addBatch();
				}
				stm.executeBatch();
				con.commit();
				stm.close();
				con.close();
				updatePrerequisites(list);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static void updatePrerequisites(ArrayList<Course> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement stm = con.prepareStatement(insertPrerequisite);
				for (Course c : list) {
					for (Course pre : c.getPrerequisites()) {
						stm.setString(1, c.getCode());
						stm.setString(2, pre.getCode());
						stm.addBatch();
					}
				}
				stm.executeBatch();
				con.commit();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static Course getOrCreateM(String code) {
			Course course = courses.get(code);
			if (course == null) {
				course = new Course(code);
				courses.put(code, course);
			}

			return course;
		}

		public static void updateOrCreateM(String code, String name, String type, int units) {
			Course course = getOrCreateM(code);
			course.setName(name);
			course.setType(type);
			course.setUnits(units);
		}
	}

	public static class OfferingClassTimeManager {
		private static String insertQuery =
			"INSERT INTO OfferingClassTime" +
				" (course_code, class_code, time)" + 
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" time=?;";
		private static String insertTimeDayQuery =
			"INSERT INTO OfferingDays" +
				" (course_code, class_code, day)" +
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" day=day;";

		public static void bulkUpdate(ArrayList<Offering> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement timestm = con.prepareStatement(insertQuery);
				PreparedStatement daystm = con.prepareStatement(insertTimeDayQuery);
				for (Offering o : list) {
					timestm.setString(1, o.getCourse().getCode());
					timestm.setString(2, o.getClassCode());
					timestm.setString(3, o.getClassTime().getTime());
					timestm.setString(4, o.getClassTime().getTime());
					timestm.addBatch();
					for (String day : o.getClassTime().getDays()) {
						daystm.setString(1, o.getCourse().getCode());
						daystm.setString(2, o.getClassCode());
						daystm.setString(3, day);
						daystm.addBatch();
					}
				}
				timestm.executeBatch();
				daystm.executeBatch();
				con.commit();
				timestm.close();
				daystm.close();
				con.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static class OfferingExamTimeManager {
		private static String insertQuery =
			"INSERT INTO OfferingExamTime" +
				" (course_code, class_code, start, end)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" start=?, end=?;";

		public static void bulkUpdate(ArrayList<Offering> list) {
			try {
				Connection con = ConnectionPool.getConnection();
				con.setAutoCommit(false);
				PreparedStatement stm = con.prepareStatement(insertQuery);
				for (Offering o : list) {
					stm.setString(1, o.getCourse().getCode());
					stm.setString(2, o.getClassCode());
					stm.setString(3, o.getExamTime().getStart());
					stm.setString(4, o.getExamTime().getEnd());
					stm.setString(5, o.getExamTime().getStart());
					stm.setString(6, o.getExamTime().getEnd());
					stm.addBatch();
				}
				stm.executeBatch();
				con.commit();
				stm.close();
				con.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static class GradeManager {
		private static String getQuery =
			"SELECT *" +
			" FROM Grade" +
			" WHERE student_id=?;";
		public static ArrayList<Grade> get(String studentId) {
			ArrayList<Grade> result = new ArrayList<>();
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(getQuery);
				stm.setString(1, studentId);
				ResultSet rs = stm.executeQuery();
				while (rs.next()) {
					Grade g = new Grade();
					g.setCourse(CourseManager.get(rs.getString("course_code")));
					g.setGrade(rs.getFloat("grade"));
					g.setTerm(rs.getInt("term"));
					result.add(g);
				}
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return result;
		}
	}

	public static void updateLocalDataBase() {
		CourseManager.initUpdate();
		OfferingManager.initUpdate();
		StudentManager.initUpdate();
	}
}
