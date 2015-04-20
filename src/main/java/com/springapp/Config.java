package com.springapp;

import org.apache.log4j.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.*;

/**
 * Created by franschl on 18.04.15.
 */
public class Config {
    final static Logger logger = Logger.getLogger(Config.class);

    private Config() {}

    public static String getValue(String name) throws IOException {
        String dirPath = "/var/lib/tomcat7/webapps/";
        String fileName = "ghostrunner_config.txt";
        BufferedReader br = new BufferedReader(new FileReader(dirPath + fileName));
        String sCurrentLine;

        while ((sCurrentLine = br.readLine()) != null) {
            if (sCurrentLine.matches(name + "=" + ".+")) {
                // convert "name=TEST" to "TEST"
                sCurrentLine = sCurrentLine.substring(name.length() + 1);
                return sCurrentLine;
            }
        }

        logger.error("Property " + "[" + name + "]" + " could not be found in config file at" + dirPath + fileName);
        throw new IOException();
    }
}
