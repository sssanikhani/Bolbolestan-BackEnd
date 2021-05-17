package models.database.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.database.ConnectionPool;
import models.entities.Offering;
import models.entities.OfferingClassTime;

public class OfferingClassTimeRepository {

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
			if (rs.next()) {
				ot = buildObjectFromResult(rs);
			}
			rs.close();
			stm.close();
			con.close();
		} catch (SQLException e) {
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
		while (daysRes.next()) {
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
