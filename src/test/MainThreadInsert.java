//package test;
//
//import common.Stat;
//import db.Pool;
//import db.Single;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.time.Duration;
//import java.time.Instant;
//
//public class MainThreadInsert implements Runnable {
//    private static final Logger logger = LogManager.getLogger(MainThreadInsert.class);
//
//    private String threadName;
//
//    private int queryCount = 1000;
//
//    public static boolean dbFlag;
//
//    public static String dbType;
//
//
//    public MainThreadInsert(int i, boolean dbFlag, String dbType) {
//        this.threadName = "MainThread-" + i;
//        this.dbFlag = dbFlag;
//        this.dbType = dbType;
//
//    }
//
//    @Override
//    public void run() {
//        try {
//            // H2 Database
//            performDatabaseOperations(true);
//
//            // MariaDB Database
//            performDatabaseOperations(false);
//        } catch (Exception e) {
//            logger.error("Error in thread {}: {}", threadName, e.getMessage());
//        }
//    }
//
//    private void performDatabaseOperations(boolean useH2) throws SQLException {
//        String url, user, password;
//        if (useH2) {
//            url = "jdbc:h2:tcp://localhost/~/test_h2";
//            user = "admin";
//            password = "admin";
//        } else {
//            url = "jdbc:mariadb://localhost:3306/push";
//            user = "push";
//            password = "push";
//        }
//
//// insert
//        try {
//            for (int i = 0; i < queryCount; i++) {
//                Instant beforeTime = Instant.now();
//                try (Connection connection = useH2 ? Pool.getConnection() : Single.getConnection(url, user, password)) {
//                    // Insert
//                    insertData(connection, i, useH2);
//
//                }
//                Instant afterTime = Instant.now();
//                long runTime = Duration.between(beforeTime, afterTime).toMillis();
//                Stat.addList(runTime);
//            }
//        } catch (Exception e) {
//            logger.error("Error in thread {}: {}", threadName, e.getMessage());
//        }
//    }
//
//    private static void insertData(Connection connection, int i, boolean useH2) throws SQLException {
//
//        String insertSql = "INSERT INTO json_data (dbFlag, dbType) VALUES (?, ?)";
//
//        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
//
//
//            insertStatement.setBoolean(1, dbFlag);
//            insertStatement.setString(2, dbType);
//
//            // insert 실행하다.
//            insertStatement.executeUpdate();
//        }
//    }
//
//}
