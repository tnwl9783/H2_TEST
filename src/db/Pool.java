package db;

import org.apache.commons.dbcp2.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;


public class Pool {
    public static final Logger logger = LogManager.getLogger(Pool.class);
    public static BasicDataSource dataS = new BasicDataSource();

    public static void init(String url, String user, String password){
        try {
            dataS.setDriverClassName("org.h2.Driver");

//            dataS.setDriverClassName("org.mariadb.jdbc.Driver");
            dataS.setUrl(url);
            dataS.setUsername(user);
            dataS.setPassword(password);
            dataS.setInitialSize(5);
            dataS.setMaxTotal(10);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataS.getConnection();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

}
