//package common;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class DataHandler {
//
//    public static void insertData(String dbType, boolean dbFlag) {
//        try (Connection connection = /* obtain your database connection here */) {
//            String insertSql = "INSERT INTO your_table (dbType, dbFlag) VALUES (?, ?)";
//            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
//                insertStatement.setString(1, dbType);
//                insertStatement.setBoolean(2, dbFlag);
//                insertStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace(); // Handle the exception properly in your application
//        }
//    }
//
//    public static String selectData(int id) {
//        try (Connection connection = /* obtain your database connection here */) {
//            String selectSql = "SELECT dbType FROM your_table WHERE id = ?";
//            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
//                selectStatement.setInt(1, id);
//                try (ResultSet resultSet = selectStatement.executeQuery()) {
//                    if (resultSet.next()) {
//                        return resultSet.getString("dbType");
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace(); // Handle the exception properly in your application
//        }
//        return null;
//    }
//
//    // Other methods or configurations related to your data handling
//}
//
