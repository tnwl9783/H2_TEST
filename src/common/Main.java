package common;

import db.DBConnection;
import db.Pool;
import netty.NettyHttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.*;

public class Main {

    // 쓰레드 기본 갯수
    public static int corePoolSize = 10;
    // 쓰레트 최대 갯수
    public static int maximumPoolSize = 100;
    //
    public static long keepAliveTime = 10;
    //
    public static int queueSize = 1;
    public static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

//        post("/api/insert", (request, response) -> {
//            try {
//                JSONObject json = new JSONObject(request.body());
//                String dbType = json.getString("dbType");
//                boolean dbFlag = json.getBoolean("dbFlag");
//
//                MainThread2.insertData(dbFlag, dbType);
//                return "Data inserted successfully";
//            } catch (Exception e) {
//                response.status(500); // Internal Server Error
//                return "Error inserting data: " + e.getMessage();
//            }
//        });



        System.out.println("main netty start");
        int port = 8080;
        new NettyHttpServer(port).run();
    }

    public static void receiveJSONObject(JSONObject jsonObject) {

        DBConfig config = null;

        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.get(key));
        }


        String dbType = (String) map.get("dbType");
        Boolean dbFlag = (boolean) map.get("dbFlag");


        try {

            if (dbType.equalsIgnoreCase("h2")) {
                config = new DBConnection();
                config.localServerTest("jdbc:h2:tcp://localhost/~/test_h2", "admin", "admin");
                System.out.println("h2 db");
            } else {
                config = new DBConnection();
                config.localServerTest("jdbc:mariadb://localhost:3306/push", "push", "push");
                System.out.println("maria");
            }

            if (dbFlag) {
                Pool.init(config.getJdbcUrl(), config.getUsername(), config.getUserPw());
            }

            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File log4j2XmlFile = new File("/Users/sujijeon/Documents/H2_TEST/config/log4j2.xml");
            context.setConfigLocation(log4j2XmlFile.toURI());

            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueSize);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
            System.out.println("threadPoolExecutor " + threadPoolExecutor);
            for (int i = 0; i < maximumPoolSize; i++) {
                threadPoolExecutor.execute(new MainThread2(i, dbFlag, dbType));


            }

            threadPoolExecutor.shutdown();
            while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.debug("wait End");
            }
            Stat.statRuntime();
        } catch (
                Exception e) {
            logger.error(e.getMessage());
        }

    }
}
