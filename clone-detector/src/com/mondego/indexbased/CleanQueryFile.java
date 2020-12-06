package com.mondego.indexbased;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CleanQueryFile {
    private String path2Afile = "";
    private String path2Bfile = "";
    private String path2Tfile = "";

    private List<Tuple<Integer, String>> getDatafromFile(String path2file) {

        String path = path2file;
        BufferedReader reader = null;

        List<Tuple<Integer, String>> ans = new ArrayList<>();
        Integer cnt = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                cnt += 1;
                ans.add(new Tuple(cnt, tempString));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ans;
    }

    public void clean(String path2A, String path2B) {
        path2Afile = path2A;
        path2Bfile = path2B;
        path2Tfile = path2A + ".fix";

        List<Tuple<Integer, String>> textInBfile = getDatafromFile(path2Bfile);

        if (textInBfile.size() == 0) {
            return;
        }

        List<Tuple<Integer, String>> textInAfile = getDatafromFile(path2Afile);

        textInAfile.sort(new Comparator<Tuple<Integer, String>>() {
            @Override
            public int compare(Tuple<Integer, String> o1, Tuple<Integer, String> o2) {
                return o1.b.compareTo(o2.b);
            }
        });
        textInBfile.sort(new Comparator<Tuple<Integer, String>>() {
            @Override
            public int compare(Tuple<Integer, String> o1, Tuple<Integer, String> o2) {
                return o1.b.compareTo(o2.b);
            }
        });

        System.out.println("fileA: " + textInAfile.size());
        System.out.println("fileB: " + textInBfile.size());

        // i -> Afile
        // j -> Bfile
        int i = 0, j = 0;
        int res = 0;
        while (i < textInAfile.size()) {
            res = textInAfile.get(i).b.compareTo(textInBfile.get(j).b);
            if (res < 0) { // strA < strB
                // pass
            } else if (res > 0) { // strA > strB
                j += 1;
                if (j < textInBfile.size()) {
                    continue;
                } else {
                    break;
                }
            } else {// res == 0
                // remove item in A file
                textInAfile.get(i).a = 0;
            }
            i += 1;
        }
        textInAfile.sort(new Comparator<Tuple<Integer, String>>() {
            @Override
            public int compare(Tuple<Integer, String> o1, Tuple<Integer, String> o2) {
                int res = 0;
                if (o1.a.equals(o2.a)) {
                    // pass
                } else {
                    if (o1.a > o2.a) {
                        res = 1;
                    } else {
                        res = -1;
                    }
                }
                return res;
            }
        });

        // ignore lines in A file
        i = 0;
        while (textInAfile.get(i).a == 0) {
            i += 1;
        }
        System.out.println("clean lines: " + i);

        // output ans to new file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path2Tfile));
            while (i < textInAfile.size()) {
                out.write(textInAfile.get(i).b + "\n");
                i++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
