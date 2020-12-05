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
    private static final Logger logger = LogManager.getLogger(Summary.class);


    public static String readJsonResult(String userPath, String threshold, String startTime, String endTime)
            throws IOException {

//        for (String th : type) {
//
//        }
//        String threshold = "";
//        Map<String, String> result = new HashMap<>();
//        result.put("function_id", "");
//        result.put("file_name_and_path", "");
//        result.put("similarity", ">=" + threshold + "%");
//        result.put("LOC", "");
//        result.put("start_line", "");
//        result.put("end_line", "");

//        Map<String, Object> fileResult = new HashMap<>();
//        float thresholdVal = Float.parseFloat(threshold);
//        if ((thresholdVal - 8.0 >= 0.0) && (9.0 - thresholdVal > 0.0)) {
//            fileResult.put("type", "type3");
//        } else if ((thresholdVal - 9.0 >= 0.0) && (10.0 - thresholdVal > 0.0)) {
//            fileResult.put("type", "type2");
//        } else if (thresholdVal - 10.0 == 0.0) {
//            fileResult.put("type", "type1");
//        }
//        fileResult.put("result", result);

//        Map<String, Object> cloneDetectResult = new HashMap<>();
//        cloneDetectResult.put("file_name_and_path", "");
//        cloneDetectResult.put("function_id", "");
//        cloneDetectResult.put("LOC", "");
//        cloneDetectResult.put("start_line", "");
//        cloneDetectResult.put("end_line", "");
//        cloneDetectResult.put("file_result", fileResult);

        List<Map<String, Object>> detectList = new ArrayList<>();

        List<String> zipID = new ArrayList<>();
        List<Map<String, Object>> openSourceLibrary = new ArrayList<>();

        Gson objectGson = new Gson();
        Map<String, Object> object = new HashMap<>();
        object.put("method", "SourcererCC_code_clone");
        object.put("user_workspace", userPath);
        object.put("clone_detect_start_time", startTime);
        object.put("clone_detect_end_time", endTime);
        object.put("clone_detection_result", detectList);


        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(userPath + "NODE" + File.separator + "output" + threshold
                        + File.separator + "blocksclones_index_WITH_FILTER.txt"), StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            String[] fileID = line.split(",");
//            logger.info(line + 'y' + fileID[0] + ' ' + fileID[1] + ' ' + fileID[2] + ' ' + fileID[3]);

            File s = new File(userPath + "file-stats.stats");
            BufferedReader fileStats = new BufferedReader(new FileReader(s));
            String fileStatus;
            int done = 0;
            if (zipID.contains(fileID[2])) {
                int index = zipID.indexOf(fileID[2]);
                int number = Integer.parseInt(openSourceLibrary.get(index).get("dectect_file_number").toString());
                number += 1;
                openSourceLibrary.get(index).put("dectect_file_number", number);
            }

            Map<String, String> result = new HashMap<>();
            Map<String, Object> fileResult = new HashMap<>();
            Map<String, Object> cloneDetectResult = new HashMap<>();

            while ((done < 4) && ((fileStatus = fileStats.readLine()) != null)) {
                String[] status = fileStatus.split(",");

                if ((!zipID.contains(fileID[2])) && (status[0].equals("f" + fileID[2]))) {
                    zipID.add(fileID[2]);
                    String[] name = status[2].split("/");
                    name[1] = name[1].replace(".zip", "");
                    name = name[1].split("-");
                    Map<String, Object> libraryInformation = new HashMap<>();
                    libraryInformation.put("library_name", name[0]);
                    libraryInformation.put("library_version", name[1]);
                    libraryInformation.put("dectect_file_number", 1);
                    openSourceLibrary.add(libraryInformation);
                }

                if (fileID[1].substring(5).equals(status[1])) {
                    status[2] = status[2].replace('"', ' ');
                    cloneDetectResult.put("file_name_and_path", status[2]);
                    done += 1;
                } else if (fileID[1].equals(status[1])) {
                    cloneDetectResult.put("function_id", status[1]);
                    cloneDetectResult.put("LOC", status[4]);
                    cloneDetectResult.put("start_line", status[6]);
                    cloneDetectResult.put("end_line", status[7]);
                    done += 1;
                } else if (fileID[3].substring(5).equals(status[1])) {
                    status[2] = status[2].replace('"', ' ');
                    result.put("file_name_and_path", status[2]);
                    done += 1;
                } else if (fileID[3].equals(status[1])) {
//                    result.put("function_id", status[1]);

                    result.put("LOC", status[4]);
                    result.put("start_line", status[6]);
                    result.put("end_line", status[7]);
                    done += 1;
                }
            }
            fileResult.put("result", result);
            cloneDetectResult.put("file_result", fileResult);
            detectList.add(cloneDetectResult);
        }
        object.put("open_source_library", openSourceLibrary);
        object.put("clone_detection_result", detectList);
        logger.info(objectGson.toJson(object));
//        logger.info(zipID);
        return objectGson.toJson(object);
    }

    public static void main(String[] args)
            throws IOException, ParseException, InterruptedException {

        Properties properties = new Properties();
        String userPath = "/home/xinxin/Desktop/code_clone/fastJson/";
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
            long start = System.currentTimeMillis();
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
                    SearchManager.stepInitIndexSearch(arg, properties);
                } else {
                    //IndexMerger indexMerger = new IndexMerger();
                    IndexMerger.stepMerge(arg, properties);
                }
            }
            System.out.println("over one");
            Date endDay = new Date();
            SimpleDateFormat endDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String endTime = endDateFormat.format(endDay);
            long end = System.currentTimeMillis();
            Lines.speed(end - start);
            if (i == 1) {
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
