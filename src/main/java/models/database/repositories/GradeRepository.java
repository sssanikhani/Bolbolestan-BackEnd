package models.database.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import models.database.ConnectionPool;
import models.entities.Grade;

public class GradeRepository {

	private static String getQuery =
		"SELECT *" + 
        " FROM bolbolestan.Grade" + 
        " WHERE student_id=?;";

	public static ArrayList<Grade> get(String studentId) {
		ArrayList<Grade> result = new ArrayList<>();
		
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			con = ConnectionPool.getConnection();
			stm = con.prepareStatement(getQuery);
			stm.setString(1, studentId);
			rs = stm.executeQuery();
			while (rs.next()) {
				Grade g = new Grade();
				g.setCourse(CourseRepository.get(rs.getString("course_code")));
				g.setGrade(rs.getFloat("grade"));
				g.setTerm(rs.getInt("term"));
				result.add(g);
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
}
