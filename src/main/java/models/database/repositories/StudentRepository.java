package models.database.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.database.ConnectionPool;
import models.entities.Grade;
import models.entities.Offering;
import models.entities.Student;
import models.entities.Term;
import models.serializers.GradeSerializer;
import models.statics.Constants;
import models.statics.Exceptions;
import models.utils.Utils;

public class StudentRepository {

	private static HashMap<String, Student> students = new HashMap<>();
	static String retrieveAllUrl = Constants.externalDataBaseURL + "students";
	static String retrieveGradesUrl = Constants.externalDataBaseURL + "grades";

	private static String insertQuery =
		"INSERT INTO bolbolestan.Student" +
			" (id, name, second_name, email, password, birth_date, field, faculty, level, status, image)" +
		" VALUES" +
			" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
		" ON DUPLICATE KEY UPDATE" +
			" name=?, second_name=?, email=?, password=?, birth_date=?, field=?, faculty=?, level=?, status=?, image=?;";
	private static String getByIdQuery =
		"SELECT *" + 
		" FROM bolbolestan.Student S" + 
		" WHERE S.id=?;";
	private static String getByEmailQuery =
		"SELECT *" + 
		" FROM bolbolestan.Student S" + 
		" WHERE S.email=?;";

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
		HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
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
		students.clear();
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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void updateOfferings(Student s) {
		try {
			Connection con = ConnectionPool.getConnection();
			con.setAutoCommit(false);
			String removeChosenQuery = String.format(
				removeOfferingsQuery,
				"StudentChosenOfferings"
			);
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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static Student get(String identifier, boolean byEmail) throws Exceptions.StudentNotFound {
		Student student = new Student();
		try {
			Connection con = ConnectionPool.getConnection();
			PreparedStatement stm; 
			if (byEmail) {
				stm = con.prepareStatement(getByEmailQuery);
			}
			else {
				stm = con.prepareStatement(getByIdQuery);
			}
			stm.setString(1, identifier);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				student.setId(rs.getString("id"));
				student.setName(rs.getString("name"));
				student.setSecondName(rs.getString("second_name"));
				student.setEmail(rs.getString("email"));
				student._setHashedPassword(rs.getString("password"));
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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		retrieveGrades(student);
		retrieveOfferings(student);
		return student;
	}

	public static boolean existsId(String id) {
		try {
			get(id, false);
			return true;
		} catch(Exceptions.StudentNotFound e) {
			return false;
		}
	}

	public static boolean existsEmail(String email) {
		try {
			get(email, true);
			return true;
		} catch(Exceptions.StudentNotFound e) {
			return false;
		}
	}

	public static void retrieveGrades(Student s) {
		ArrayList<Grade> grades = GradeRepository.get(s.getId());
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
				String[] codes = { code, classCode };
				chosenCodes.add(codes);
			}
			lastStm.setString(1, s.getId());
			ResultSet lastRS = lastStm.executeQuery();
			while (lastRS.next()) {
				String code = lastRS.getString("course_code");
				String classCode = lastRS.getString("class_code");
				String[] codes = { code, classCode };
				lastCodes.add(codes);
			}
			con.close();
			chosenStm.close();
			lastStm.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		ArrayList<Offering> chosen = OfferingRepository.getIfIn(chosenCodes);
		ArrayList<Offering> last = OfferingRepository.getIfIn(lastCodes);
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
