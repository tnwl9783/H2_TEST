package db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class Single {
    public static final Logger logger = LogManager.getLogger(Single.class);

    public static Connection getConnection(String url, String user, String password) {
        Connection connection = null;
        try {
//            Class.forName("org.mariadb.jdbc.Driver");
//            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.h2.Driver");
            connection =  DriverManager.getConnection(url, user, password);


        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

}
