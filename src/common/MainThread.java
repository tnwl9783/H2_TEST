package common;

import db.Pool;
import db.Single;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;

public class MainThread implements Runnable{

    public static final Logger logger = LogManager.getLogger(MainThread.class);
//    public static String sql = "SELECT * FROM all_info WHERE code = ?";

//    public static String sql = "SELECT * FROM all_info";

    public static String sql = "SELECT * FROM TEST_TABLE";
    public int count = 10000;

    private String name =  "MainThread-"; //Thread.currentThread().getName();



    public MainThread(int i) {
        name = (name + i);
    }

    @Override
    public void run() {
        // DB pool 사용 여부
        boolean poolFlag = true;
        // DB 계정 정보 (이따 설정 파일로 빼기)
        String url = "jdbc:h2:tcp://localhost/~/test_h2";
        String user = "sa";
        String password = "sa";

//        String url = "jdbc:mariadb://localhost:3306/rusa";
//        String user = "push";
//        String password = "push";


        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if(poolFlag) {

                for(int i=0; i<count; i++) {
                    Instant beforeTime = Instant.now();
                    connection = Pool.getConnection();
                    System.out.println(name + " " + i + " run " + connection.toString());

                    runQuery(ps, connection, i, rs);

                    Instant afterTime = Instant.now();
                    long runTime = Duration.between(beforeTime, afterTime).toMillis();

                    Stat.addList(runTime);
                }
            } else {
                // Single
                for(int i=0; i<count; i++) {
                    Instant beforeTime = Instant.now();
                    connection = Single.getConnection(url, user, password);
                    System.out.println(name + " " + i + " run " + connection.toString());
                    runQuery(ps, connection, i, rs);
                    Instant afterTime = Instant.now();
                    long runTime = Duration.between(beforeTime, afterTime).toMillis();

                    Stat.addList(runTime);
                }
            }

        } catch (Exception e){
            logger.error(e.getMessage());
        } finally {
            closeConnection(rs, ps, connection);
        }


    }

    public static void runQuery(PreparedStatement ps, Connection connection, int i, ResultSet rs) {
        try {
            ps = connection.prepareStatement(sql);
      //      ps.setInt('1', i);
            rs = ps.executeQuery();
            closeConnection(rs, ps, connection);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void closeConnection(ResultSet rs, PreparedStatement ps, Connection connection){
        try {
            if(rs != null) {
                rs.close();
            }
            if(ps != null) {
                ps.close();
            }
            if(connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
