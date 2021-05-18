package models.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;

import models.database.repositories.CourseRepository;
import models.database.repositories.OfferingRepository;
import models.database.repositories.StudentRepository;

public class DataBase {

	public static void updateLocalDataBase() {
		createTables();
		CourseRepository.initUpdate();
		OfferingRepository.initUpdate();
		StudentRepository.initUpdate();
	}

	public static void createTables() {
        Connection connection = null;
        try {
            System.out.println("#########################################################");
            connection = ConnectionPool.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.runScript(new BufferedReader(new FileReader("src/main/java/models/database/.init.sql")));
			connection.close();
        } catch(Exception e) {
            try {
                if(connection != null && !connection.isClosed())
                    connection.close();
            } catch(SQLException e2) {
                System.out.println(e2.getMessage());
            }
            System.out.println(e.getMessage());
        }
    }
}
