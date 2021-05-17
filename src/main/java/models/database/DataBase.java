package models.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;

import org.apache.ibatis.jdbc.ScriptRunner;

import models.database.repositories.CourseRepository;
import models.database.repositories.OfferingRepository;
import models.database.repositories.StudentRepository;
import models.entities.Student;
import models.statics.Exceptions;

public class DataBase {

	public static class AuthManager {

		private static Student loggedInUser = null;

		public static Student getLoggedInUser() {
			return loggedInUser;
		}

		public static void login(String id) throws Exceptions.StudentNotFound {
			loggedInUser = StudentRepository.get(id);
		}

		public static void logout() {
			loggedInUser = null;
		}

		public static boolean isLoggedIn() {
			return loggedInUser != null;
		}
	}

	public static void updateLocalDataBase() {
		createTables();
		CourseRepository.initUpdate();
		OfferingRepository.initUpdate();
		StudentRepository.initUpdate();
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
