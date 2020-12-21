package com.mondego.indexbased;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Lines {
    public static void speed(String path, int time) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path + File.separator + "file-stats.stats"), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        int lineNum = 0;
        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
                String[] info = line.split(",");
                if (info[0].charAt(0) == 'f') {
                    lineNum += Integer.parseInt(info[6]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int speed = lineNum / (time / 1000);
        System.out.println(lineNum);
        System.out.println(speed);
    }
}
