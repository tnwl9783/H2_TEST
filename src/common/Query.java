package common;

import java.sql.*;

public class Query {

    private static final String JDBC_URL = "jdbc:h2:tcp://localhost/~/test_h2";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void insertData(Connection connection, String number, String text) throws SQLException {
        String insertSql = "INSERT INTO hello (number, text) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.setString(1, number);
            insertStatement.setString(2, text);
            insertStatement.executeUpdate();
        }
    }

    public static String selectData(Connection connection, String number) throws SQLException {
        String selectSql = "SELECT text FROM hello WHERE number = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, number);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("text");
                }
            }
        }
        return null;
    }
}
