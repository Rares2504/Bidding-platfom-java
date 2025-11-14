package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String URL      = "jdbc:mysql://127.0.0.1:3306/bidding_platform?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "R@resK25";

    private static DatabaseConnectionManager instance;
    private Connection conn;

    private DatabaseConnectionManager() throws SQLException {
        this.conn = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static synchronized DatabaseConnectionManager getInstance() throws SQLException {
        if (instance == null || instance.conn.isClosed()) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
