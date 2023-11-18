package common;

import netty.NettyHttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

        System.out.println("main netty start");
        int port = 8080;
        new NettyHttpServer(port).run();
    }

    public static void receiveJSONObject(JSONObject jsonObject) {

        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.get(key));
        }


        String number = (String) map.get("number");
        String text = (String) map.get("text");

        String requestType = (String) map.get("requestType");

        try {
            Connection connection = Query.createConnection();

            if ("insert".equalsIgnoreCase(requestType)) {

                Query.insertData(connection, number, text);

            } else if ("select".equalsIgnoreCase(requestType)) {

                String result = new Query().selectData(connection, number);
                System.out.println("Selected data: " + result);

            } else {
                logger.error("Unknown requestType: " + requestType);
            }

            //종료
            connection.close();
        } catch (Exception e) {
            logger.error("Error during processing", e);
        }
    }

}


//    public static void receiveJSONObject(JSONObject jsonObject) {
//
//        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
//        for (String key : jsonObject.keySet()) {
//            map.put(key, jsonObject.get(key));
//        }
//        DBConfig config = null;
//        try {
//            if (dbType.equalsIgnoreCase("h2")) {
//                config = new DBConnection();
//                config.localServerTest("jdbc:h2:tcp://localhost/~/test_h2", "admin", "admin");
//                System.out.println("h2 db");
//            } else {
//                config = new DBConnection();
//                config.localServerTest("jdbc:mariadb://localhost:3306/push", "push", "push");
//                System.out.println("maria");
//            }
//
//            if (dbFlag) {
//                Pool.init(config.getJdbcUrl(), config.getUsername(), config.getUserPw());
//            }
//
//            LoggerContext context = (LoggerContext) LogManager.getContext(false);
//            File log4j2XmlFile = new File("/Users/sujijeon/Documents/H2_TEST/config/log4j2.xml");
//            context.setConfigLocation(log4j2XmlFile.toURI());
//
//            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueSize);
//            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
//            System.out.println("threadPoolExecutor " + threadPoolExecutor);
//            for (int i = 0; i < maximumPoolSize; i++) {
//                threadPoolExecutor.execute(new MainThread2(i, dbFlag, dbType));
//            }
//
//            threadPoolExecutor.shutdown();
//            while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
//                logger.debug("wait End");
//            }
//            Stat.statRuntime();
//        } catch (
//                Exception e) {
//            logger.error(e.getMessage());
//        }
//    }
//}
