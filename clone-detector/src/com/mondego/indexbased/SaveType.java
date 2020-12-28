package com.mondego.indexbased;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SaveType {
    public static void type(String userpath, String threshold) throws IOException {
        BufferedReader br = null;
        String queryClones = userpath + "NODE" + File.separator + "output" + threshold
                + File.separator + "queryclones_index_WITH_FILTER.txt";
        String queryClonesFix = userpath + "NODE" + File.separator + "output" + threshold
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

        BufferedReader fs = null;  //file-stats.stats


        BufferedWriter out = new BufferedWriter(new FileWriter(userpath + "output"+threshold+"type.txt"));
//        out.write("");

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] fileID = line.split(",");
            String[] name1 = null;
            String[] name2 = null;
            String ID1 = null;
            String ID2 = null;
            fs = new BufferedReader(new InputStreamReader(new FileInputStream(userpath + "file-stats.stats"), StandardCharsets.UTF_8));
            String fileStats = null;
            int done = 0;
            while (((fileStats = fs.readLine()) != null) && done < 2){
                String[] fileInfo = fileStats.split(",");
                if (fileID[1].equals(fileInfo[1])) {
                    name1 = fileInfo[2].split("/");
                    ID1 = name1[name1.length-1];
                    ID1 = ID1.replace(".java\"", "");
                    done += 1;
                }
                if (fileID[3].equals(fileInfo[1])) {
                    name2 = fileInfo[2].split("/");
                    ID2 = name2[name2.length-1];
                    ID2 = ID2.replace(".java\"", "");
                    done += 1;
                }
            }
            if (threshold.equals("10.0"))
                out.write(ID1 + " " + ID2 + " " + "1\n");
            if (threshold.equals("8.0"))
                out.write(ID1 + " " + ID2 + " " + "2\n");
            if (threshold.equals("4.0"))
                out.write(ID1 + " " + ID2 + " " + "3\n");
        }
        out.close();
    }
}
