package models.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

public class ConnectionPool {
    private static BasicDataSource ds = new BasicDataSource();
    private final static String dbName = LocalVars.getDBName();
    private final static String dbmsURL = "jdbc:mysql://localhost:3306";
    private final static String dbURL = dbmsURL + "/" + dbName;
    private final static String dbUserName = LocalVars.getDBUser();
    private final static String dbPassword = LocalVars.getDBPassword();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        ds.setUsername(dbUserName);
        ds.setPassword(dbPassword);
        ds.setUrl(dbmsURL);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        createTables();
        ds.setUrl(dbURL);
        setEncoding();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setEncoding(){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String encodingStatement = String.format("ALTER DATABASE %s CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;", dbName);
            statement.execute(encodingStatement);
            connection.close();
            statement.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        try {
            System.out.println("#########################################################");
            Connection connection = getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.runScript(new BufferedReader(new FileReader("src/main/java/models/logic/.init.sql")));
            connection.close();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
