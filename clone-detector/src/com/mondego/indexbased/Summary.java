package com.mondego.indexbased;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class Summary {
    private static final Logger logger = LogManager.getLogger(Summary.class);

    public static void readJson(Properties properties, String th)
            throws IOException{

        String userPath = properties.getProperty("RESULT_DIR_PATH");

//        Gson resultGson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("function id", "");
        result.put("file name and path", "");
        result.put("similarity", ">=" + th + "0%");
        result.put("LOC", "");
        result.put("total tokens", "");
        result.put("start line", "");
        result.put("end line", "");

//        Gson fileResultGson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, Object> fileResult = new LinkedHashMap<String, Object>();
        float t = Float.parseFloat(th);
        if ( (t - 8.0 >= 0.0) && (9.0 - t > 0.0) ){
            fileResult.put("type", "type3");
        }
        if ( (t - 9.0 >= 0.0) && (10.0 - t > 0.0) ){
            fileResult.put("type", "type2");
        }
        if ( t - 10.0 == 0.0 ){
            fileResult.put("type", "type1");
        }
        fileResult.put("result", result);

//        Gson detectResultGson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, Object> detectResult = new LinkedHashMap<String, Object>();
        detectResult.put("file name and path", "");
        detectResult.put("function id", "");
        detectResult.put("LOC", "");
        detectResult.put("total tokens", "");
        detectResult.put("start line", "");
        detectResult.put("end line", "");
        detectResult.put("file result", fileResult);

        List<Map<String, Object>> detectList = new ArrayList<Map<String, Object>>();

        Gson objectGson = new Gson();
        Map<String, Object> object = new HashMap<>();
        object.put("method", "SourcererCC code clone");
        object.put("user workspace", userPath);
        object.put("clone detection result", detectList);

//        File re = new File(userPath + "NODE/output"+th+".0/blocksclones_index_WITH_FILTER.txt");
//        BufferedReader br = new BufferedReader(new FileReader(re));
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(userPath + "NODE/output"+th+".0/blocksclones_index_WITH_FILTER.txt"), StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null){
            String[] fileID = line.split(",");
            logger.info(line+'y'+fileID[0]+' '+fileID[1]+' '+fileID[2]+' '+fileID[3]);

            File s = new File(userPath + "file_block_stats/files-stats-0.stats");
            BufferedReader fileStats = new BufferedReader(new FileReader(s));
            String file_s;
            int done = 0;
            while ((done < 4) && ((file_s = fileStats.readLine()) != null) ){
                String[] status = file_s.split(",");

                if (fileID[1].substring(5,12).equals(status[1])){
                    status[2] = status[2].replace('"', ' ');
                    detectResult.put("file name and path", status[2]);
                    done += 1;
                }
                else if (fileID[1].equals(status[1])){
                    detectResult.put("function id", status[1]);
                    detectResult.put("LOC", status[4]);
                    detectResult.put("start line", status[6]);
                    detectResult.put("end line", status[7]);
                    done += 1;
                }
                else if (fileID[3].substring(5,12).equals(status[1])){
                    status[2] = status[2].replace('"', ' ');
                    result.put("file name and path", status[2]);
                    done += 1;
                }
                else if (fileID[3].equals(status[1])){
                    result.put("function id", status[1]);
                    result.put("LOC", status[4]);
                    result.put("start line", status[6]);
                    result.put("end line", status[7]);
                    done += 1;
                }
            }
            fileResult.put("result", result);
            detectResult.put("file result", fileResult);
            detectList.add(detectResult);
            logger.info(detectList);
        }
        object.put("clone detection result", detectList);
        String jsonString = objectGson.toJson(object);
        System.out.println(jsonString);

        File fj = new File(userPath+"output.json");
        Writer o = new OutputStreamWriter(new FileOutputStream(fj));
//        String jsonString = object.toString();
        logger.info(jsonString);
        o.write(jsonString);
        o.close();


    }

    public static void main(String[] args)
            throws IOException, ParseException, InterruptedException {

        Properties properties = new Properties();

        properties.setProperty("RESULT_DIR_PATH", "/home/xinxin/Desktop/kuku/");

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

//        String cmd[] = new String[] { "init", "index", "merge", "search" };
//        for (int i = 0; i < cmd.length; i++) {
//            String[] arg = new String[2];
//            arg[0] = cmd[i];
//            arg[1] = "9";
//
//            // InputStreamReader isr = null;
//            // logger.info("reading Q values from properties file");
//            // String propertiesPath = System.getProperty("properties.location");
//            // logger.debug("propertiesPath: " + propertiesPath);
//            // FileInputStream fis = new FileInputStream(propertiesPath);
//            // isr = new InputStreamReader(fis, "UTF-8");
//            // properties.load(isr);
//            if (arg[0].equals("init") || arg[0].equals("index") || arg[0].equals("search")) {
//                //SearchManager searchManager = new SearchManager(arg);
//                SearchManager.stepInitIndexSearch(arg, properties);
//            } else {
//                //IndexMerger indexMerger = new IndexMerger();
//                IndexMerger.stepMerge(arg, properties);
//            }
//        }

        readJson(properties, "9");

    }
}
