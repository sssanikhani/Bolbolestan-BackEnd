package models.database.repositories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import models.database.ConnectionPool;
import models.entities.Course;
import models.entities.Offering;
import models.entities.OfferingClassTime;
import models.entities.OfferingExamTime;
import models.serializers.OfferingSerializer;
import models.statics.Constants;
import models.statics.Exceptions;
import models.utils.Utils;

public class OfferingRepository {

	private static HashMap<String, HashMap<String, Offering>> codeOfferingsMap = new HashMap<>();
	private static String retrieveAllUrl = Constants.externalDataBaseURL + "courses";

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
		" FROM bolbolestan.Offering O, bolbolestan.Course C" +
		" WHERE O.course_code=C.code AND C.name LIKE ?;";

	private static String selectRegisteredStudentIDs =
		"SELECT R.student_id" +
		" FROM bolbolestan.RegisteredStudents R" +
		" WHERE R.course_code = ? AND R.class_code = ?;";

	private static String selectWaitingStudentIDs =
		"SELECT W.student_id" +
		" FROM bolbolestan.WaitingStudents W" +
		" WHERE W.course_code = ? AND W.class_code = ?" +
		" ORDER BY W.insert_time ASC;";

	public static void updateFromExternalServer() throws IOException, InterruptedException {
		HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
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
		codeOfferingsMap.clear();
	}

