package db;

import common.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class testttt implements Runnable {
    private static final Logger logger = LogManager.getLogger(testttt.class);

    // Assuming this map contains the data to be inserted into and selected from databases
    private static final ConcurrentHashMap<String, String> database = new ConcurrentHashMap<>();

    private String threadName;
    private int queryCount = 10000;

    public testttt(int i) {
        this.threadName = "MainThread-" + i;
    }

    @Override
    public void run() {
        try {
            // H2 Database
            performDatabaseOperations(true);

            // MariaDB Database
            performDatabaseOperations(false);
        } catch (Exception e) {
            logger.error("Error in thread {}: {}", threadName, e.getMessage());
        }
    }

    private void performDatabaseOperations(boolean useH2) throws SQLException {
        String url, user, password;
        if (useH2) {
            url = "jdbc:h2:tcp://localhost/~/test_h2";
            user = "admin";
            password = "admin";
        } else {
            url = "jdbc:mariadb://localhost:3306/rusa";
            user = "push";
            password = "push";
        }

        try {
            for (int i = 0; i < queryCount; i++) {
                Instant beforeTime = Instant.now();
                try (Connection connection = useH2 ? Pool.getConnection() : Single.getConnection(url, user, password)) {
                    // Insert data into the database
                    insertData(connection, i, useH2);

                    // Select data from the database
                    String selectedValue = selectData(connection, i, useH2);
                    logger.info("{} {} run - Selected Value: {}", threadName, i, selectedValue);
                }
                Instant afterTime = Instant.now();
                long runTime = Duration.between(beforeTime, afterTime).toMillis();
                Stat.addList(runTime);
            }
        } catch (Exception e) {
            logger.error("Error in thread {}: {}", threadName, e.getMessage());
        }
    }

    private void insertData(Connection connection, int i, boolean useH2) throws SQLException {
        // Assuming your table has columns named 'column1', 'column2' etc.
        String insertSql = "INSERT INTO your_table (column1, column2) VALUES (?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            // Assuming your map contains the necessary information for the insert
            insertStatement.setString(1, database.get("value1")); // Replace "value1" with the actual key in your map
            insertStatement.setString(2, database.get("value2")); // Replace "value2" with the actual key in your map

            // ... repeat for other columns if needed

            // Execute the insert
            insertStatement.executeUpdate();
        }
    }

    private String selectData(Connection connection, int i, boolean useH2) throws SQLException {
        // Assuming your table has a column named 'column1' and you want to select it
        String selectSql = "SELECT column1 FROM your_table WHERE id = ?";

        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            // Set the parameter for the select query
            selectStatement.setInt(1, i);

            // Execute the select
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                // Process the result set if needed
                if (resultSet.next()) {
                    return resultSet.getString("column1");
                }
            }
        }
        return null;
    }
}
