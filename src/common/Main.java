package common;

import db.DBConnection;
import db.Pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
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
    public static void main(String[] args) {
        boolean DBflag = true;
        DBConfig config = null;
        try {
            String dbType = "h2"; // Set this dynamically based on your requirements

            if(dbType.equalsIgnoreCase("h2") ){
                config =  new DBConnection();
                config.createTest("jdbc:h2:tcp://localhost/~/test_h2", "sa", "sa");
            } else{

                config = new DBConnection();
                config.createTest("jdbc:mariadb://localhost:3306/rusa", "push", "push");
            }

            if(DBflag){
                Pool.init(config.getJdbcUrl(), config.getUsername(), config.getUserPw());
            }


            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File log4j2XmlFile = new File("/Users/sujijeon/Documents/H2_TEST/config/log4j2.xml");
            context.setConfigLocation(log4j2XmlFile.toURI());

            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueSize);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

            for(int i=0; i<maximumPoolSize; i++){
                threadPoolExecutor.execute(new MainThread(i));
            }

            threadPoolExecutor.shutdown();
            while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                logger.debug("wait End");
            }
            Stat.statRuntime();
        } catch(Exception e) {
            logger.error(e.getMessage());
        }

    }
}
