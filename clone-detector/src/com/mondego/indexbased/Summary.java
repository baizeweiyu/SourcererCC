package com.mondego.indexbased;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Properties;

public class Summary {
    private static final Logger logger = LogManager.getLogger(Summary.class);
    public static void main(String[] args)
            //public static void main(String[] args)
            throws IOException, ParseException, InterruptedException {
        String[] arg = new String[2];
        arg[0] = "search";
        arg[1] = "9";
        Properties properties = new Properties();
        InputStreamReader isr = null;
        logger.info("reading Q values from properties file");
        String propertiesPath = System.getProperty("properties.location");
        logger.debug("propertiesPath: " + propertiesPath);
        FileInputStream fis = new FileInputStream(propertiesPath);
        isr = new InputStreamReader(fis, "UTF-8");
        properties.load(isr);
        if(arg[0].equals("init") || arg[0].equals("index") || arg[0].equals("search"))
        {
            //SearchManager searchManager = new SearchManager(arg);
            SearchManager.stepInitIndexSearch(arg, properties);
        }
        else
        {
            //IndexMerger indexMerger = new IndexMerger();
            IndexMerger.stepMerge(arg, properties);
        }

    }
}
