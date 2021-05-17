package models.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.ibatis.jdbc.ScriptRunner;

import models.entities.Course;
import models.entities.Grade;
import models.entities.Offering;
import models.entities.OfferingClassTime;
import models.entities.OfferingExamTime;
import models.entities.Student;
import models.entities.Term;
import models.serializers.GradeSerializer;
import models.serializers.OfferingSerializer;
import models.statics.Exceptions;
import models.utils.Utils;

public class DataBase {

	private static String externalServerUrl = "http://138.197.181.131:5200/api/";

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
			"INSERT INTO bolbolestan.Offering" +
				" (course_code, class_code, instructor, capacity)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			"ON DUPLICATE KEY UPDATE" +
			" instructor=?, capacity=?;";
		private static String insertRegisteredStudentIDs = 
			"INSERT IGNORE INTO bolbolestan.RegisteredStudents" +
				" (course_code, class_code, student_id)" +
			" VALUES" +
				" (?, ?, ?);";
		private static String insertWaitingStudentIDs =
			"INSERT IGNORE INTO bolbolestan.WaitingStudents" +
				" (course_code, class_code, student_id, insert_time)" +
			" VALUES" +
				" (?, ?, ?, NOW());";

		private static String removeStudentsQuery =
			"DELETE" +
			" FROM bolbolestan.%s" +
			" WHERE course_code=? AND class_code=?;";

		private static String selectQuery = 
			"SELECT *" +
			" FROM bolbolestan.Offering O" +
			" WHERE O.course_code = ? AND O.class_code = ?;";

		private static String multipleSelectQuery =
			"SELECT *" +
			" FROM bolbolestan.Offering O" +
			" WHERE (O.course_code, O.class_code) in (%s);";

		private static String selectAllQuery =
			"SELECT *" +
			" FROM bolbolestan.Offering;";
		
		private static String searchQuery =
			"SELECT *" +
			" FROM bolbolestan.Offering O, bolbolestan.Course C"+
			" WHERE O.course_code=C.code AND C.name LIKE '%?%';";
		
		private static String selectRegisteredStudentIDs =
			"SELECT R.student_id" +
			" FROM bolbolestan.RegisteredStudents R" +
			" WHERE R.course_code = ? AND R.class_code = ?;";
		
