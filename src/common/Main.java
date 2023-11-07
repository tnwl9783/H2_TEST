package common;

import db.DBConnection;
import db.Pool;
import netty.NettyHttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        System.out.println("start");
        int port = 8080;
        new NettyHttpServer(port).run();
    }

    public static void test(JSONObject jsonObject) {

        DBConfig config = null;
        System.out.println("start");

        Map<String, Object> map = new HashMap<>();
        // Iterate through the keys in the JSONObject and put them into the Map
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.get(key));
        }
        // map 출력.
        System.out.println("Map: " + map);

        System.out.println(map.get("dbFlag").getClass());
        Boolean dbFlag = (boolean)map.get("dbFlag");
        String dbType = (String) map.get("dbType");
        int queryLoop = (int)map.get("queryLoop");

        try{

 //           String dbType = "h2";

            if (dbType.equalsIgnoreCase("h2")) {
                config = new DBConnection();
                config.localServerTest("jdbc:h2:tcp://localhost/~/test_h2", "sa", "sa");
                System.out.println("h2h2h2h2h2h2h2h2h22h2h2h2hh2h2h2h2h2h2h2h2");
            } else {
                config = new DBConnection();
                config.localServerTest("jdbc:mariadb://localhost:3306/rusa", "push", "push");
                System.out.println("mariamariamariamariamariamariamariamaria");
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
                threadPoolExecutor.execute(new MainThread(i));
            }

            threadPoolExecutor.shutdown();
            while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.debug("wait End");
            }
            Stat.statRuntime();
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}