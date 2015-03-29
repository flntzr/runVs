package com.springapp.transactional;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by franschl on 29.03.15.
 */
public class Tiles {
    // assumes that .../{lat}/{lon}.zip
    public static byte[] getTile(int lat, int lon) throws IOException {
        // TODO set proper file location
        String dirPath = "/home/franschl/Documents/Studienarbeit/tiles/" + lat + "/";
        String fileName = lon + ".zip";
        byte[] bytes;

        /*BufferedReader br = new BufferedReader(new FileReader(dirPath + fileName));
        String sCurrentLine;

        while ((sCurrentLine = br.read()) != null) {
            sCurrentLine+=sCurrentLine;
        }*/

        bytes = FileUtils.readFileToByteArray(new File(dirPath + fileName));

        return bytes;
    }
}