		private static String selectWaitingStudentIDs =
			"SELECT W.student_id" +
			" FROM bolbolestan.WaitingStudents W" +
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
				String removeWaitingQuery = String.format(removeStudentsQuery, "WaitingStudents");
				String removeRegisteredQuery = String.format(removeStudentsQuery, "RegisteredStudents");
				PreparedStatement removeWaitingStm = con.prepareStatement(removeWaitingQuery);
				PreparedStatement removeRegisteredStm = con.prepareStatement(removeRegisteredQuery);
				PreparedStatement addWaitingStudentStm = con.prepareStatement(insertWaitingStudentIDs);
				PreparedStatement addRegisteredStudentStm = con.prepareStatement(insertRegisteredStudentIDs);
				removeWaitingStm.setString(1, o.getCourse().getCode());
				removeWaitingStm.setString(2, o.getClassCode());
				removeWaitingStm.execute();
				removeRegisteredStm.setString(1, o.getCourse().getCode());
				removeRegisteredStm.setString(2, o.getClassCode());
				removeRegisteredStm.execute();
				con.commit();
				removeWaitingStm.close();
				removeRegisteredStm.close();
				for (String studentId : o.getWaitingStudents()) {
					addWaitingStudentStm.setString(1, o.getCourse().getCode());
					addWaitingStudentStm.setString(2, o.getClassCode());
					addWaitingStudentStm.setString(3, studentId);
					addWaitingStudentStm.addBatch();
				}
				for (String studentId : o.getRegisteredStudents()) {
					addRegisteredStudentStm.setString(1, o.getCourse().getCode());
					addRegisteredStudentStm.setString(2, o.getClassCode());
					addRegisteredStudentStm.setString(3, studentId);
					addRegisteredStudentStm.addBatch();
				}
				addWaitingStudentStm.executeBatch();
				addRegisteredStudentStm.executeBatch();
				con.commit();
				addWaitingStudentStm.close();
				addRegisteredStudentStm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		public static ArrayList<Offering> getAll() {
			ArrayList<Offering> list = new ArrayList<>();
			try {
				Connection con = ConnectionPool.getConnection();
				Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery(selectAllQuery);
				while (rs.next()) {
					Offering o = buildObjectFromResult(rs);
					list.add(o);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return list;
		}

		public static ArrayList<Offering> search(String query) {
			ArrayList<Offering> filtered = new ArrayList<>();
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(searchQuery);
				stm.setString(1, query);
				ResultSet rs = stm.executeQuery();
				while (rs.next()) {
					Offering o = buildObjectFromResult(rs);
					filtered.add(o);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return filtered;
		}

		public static Offering get(String code, String classCode)
			throws Exceptions.offeringNotFound {
			Offering offering = null;
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(selectQuery);
				stm.setString(1, code);
				stm.setString(2, classCode);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					offering = buildObjectFromResult(rs);
				} else {
					throw new Exceptions.offeringNotFound();
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return offering;
		}

		public static ArrayList<Offering> getIfIn(ArrayList<String[]> codes) {
			ArrayList<Offering> result = new ArrayList<>();
			if (codes.isEmpty())
				return result;
			ArrayList<String> queryList = new ArrayList<>();
			for (String[] code : codes) {
				String s = String.format("(%s, %s)", code[0], code[1]);
				queryList.add(s);
			}
			String list = String.join(",", queryList);
			
			try {
				Connection con = ConnectionPool.getConnection();
				Statement stm = con.createStatement();
				String query = String.format(multipleSelectQuery, list);
				ResultSet rs = stm.executeQuery(query);
				while(rs.next()) {
					Offering o = buildObjectFromResult(rs);
					result.add(o);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return result;
		}

		public static Offering buildObjectFromResult(ResultSet rs) throws SQLException {
			Offering res = new Offering();
			String code = rs.getString("course_code");
			String classCode = rs.getString("class_code");
			Course c = CourseManager.get(code);
			OfferingClassTime ot = OfferingClassTimeManager.get(code, classCode);
			OfferingExamTime oe = OfferingExamTimeManager.get(code, classCode);
			res.setCourse(c);
			res.setClassTime(ot);
			res.setExamTime(oe);
			res.setClassCode(classCode);
			res.setInstructor(rs.getString("instructor"));
			res.setCapacity(rs.getInt("capacity"));
			retrieveStudents(res);
			return res;
		}

		public static void retrieveStudents(Offering o) throws SQLException {
			HashSet<String> registered = new HashSet<>();
			LinkedHashSet<String> waiting = new LinkedHashSet<>();
			Connection con = ConnectionPool.getConnection();
			PreparedStatement regStm = con.prepareStatement(selectRegisteredStudentIDs);
			PreparedStatement waitStm = con.prepareStatement(selectWaitingStudentIDs);
			regStm.setString(1, o.getCourse().getCode());
			regStm.setString(2, o.getClassCode());
			waitStm.setString(1, o.getCourse().getCode());
			waitStm.setString(2, o.getClassCode());
			ResultSet regRS = regStm.executeQuery();
			ResultSet waitRS = waitStm.executeQuery();
			while(regRS.next()) {
				registered.add(regRS.getString("student_id"));
			}
			while(waitRS.next()) {
				waiting.add(waitRS.getString("student_id"));
			}
			regStm.close();
			waitStm.close();
			con.close();
			o.setRegisteredStudents(registered);
			o.setWaitingStudents(waiting);
		}
	}

	public static class StudentManager {

		private static HashMap<String, Student> students = new HashMap<>();
		static String retrieveAllUrl = externalServerUrl + "students";
		static String retrieveGradesUrl = externalServerUrl + "grades";

		private static String insertQuery =
			"INSERT INTO bolbolestan.Student" +
				" (id, name, second_name, email, password, birth_date, field, faculty, level, status, image)" +
			" VALUES" +
				" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" name=?, second_name=?, email=?, password=?, birth_date=?, field=?, faculty=?, level=?, status=?, image=?;";
		private static String getQuery =
			"SELECT *" +
			" FROM bolbolestan.Student S" +
			" WHERE S.id=?";

		private static String removeGradesQuery =
			"DELETE" + 
			" FROM bolbolestan.Grade" +
			" WHERE student_id=?;";
		
		private static String removeTermsQuery =
			"DELETE" +
			" FROM bolbolestan.Term" +
			" WHERE student_id=?;";

		private static String insertTermQuery =
			"INSERT IGNORE INTO bolbolestan.Term" +
				" (student_id, term)" +
			" VALUES" +
				" (?, ?);";

		private static String insertGradeQuery =
			"INSERT INTO bolbolestan.Grade" +
				" (student_id, course_code, term, grade)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" grade=?;";

		private static String removeOfferingsQuery =
			"DELETE FROM bolbolestan.%s" +
			" WHERE student_id=?;";

		private static String insertChosenOfferingQuery =
			"INSERT IGNORE INTO bolbolestan.StudentChosenOfferings" +
				" (student_id, course_code, class_code)" +
			" VALUES" +
				" (?, ?, ?);";
		private static String getChosenOfferingQuery =
			"SELECT *" +
			" FROM bolbolestan.StudentChosenOfferings" +
			" WHERE student_id=?;";
		
		private static String insertSubmittedOfferingQuery =
			"INSERT IGNORE INTO bolbolestan.StudentLastPlan" +
				" (student_id, course_code, class_code)" +
			" VALUES" +
				" (?, ?, ?);";
		private static String getSubmittedOfferingQuery =
			"SELECT *" +
			" FROM bolbolestan.StudentChosenOfferings" +
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
				String removeChosenQuery = String.format(removeOfferingsQuery, "StudentChosenOfferings");
				String removeLastQuery = String.format(removeOfferingsQuery, "StudentLastPlan");
				PreparedStatement removeChosenStm = con.prepareStatement(removeChosenQuery);
				PreparedStatement removeLastStm = con.prepareStatement(removeLastQuery);
				PreparedStatement addChosenStm = con.prepareStatement(insertChosenOfferingQuery);
				PreparedStatement addLastStm = con.prepareStatement(insertSubmittedOfferingQuery);
				
				removeChosenStm.setString(1, s.getId());
				removeChosenStm.addBatch();
				removeLastStm.setString(1, s.getId());
				removeLastStm.addBatch();

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
				removeChosenStm.executeBatch();
				removeLastStm.executeBatch();
				addChosenStm.executeBatch();
				addLastStm.executeBatch();
				con.commit();
				removeChosenStm.close();
				removeLastStm.close();
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
					student.setImg(rs.getString("image"));
				} else {
					rs.close();
					stm.close();
					con.close();
					throw new Exceptions.StudentNotFound();
				}
				rs.close();
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
				con.close();
				chosenStm.close();
				lastStm.close();
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
			"INSERT INTO bolbolestan.Course" +
				" (code, name, type, units)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" name=?, type=?, units=?;";

		private static String getQuery =
			"SELECT *" +
			" FROM bolbolestan.Course C" +
			" WHERE C.code=?;";

		private static String insertPrerequisite =
			"INSERT IGNORE INTO bolbolestan.Prerequisites" +
				" (code, precode)" +
			" VALUES" +
				" (?, ?);";
		private static String getPrerequisitesQuery =
			"SELECT *" +
			" FROM bolbolestan.Prerequisites P, bolbolestan.Course C" +
			" WHERE P.precode=C.code AND P.code=?;";

		public static Course get(String code) {
			Course res = null;
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(getQuery);
				stm.setString(1, code);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					res = buildObjectFromResult(rs);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return res;
		}

		public static ArrayList<String> retrievePrerequisites(String code) throws SQLException {
			ArrayList<String> result = new ArrayList<>();
			Connection con = ConnectionPool.getConnection();
			PreparedStatement stm = con.prepareStatement(getPrerequisitesQuery);
			stm.setString(1, code);
			ResultSet rs = stm.executeQuery();
			while(rs.next()) {
				String pre = rs.getString("precode");
				result.add(pre);
			}
			rs.close();
			stm.close();
			con.close();
			return result;
		}

		public static Course buildObjectFromResult(ResultSet rs) throws SQLException {
			Course c = new Course();
			c.setCode(rs.getString("code"));
			c.setName(rs.getString("name"));
			c.setType(rs.getString("type"));
			c.setUnits(rs.getInt("units"));
			ArrayList<String> prerequisites = retrievePrerequisites(c.getCode());
			c.setPrerequisites(prerequisites);
			return c;
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
					for (String pre : c.getPrerequisites()) {
						stm.setString(1, c.getCode());
						stm.setString(2, pre);
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
		private static String selectQuery =
			"SELECT *" +
			" FROM bolbolestan.OfferingClassTime" +
			" WHERE course_code=? AND class_code=?;";
		
		private static String insertQuery =
			"INSERT INTO bolbolestan.OfferingClassTime" +
				" (course_code, class_code, time)" + 
			" VALUES" +
				" (?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" time=?;";
		private static String insertTimeDayQuery =
			"INSERT IGNORE INTO bolbolestan.OfferingDays" +
				" (course_code, class_code, day)" +
			" VALUES" +
				" (?, ?, ?);";
		private static String selectDaysQuery =
			"SELECT day" +
			" FROM bolbolestan.OfferingDays" +
			" WHERE course_code=? AND class_code=?;";

		public static OfferingClassTime get(String code, String classCode) {
			OfferingClassTime ot = null;
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(selectQuery);
				stm.setString(1, code);
				stm.setString(2, classCode);
				ResultSet rs = stm.executeQuery();
				if(rs.next()) {
					ot = buildObjectFromResult(rs);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return ot;
		}

		public static OfferingClassTime buildObjectFromResult(ResultSet rs) throws SQLException {
			OfferingClassTime ot = new OfferingClassTime();
			ot.setTime(rs.getString("time"));
			String code = rs.getString("course_code");
			String classCode = rs.getString("class_code");
			ArrayList<String> days = new ArrayList<>();
			Connection con = ConnectionPool.getConnection();
			PreparedStatement stm = con.prepareStatement(selectDaysQuery);
			stm.setString(1, code);
			stm.setString(2, classCode);
			ResultSet daysRes = stm.executeQuery();
			while(daysRes.next()) {
				String day = daysRes.getString("day");
				days.add(day);
			}
			stm.close();
			con.close();
			ot.setDays(days);
			return ot;
		}

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
		private static String selectQuery =
			"SELECT *" +
			" FROM bolbolestan.OfferingExamTime" +
			" WHERE course_code=? AND class_code=?;";
		
		private static String insertQuery =
			"INSERT INTO bolbolestan.OfferingExamTime" +
				" (course_code, class_code, start, end)" +
			" VALUES" +
				" (?, ?, ?, ?)" +
			" ON DUPLICATE KEY UPDATE" +
				" start=?, end=?;";

		public static OfferingExamTime get(String code, String classCode) {
			OfferingExamTime oe = null;
			try {
				Connection con = ConnectionPool.getConnection();
				PreparedStatement stm = con.prepareStatement(selectQuery);
				stm.setString(1, code);
				stm.setString(2, classCode);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					oe = buildObjectFromResult(rs);
				}
				rs.close();
				stm.close();
				con.close();
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			return oe;
		}

		public static OfferingExamTime buildObjectFromResult(ResultSet rs) throws SQLException, ParseException {
			OfferingExamTime oe = new OfferingExamTime();
			String start = rs.getString("start");
			String end = rs.getString("end");
			oe.setStart(start);
			oe.setEnd(end);
			return oe;
		}

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
			" FROM bolbolestan.Grade" +
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
				rs.close();
				stm.close();
				con.close();
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return result;
		}
	}

	public static void updateLocalDataBase() {
		createTables();
		CourseManager.initUpdate();
		OfferingManager.initUpdate();
		StudentManager.initUpdate();
	}

	public static void createTables() {
        try {
            System.out.println("#########################################################");
            Connection connection = ConnectionPool.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.runScript(new BufferedReader(new FileReader("src/main/java/models/database/.init.sql")));
			connection.close();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
