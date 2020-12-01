package com.mondego.indexbased;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MyThread implements Runnable{ // 实现Runnable接口，作为线程的实现类
    private String name ;       // 表示线程的名称
    public MyThread(String name){
        this.name = name ;      // 通过构造方法配置name属性
    }
    public void run(){  // 覆写run()方法，作为线程 的操作主体


        List<Map<String, Object>> detectList = new ArrayList<>();

        List<String> zipID = new ArrayList<>();
        List<Map<String, Object>> openSourceLibrary = new ArrayList<>();

        Gson objectGson = new Gson();
        Map<String, Object> object = new HashMap<>();
        object.put("method", "SourcererCC_code_clone");
        object.put("user_workspace", ReadJson.userPath);
        object.put("clone_detect_start_time", ReadJson.startTime);
        object.put("clone_detect_end_time", ReadJson.endTime);
        object.put("clone_detection_result", detectList);


        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(ReadJson.userPath + "NODE" + File.separator + "output" + ReadJson.threshold
                            + File.separator + "blocksclones_index_WITH_FILTER.txt"), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] fileID = line.split(",");
//            logger.info(line + 'y' + fileID[0] + ' ' + fileID[1] + ' ' + fileID[2] + ' ' + fileID[3]);

            File s = new File(ReadJson.userPath + "file-stats.stats");
            BufferedReader fileStats = null;
            try {
                fileStats = new BufferedReader(new FileReader(s));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileStatus = null;
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

            while (true) {
                try {
                    if (!((done < 4) && ((fileStatus = fileStats.readLine()) != null))) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
//        logger.info(objectGson.toJson(object));
//        logger.info(zipID);
//        return objectGson.toJson(object);

    }
};
public class ReadJson {
    public static String userPath;
    public static String threshold;
    public static String startTime;
    public static String endTime;

    public static void main(String[] args) {
        MyThread mt1 = new MyThread("线程A ");    // 实例化对象
        MyThread mt2 = new MyThread("线程B ");    // 实例化对象
        Thread t1 = new Thread(mt1);       // 实例化Thread类对象
        Thread t2 = new Thread(mt2);       // 实例化Thread类对象
        t1.start();    // 启动多线程
        t2.start();    // 启动多线程
    }
}