	public static void bulkUpdate(ArrayList<Offering> list) {
		Connection con = null;
		PreparedStatement stm = null;
		try {
			con = ConnectionPool.getConnection();
			con.setAutoCommit(false);
			stm = con.prepareStatement(insertQuery);
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
			OfferingClassTimeRepository.bulkUpdate(list);
			OfferingExamTimeRepository.bulkUpdate(list);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(stm != null && !stm.isClosed())
					stm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
	}

	public static void updateStudents(Offering o) {
		Connection con = null;
		PreparedStatement removeWaitingStm = null;
		PreparedStatement removeRegisteredStm = null;
		PreparedStatement addWaitingStudentStm = null;
		PreparedStatement addRegisteredStudentStm = null;
		try {
			con = ConnectionPool.getConnection();
			con.setAutoCommit(false);
			String removeWaitingQuery = String.format(removeStudentsQuery, "WaitingStudents");
			String removeRegisteredQuery = String.format(
				removeStudentsQuery,
				"RegisteredStudents"
			);
			removeWaitingStm = con.prepareStatement(removeWaitingQuery);
			removeRegisteredStm = con.prepareStatement(
				removeRegisteredQuery
			);
			addWaitingStudentStm = con.prepareStatement(
				insertWaitingStudentIDs
			);
			addRegisteredStudentStm = con.prepareStatement(
				insertRegisteredStudentIDs
			);
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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(removeRegisteredStm != null && !removeRegisteredStm.isClosed())
					removeRegisteredStm.close();
				if(removeWaitingStm != null && !removeWaitingStm.isClosed())
					removeWaitingStm.close();
				if(addWaitingStudentStm != null && !addWaitingStudentStm.isClosed())
					addWaitingStudentStm.close();
				if(addRegisteredStudentStm != null && !addRegisteredStudentStm.isClosed())
					addRegisteredStudentStm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
	}

	public static ArrayList<Offering> getAll() {
		ArrayList<Offering> list = new ArrayList<>();

		Connection con = null;
		Statement stm = null;
		ResultSet rs = null;
		try {
			con = ConnectionPool.getConnection();
			stm = con.createStatement();
			rs = stm.executeQuery(selectAllQuery);
			while (rs.next()) {
				Offering o = buildObjectFromResult(rs);
				list.add(o);
			}
			rs.close();
			stm.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(rs != null && !rs.isClosed())
					rs.close();
				if(stm != null && !stm.isClosed())
					stm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
		return list;
	}

	public static ArrayList<Offering> search(String query) {
		ArrayList<Offering> filtered = new ArrayList<>();
		
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			con = ConnectionPool.getConnection();
			stm = con.prepareStatement(searchQuery);
			stm.setString(1, "%" + query + "%");
			rs = stm.executeQuery();
			while (rs.next()) {
				Offering o = buildObjectFromResult(rs);
				filtered.add(o);
			}
			rs.close();
			stm.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(rs != null && !rs.isClosed())
					rs.close();
				if(stm != null && !stm.isClosed())
					stm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
		return filtered;
	}

	public static Offering get(String code, String classCode)
		throws Exceptions.offeringNotFound {
		Offering offering = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			con = ConnectionPool.getConnection();
			stm = con.prepareStatement(selectQuery);
			stm.setString(1, code);
			stm.setString(2, classCode);
			rs = stm.executeQuery();
			if (rs.next()) {
				offering = buildObjectFromResult(rs);
			} else {
				throw new Exceptions.offeringNotFound();
			}
			rs.close();
			stm.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(rs != null && !rs.isClosed())
					rs.close();
				if(stm != null && !stm.isClosed())
					stm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
		return offering;
	}

	public static ArrayList<Offering> getIfIn(ArrayList<String[]> codes) {
		ArrayList<Offering> result = new ArrayList<>();
		if (codes.isEmpty()) return result;
		ArrayList<String> queryList = new ArrayList<>();
		for (String[] code : codes) {
			String s = String.format("(%s, %s)", code[0], code[1]);
			queryList.add(s);
		}
		String list = String.join(",", queryList);

		Connection con = null;
		Statement stm = null;
		ResultSet rs = null;
		try {
			con = ConnectionPool.getConnection();
			stm = con.createStatement();
			String query = String.format(multipleSelectQuery, list);
			rs = stm.executeQuery(query);
			while (rs.next()) {
				Offering o = buildObjectFromResult(rs);
				result.add(o);
			}
			rs.close();
			stm.close();
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(rs != null && !rs.isClosed())
					rs.close();
				if(stm != null && !stm.isClosed())
					stm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
		return result;
	}

	public static Offering buildObjectFromResult(ResultSet rs) throws SQLException {
		Offering res = new Offering();
		String code = rs.getString("course_code");
		String classCode = rs.getString("class_code");
		Course c = CourseRepository.get(code);
		OfferingClassTime ot = OfferingClassTimeRepository.get(code, classCode);
		OfferingExamTime oe = OfferingExamTimeRepository.get(code, classCode);
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
		
		Connection con = null;
		PreparedStatement regStm = null;
		PreparedStatement waitStm = null;
		ResultSet regRS = null;
		ResultSet waitRS = null;
		try {
			con = ConnectionPool.getConnection();
			regStm = con.prepareStatement(selectRegisteredStudentIDs);
			waitStm = con.prepareStatement(selectWaitingStudentIDs);
			regStm.setString(1, o.getCourse().getCode());
			regStm.setString(2, o.getClassCode());
			waitStm.setString(1, o.getCourse().getCode());
			waitStm.setString(2, o.getClassCode());
			regRS = regStm.executeQuery();
			waitRS = waitStm.executeQuery();
			while (regRS.next()) {
				registered.add(regRS.getString("student_id"));
			}
			while (waitRS.next()) {
				waiting.add(waitRS.getString("student_id"));
			}
			regRS.close();
			waitRS.close();
			regStm.close();
			waitStm.close();
			con.close();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			try {
				if(regRS != null && !regRS.isClosed())
					regRS.close();
				if(waitRS != null && !waitRS.isClosed())
					waitRS.close();
				if(regStm != null && !regStm.isClosed())
					regStm.close();
				if(waitStm != null && !waitStm.isClosed())
					waitStm.close();
				if(con != null && !con.isClosed())
					con.close();
			} catch(SQLException e2) {
				System.out.println(e2.getMessage());
			}
		}
		o.setRegisteredStudents(registered);
		o.setWaitingStudents(waiting);
	}
}
