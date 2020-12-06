package com.mondego.indexbased;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Summary {
//    private static final Logger logger = LogManager.getLogger(Summary.class);

    public static void main(String[] args) {

        Properties properties = new Properties();
//        String userPath = "/home/xinxin/Desktop/code_clone/javatoken/";
        String userPath = args[0];
        properties.setProperty("RESULT_DIR_PATH", userPath);
        //need to get

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
        properties.setProperty("BTSQ_THREADS", "16");
        properties.setProperty("BTIIQ_THREADS", "16");
        properties.setProperty("BTFIQ_THREADS", "16");
        properties.setProperty("QLQ_THREADS", "16");
        properties.setProperty("QBQ_THREADS", "16");
        properties.setProperty("QCQ_THREADS", "16");
        properties.setProperty("VCQ_THREADS", "64");
        properties.setProperty("RCQ_THREADS", "16");


        String[] cmd = new String[]{"init", "index", "merge", "search"};
        String[] type = new String[]{"10.0", "8.0", "4.0"};
        String[] arg = new String[2];
        for (int i = 0; i < 3; i++) {
//            long start = System.currentTimeMillis();
            Date startDay = new Date();
            SimpleDateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = startDateFormat.format(startDay);
            System.out.println(type[i]);

            for (String s : cmd) {
                arg[0] = s;
                arg[1] = type[i];

                // InputStreamReader isr = null;
                // logger.info("reading Q values from properties file");
                // String propertiesPath = System.getProperty("properties.location");
                // logger.debug("propertiesPath: " + propertiesPath);
                // FileInputStream fis = new FileInputStream(propertiesPath);
                // isr = new InputStreamReader(fis, "UTF-8");
                // properties.load(isr);
                if (arg[0].equals("init") || arg[0].equals("index") || arg[0].equals("search")) {
                    //SearchManager searchManager = new SearchManager(arg);
                    try {
                        SearchManager.stepInitIndexSearch(arg, properties);
                    } catch (IOException | ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //IndexMerger indexMerger = new IndexMerger();
                    try {
                        IndexMerger.stepMerge(arg, properties);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("over one");
            Date endDay = new Date();
            SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String endTime = endDateFormat.format(endDay);
//            long end = System.currentTimeMillis();
//            Lines.speed(end - start);
            if (i > 0) {
                CleanQueryFile handler = new CleanQueryFile();
//                System.out.println(System.getProperty("user.dir"));

                String p2A = properties.getProperty("RESULT_DIR_PATH") + "NODE" + File.separator
                        + "output" + type[i] + File.separator + "queryclones_index_WITH_FILTER.txt";
                String p2B = properties.getProperty("RESULT_DIR_PATH") + "NODE" + File.separator
                        + "output" + type[i-1] + File.separator + "queryclones_index_WITH_FILTER.txt";

                handler.clean(p2A, p2B);

            }
            ReadJson.output(properties.getProperty("RESULT_DIR_PATH"), type[i], startTime, endTime);
        }

//        readJsonResult(properties.getProperty("RESULT_DIR_PATH"), type[0], startTime, endTime);

    }
}
