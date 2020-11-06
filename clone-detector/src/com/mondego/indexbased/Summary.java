package com.mondego.indexbased;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Properties;

public class Summary {
    private static final Logger logger = LogManager.getLogger(Summary.class);

    public static void main(String[] args)
            throws IOException, ParseException, InterruptedException {

        Properties properties = new Properties();

        properties.setProperty("RESULT_DIR_PATH", "C:\\Users\\mrdrivingduck\\Desktop\\emm\\");

        properties.setProperty("NODE_PREFIX", "NODE");
        properties.setProperty("QUERY_DIR_PATH", "query");
        properties.setProperty("OUTPUT_DIR", "output");
        properties.setProperty("DATASET_DIR_PATH", "input" + File.separator + "dataset");
        properties.setProperty("IS_GEN_CANDIDATE_STATISTICS", "false");
        properties.setProperty("IS_STATUS_REPORTER_ON", "true");
        properties.setProperty("LOG_PROCESSED_LINENUMBER_AFTER_X_LINES", "50");
        properties.setProperty("MIN_TOKENS", "20");
        properties.setProperty("MAX_TOKENS", "500000");
        properties.setProperty("IS_SHARDING", "true");
        properties.setProperty("SHARD_MAX_NUM_TOKENS", "65,100,300,500000");
        properties.setProperty("BTSQ_THREADS", "4");
        properties.setProperty("BTIIQ_THREADS", "4");
        properties.setProperty("BTFIQ_THREADS", "4");
        properties.setProperty("QLQ_THREADS", "4");
        properties.setProperty("QBQ_THREADS", "4");
        properties.setProperty("QCQ_THREADS", "4");
        properties.setProperty("VCQ_THREADS", "16");
        properties.setProperty("RCQ_THREADS", "4");

        String cmd[] = new String[] { "init", "index", "merge", "search" };
        for (int i = 0; i < cmd.length; i++) {
            String[] arg = new String[2];
            arg[0] = cmd[i];
            arg[1] = "9";

            // InputStreamReader isr = null;
            // logger.info("reading Q values from properties file");
            // String propertiesPath = System.getProperty("properties.location");
            // logger.debug("propertiesPath: " + propertiesPath);
            // FileInputStream fis = new FileInputStream(propertiesPath);
            // isr = new InputStreamReader(fis, "UTF-8");
            // properties.load(isr);
            if (arg[0].equals("init") || arg[0].equals("index") || arg[0].equals("search")) {
                //SearchManager searchManager = new SearchManager(arg);
                SearchManager.stepInitIndexSearch(arg, properties);
            } else {
                //IndexMerger indexMerger = new IndexMerger();
                IndexMerger.stepMerge(arg, properties);
            }
        }

    }
}
