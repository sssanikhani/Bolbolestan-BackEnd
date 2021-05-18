package models.database.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import models.database.ConnectionPool;
import models.entities.Offering;
import models.entities.OfferingExamTime;

public class OfferingExamTimeRepository {

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
				oe = buildObjectFromResult(rs);
			}
			rs.close();
			stm.close();
			con.close();
		} catch (Exception e) {
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
		return oe;
	}

	public static OfferingExamTime buildObjectFromResult(ResultSet rs)
		throws SQLException, ParseException {
		OfferingExamTime oe = new OfferingExamTime();
		String start = rs.getString("start");
		String end = rs.getString("end");
		oe.setStart(start);
		oe.setEnd(end);
		return oe;
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
}
