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
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.Duration;
//import java.time.Instant;
//
//public class MainThreadSelect implements Runnable {
//    private static final Logger logger = LogManager.getLogger(MainThreadSelect.class);
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
//    public MainThreadSelect(int i, boolean dbFlag, String dbType) {
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
//// select
//        try {
//            for (int i = 0; i < queryCount; i++) {
//                Instant beforeTime = Instant.now();
//                try (Connection connection = useH2 ? Pool.getConnection() : Single.getConnection(url, user, password)) {
//
//
//                    // Select
//                    String selectedValue = selectData(connection, i, useH2);
//                    logger.info("{} {} run - Selected Value: {}", threadName, i, selectedValue);
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
//
//
//    private String selectData(Connection connection, int i, boolean useH2) throws SQLException {
//
//        String selectSql = "SELECT dbType FROM json_data WHERE id = ?";
//
//        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
//            // 선택 쿼리에 대한 매개변수 설정.
//            selectStatement.setInt(1, i);
//
//            // select 실행
//            try (ResultSet resultSet = selectStatement.executeQuery()) {
//                // Process the result set if needed
//                if (resultSet.next()) {
//                    return resultSet.getString("dbType");
//                }
//            }
//        }
//        return null;
//    }
//
//}
