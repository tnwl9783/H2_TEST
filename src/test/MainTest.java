//package common;
//
//import db.DBConnection;
//import db.Pool;
//import netty.NettyHttpServer;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.LoggerContext;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.concurrent.*;
//
//public class Main {
//
//    // Constants
//    private static final int CORE_POOL_SIZE = 10;
//    private static final int MAXIMUM_POOL_SIZE = 100;
//    private static final long KEEP_ALIVE_TIME = 10;
//    private static final int QUEUE_SIZE = 1;
//
//    private static final Logger logger = LogManager.getLogger(Main.class);
//
//    public static void main(String[] args) {
//        System.out.println("Main Netty start");
//        int port = 8080;
//        new NettyHttpServer(port).run();
//    }
//
//    public static void receiveJSONObject(JSONObject jsonObject) {
//        try {
//            processJsonObject(jsonObject);
//        } catch (Exception e) {
//            logger.error("Error processing JSON object", e);
//        }
//    }
//
//    private static void processJsonObject(JSONObject jsonObject) throws Exception {
//        // Extract data from the JSON object
//        String dbType = jsonObject.getString("dbType");
//        boolean dbFlag = jsonObject.getBoolean("dbFlag");
//
//        DBConfig config = createDBConnection(dbType);
//
//        if (dbFlag) {
//            Pool.init(config.getJdbcUrl(), config.getUsername(), config.getUserPw());
//        }
//
//        configureLogger();
//
//        try (ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor()) {
//            executeMainThreads(threadPoolExecutor, dbFlag, dbType);
//        }
//
//        Stat.statRuntime();
//    }
//
//    private static DBConfig createDBConnection(String dbType) {
//        DBConfig config;
//        if ("h2".equalsIgnoreCase(dbType)) {
//            config = new DBConnection();
//            config.localServerTest("jdbc:h2:tcp://localhost/~/test_h2", "admin", "admin");
//            System.out.println("H2 DB");
//        } else {
//            config = new DBConnection();
//            config.localServerTest("jdbc:mariadb://localhost:3306/push", "push", "push");
//            System.out.println("MariaDB");
//        }
//        return config;
//    }
//
//    private static void configureLogger() {
//        LoggerContext context = (LoggerContext) LogManager.getContext(false);
//        File log4j2XmlFile = new File("/Users/sujijeon/Documents/H2_TEST/config/log4j2.xml");
//        context.setConfigLocation(log4j2XmlFile.toURI());
//    }
//
//    private static ThreadPoolExecutor createThreadPoolExecutor() {
//        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
//        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
//    }
//
//    private static void executeMainThreads(ThreadPoolExecutor threadPoolExecutor, boolean dbFlag, String dbType) {
//        System.out.println("ThreadPoolExecutor: " + threadPoolExecutor);
//        for (int i = 0; i < MAXIMUM_POOL_SIZE; i++) {
//            threadPoolExecutor.execute(new MainThread2(i, dbFlag, dbType));
//        }
//    }
//}
