package common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Stat {
    public static List<Long> runtimeList = new ArrayList<>();
    public static final Logger logger = LogManager.getLogger(Stat.class);

    public static synchronized void addList(Long runTime) throws Exception{
        runtimeList.add(runTime);
    }

    public static int count() throws Exception{
        return runtimeList.size();
    }

    public static void statRuntime() {
        try {
            if(runtimeList != null && runtimeList.size() != 0) {
                Long min = runtimeList.get(0);
                Long max = runtimeList.get(0);
                Long total = runtimeList.get(0);
                float stat = 0;

                for(int i=1; i<runtimeList.size(); i++) {
                    if(min > runtimeList.get(i)) {
                        min = runtimeList.get(i);
                    }
                    if(max < runtimeList.get(i)) {
                        min = runtimeList.get(i);
                    }
                    total += runtimeList.get(i);
                }
                stat = total / runtimeList.size();

                logger.info("Stat Result -> minimum: " + min + ", maximum: " + max + " , stat: " + stat);
            } else {
                logger.info("Runtime List is null");
            }
        } catch(Exception e) {
            logger.error(e.getMessage());
        }


    }




}
