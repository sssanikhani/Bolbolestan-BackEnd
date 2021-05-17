package models.database.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import models.database.ConnectionPool;
import models.entities.Course;

public class CourseRepository {

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
		} catch (SQLException e) {
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
		while (rs.next()) {
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
		courses.clear();
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
		} catch (SQLException e) {
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
