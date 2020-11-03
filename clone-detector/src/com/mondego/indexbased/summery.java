package com.mondego.indexbased;

import com.mondego.indexbased.SearchManager;
import com.mondego.indexbased.IndexMerger;

import java.io.IOException;
import java.text.ParseException;

public class summery {
    public static void main(String[] args)
            //public static void main(String[] args)
            throws IOException, ParseException, InterruptedException {
        String[] arg = new String[2];
        arg[0] = "init";
        arg[1] = "9";
        if(arg[0] == "init" || arg[0] == "index" || arg[0] == "search")
        {
            SearchManager searchManager = new SearchManager(arg);
            searchManager.stepInitIndexSearch(arg);
        }
        else
        {
            IndexMerger indexMerger = new IndexMerger();
            indexMerger.stepMerge(arg);
        }

    }
}
