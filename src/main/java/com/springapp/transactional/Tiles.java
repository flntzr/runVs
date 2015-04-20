package com.springapp.transactional;

import com.springapp.Config;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by franschl on 29.03.15.
 */
public class Tiles {
    // assumes format .../{lat}/{lon}.zip
    public static byte[] getTile(int lat, int lon) throws IOException {
        byte[] bytes;
        String dirPath = Config.getValue("tileDir") + lat + "/";
        String fileName = lon + ".zip";

        bytes = FileUtils.readFileToByteArray(new File(dirPath + fileName));

        return bytes;
    }
}
