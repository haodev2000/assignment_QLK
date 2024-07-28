package vn.edu.likelion.QLK.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	

	private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl"; // Update with your database URL
  private static final String USER = "sys as SYSDBA"; // Update with your database username
  private static final String PASSWORD = "123123"; // Update with your database password

    // JDBC variables for opening and managing connection
    private static Connection connection;

    // Method to get the connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Registering the Oracle JDBC driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                // Opening the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
            } catch (ClassNotFoundException e) {
                System.out.println("Oracle JDBC Driver not found. Include it in your library path.");
                e.printStackTrace();
                throw new SQLException(e);
            } catch (SQLException e) {
                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    // Method to close the connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
