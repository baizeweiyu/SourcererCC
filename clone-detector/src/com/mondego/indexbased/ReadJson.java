package com.mondego.indexbased;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;

public class ReadJson {
//    private static String platform = "github";
//    private static String webPrefix = "https://github.com/";

    public static void output(String userPath, String threshold, String startTime, String endTime) {
        JsonFormat jf = new JsonFormat();
        jf.setUserPath(userPath); // my workspace
        jf.setThreshold(threshold);
        jf.setStartTime(startTime);
        jf.setEndTime(endTime);

        List<Map<String, Object>> detectList = new ArrayList<>();
        List<String> zipID = new ArrayList<>();
        List<Map<String, Object>> openSourceLibrary = new ArrayList<>();

        Map<String, Object> object = new HashMap<>(); // final result json string
        object.put("method", "SourcererCC_code_clone");
        object.put("user_workspace", jf.getUserPath());
        object.put("clone_detect_start_time", jf.getStartTime());
        object.put("clone_detect_end_time", jf.getEndTime());
        object.put("clone_detection_result", detectList);
        if (jf.getThreshold().equals("10.0")){
            object.put("similarity_range", "100%");
        }
        else if (jf.getThreshold().equals("8.0")){
            object.put("similarity_range", "80%-100%");
        }
        else {
            object.put("similarity_range", "40%-80%");
        }

        // read all the absolute paths
        BufferedReader path = null;
        String pathLine = null;
        List<String> pathList = new ArrayList<>();
        try {
            path = new BufferedReader(new InputStreamReader(new FileInputStream(jf.getUserPath() + "projects-list.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if ((pathLine = path.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            pathList.add(pathLine);
        }
        String []detectFilePath = pathList.get(0).split("/");
        object.put("clone_detection_project", detectFilePath[detectFilePath.length-1]);
        System.out.println("Read the Path Finished");

        File s = new File(jf.getUserPath() + "file-stats.stats");
        BufferedReader fileStats = null;
        String stateLine = null;
        String curFile = null;
        Map<String, FileInfo> fileInfoMap = new HashMap<>();
        Map<String, HashMap<String, FunctionInfo>> funcInfoMap = new HashMap<>();
        try {
            fileStats = new BufferedReader(new FileReader(s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if ((stateLine = fileStats.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] states = stateLine.split(",");
            if (states.length < 2) break;

            if (states[0].startsWith("f")) {
                curFile = states[1];
                fileInfoMap.put(curFile,
                        new FileInfo(states[0], states[1], states[2], states[3], states[4], Integer.parseInt(states[5]),
                                Integer.parseInt(states[6]), Integer.parseInt(states[7]), Integer.parseInt(states[8])));
            } else if (states[0].startsWith("b")) {
                if (funcInfoMap.containsKey(curFile)) {
                    funcInfoMap.get(curFile).put(states[1], new FunctionInfo(states[0], states[1], states[2],
                            Integer.parseInt(states[3]), Integer.parseInt(states[4]), Integer.parseInt(states[5]),
                            Integer.parseInt(states[6]), Integer.parseInt(states[7])));
                } else {
                    HashMap<String, FunctionInfo> tmp = new HashMap<>();
                    tmp.put(states[1], new FunctionInfo(states[0], states[1], states[2],
                            Integer.parseInt(states[3]), Integer.parseInt(states[4]), Integer.parseInt(states[5]),
                            Integer.parseInt(states[6]), Integer.parseInt(states[7])));
                    funcInfoMap.put(curFile, tmp);
                }
            }
        }
        System.out.println(funcInfoMap.size());
        System.out.println(fileInfoMap.size());
        System.out.println("Read File States Finished!");

        // read block file
        // Project1ID Func1ID Project2ID Func2ID
        BufferedReader br = null;
        String queryClones = jf.getUserPath() + "NODE" + File.separator + "output" + jf.getThreshold()
                + File.separator + "queryclones_index_WITH_FILTER.txt";
        String queryClonesFix = jf.getUserPath() + "NODE" + File.separator + "output" + jf.getThreshold()
                + File.separator + "queryclones_index_WITH_FILTER.txt.fix";
        try {
            if (threshold.equals("10.0")) {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(queryClones), StandardCharsets.UTF_8));
            }
            else {
                File file = new File(queryClonesFix);
                if (file.exists()) {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(queryClonesFix), StandardCharsets.UTF_8));
                }
                else {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(queryClones), StandardCharsets.UTF_8));
                }
            }
//            br = new BufferedReader(new InputStreamReader(new FileInputStream(jf.getUserPath() + "tmpblock.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Read Block Clone File Finished!");

        String line = null;
        int cnt = 0;
        while (true) {
            cnt++;
            try {
                assert br != null;
                if ((line = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert line != null;
            String[] fileID = line.split(",");


            if (zipID.contains(fileID[2])) {
                int index = zipID.indexOf(fileID[2]);
                int number = Integer.parseInt(openSourceLibrary.get(index).get("detect_file_number").toString());
                number += 1;
                openSourceLibrary.get(index).put("detect_file_number", number);
            }

            Map<String, Object> result = new HashMap<>();
            Map<String, Object> cloneDetectResult = new HashMap<>();
            int fID = Integer.parseInt(fileID[2]);
            String curFilePath = pathList.get(fID - 11);
            int lastLoc = curFilePath.lastIndexOf("/");
            String libPath = curFilePath.substring(0, lastLoc+1);
            if (!zipID.contains(fileID[2])) {
                zipID.add(fileID[2]);
                String []libFilePath = curFilePath.split("/");
                String proName = libFilePath[libFilePath.length-1];
                proName = proName.replace(".zip", "");

                // owner repo version
                String []name = proName.split(" ");
                String owner = name[0];
                String repo = name[1];
                String version = name[2];
//                String[] name = proName.split("-");
//                String owner = "owner";
//                String repo = name[0];
//                String version = name[1];
                //need to clarify
                String platform = null;
                platform = libFilePath[libFilePath.length-3];

                Map<String, Object> libraryInformation = new HashMap<>();
                // libraryInformation.put("library_address", webPrefix + owner + "/" + repo);
                libraryInformation.put("library_platform", platform); // may be obtained by the path
                libraryInformation.put("library_owner", owner);
                libraryInformation.put("library_id", fID);
                libraryInformation.put("library_name", repo);
                libraryInformation.put("library_version", version);
                libraryInformation.put("detect_file_number", 1);
//                if(!object.containsKey("library_path")) {
//                    object.put("library_path", curFilePath.substring(0, lastLoc+1));  // may be put outside
//                }
                openSourceLibrary.add(libraryInformation);
            }

            // file paths
            String curFileNameAndPath = fileInfoMap.get(fileID[1].substring(5)).getAbsolutePath();
            String libFileNameAndPath = fileInfoMap.get(fileID[3].substring(5)).getAbsolutePath();
            int curFileLoc = fileInfoMap.get(fileID[1].substring(5)).getLOC();
            int libFileLoc = fileInfoMap.get(fileID[3].substring(5)).getLOC();
            curFileNameAndPath = curFileNameAndPath.replace("\"", " ");
            libFileNameAndPath = libFileNameAndPath.replace("\"", " ");
            cloneDetectResult.put("file_name_and_path", curFileNameAndPath.replace(pathList.get(0), "").trim());
            result.put("file_name_and_path", libFileNameAndPath.replace(libPath, "").trim());

            String mmm = fileID[1].substring(5);
            // test code info
            cloneDetectResult.put("function_id", funcInfoMap.get(mmm).get(fileID[1]).getFunctionID());
            cloneDetectResult.put("LOC", funcInfoMap.get(mmm).get(fileID[1]).getLOC());
            cloneDetectResult.put("start_line", funcInfoMap.get(mmm).get(fileID[1]).getStartLine());
            cloneDetectResult.put("end_line", funcInfoMap.get(mmm).get(fileID[1]).getEndLine());
            cloneDetectResult.put("percentage", funcInfoMap.get(mmm).get(fileID[1]).getLOC() * 1.0 / curFileLoc * 100);
            mmm = null;

            // lib code info
            String ttt = fileID[3].substring(5);
            result.put("lib_id", Integer.parseInt(fileID[2]));
            result.put("function_id", funcInfoMap.get(ttt).get(fileID[3]).getFunctionID());
            result.put("LOC", funcInfoMap.get(ttt).get(fileID[3]).getLOC());
            result.put("start_line", funcInfoMap.get(ttt).get(fileID[3]).getStartLine());
            result.put("end_line", funcInfoMap.get(ttt).get(fileID[3]).getEndLine());
            result.put("percentage", funcInfoMap.get(ttt).get(fileID[3]).getLOC() * 1.0 / libFileLoc * 100);
            ttt = null;

//            fileResult.put("result", result);
            cloneDetectResult.put("file_result", result);
            detectList.add(cloneDetectResult);
//            System.out.println(cnt);
        }

        Collections.sort(openSourceLibrary, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (int)o1.get("library_id") - (int)o2.get("library_id");
            }
        });

        for(int i = 0; i < detectList.size(); i++) {
            int idx = -1;
            int curLibID = (int)((HashMap<String, Object>)detectList.get(i).get("file_result")).get("lib_id");
            int left = 0;
            int right = openSourceLibrary.size()-1;
            while(left <= right) {
                int half = (right - left)/2 + left;
                int halfVal = (int)openSourceLibrary.get(half).get("library_id");
                if(halfVal < curLibID) left = half + 1;
                else if(halfVal > curLibID) right = half - 1;
                else {idx = half;break;}
            }
            ((HashMap<String, Object>) detectList.get(i).get("file_result")).put("idx", idx);
            ((HashMap<String, Object>) detectList.get(i).get("file_result")).remove("lib_id");
        }

        object.put("open_source_library", openSourceLibrary);
        object.put("clone_detection_result", detectList);
        Gson gson = new Gson();
        String str = gson.toJson(object);
//        System.out.println(cnt);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(jf.getUserPath() + "output" + threshold + ".json"));
            out.write(str);
            out.close();
        } catch (IOException e) {
        }
    }

}

// outer parameters
class JsonFormat {
    private String userPath;
    private String threshold;
    private String startTime;
    private String endTime;

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

// file information class
class FileInfo {
    private String projectID;
    private String fileID;
    private String absolutePath;
    private String relativePath;
    private String hash;
    private int bytes;
    private int lines;
    private int LOC;
    private int SLOC;

