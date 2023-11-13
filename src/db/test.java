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

public class test implements Runnable {
    private static final Logger logger = LogManager.getLogger(test.class);

    // Assuming this map contains the data to be inserted into and selected from the H2 database
    private static final ConcurrentHashMap<String, String> database = new ConcurrentHashMap<>();

    private String threadName;
    private int queryCount = 10000;

    public test(int i) {
        this.threadName = "MainThread-" + i;
    }

    @Override
    public void run() {
        boolean usePool = true;
        String url = "jdbc:h2:tcp://localhost/~/test_h2";
        String user = "admin";
        String password = "admin";

        try {
            for (int i = 0; i < queryCount; i++) {
                Instant beforeTime = Instant.now();
                try (Connection connection = usePool ? Pool.getConnection() : Single.getConnection(url, user, password)) {
                    // Insert data into H2 database
                    insertDataIntoH2(connection, i);

                    // Select data from H2 database
                    String selectedValue = selectDataFromH2(connection, i);
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

    private void insertDataIntoH2(Connection connection, int i) throws SQLException {
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

    private String selectDataFromH2(Connection connection, int i) throws SQLException {
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