    public FileInfo(String projectID, String fileID, String absolutePath, String relativePath, String hash, int bytes, int lines, int LOC, int SLOC) {
        this.projectID = projectID;
        this.fileID = fileID;
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.hash = hash;
        this.bytes = bytes;
        this.lines = lines;
        this.LOC = LOC;
        this.SLOC = SLOC;
    }

    public String getFileID() {
        return fileID;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getHash() {
        return hash;
    }

    public int getBytes() {
        return bytes;
    }

    public int getLines() {
        return lines;
    }

    public int getLOC() {
        return LOC;
    }

    public int getSLOC() {
        return SLOC;
    }
}

// function information class
class FunctionInfo {
    private String projectID;
    private String functionID;
    private String hash;
    private int lines;
    private int LOC;
    private int SLOC;
    private int startLine;
    private int endLine;

    public FunctionInfo(String projectID, String functionID, String hash, int lines, int LOC, int SLOC, int startLine, int endLine) {
        this.projectID = projectID;
        this.functionID = functionID;
        this.hash = hash;
        this.lines = lines;
        this.LOC = LOC;
        this.SLOC = SLOC;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getFunctionID() {
        return functionID;
    }

    public String getHash() {
        return hash;
    }

    public int getLines() {
        return lines;
    }

    public int getLOC() {
        return LOC;
    }

    public int getSLOC() {
        return SLOC;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }
}



