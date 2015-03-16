package com.springapp.controllers;

import com.springapp.hibernate.RunsEntity;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class RunController {

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.POST)
    //first only take care of file upload, then everything else
    public ResponseEntity<RunsEntity> createRun(@RequestParam MultipartFile file, @PathVariable("id") int groupID) {
        try {
            uploadFile(file);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploadException e) {
            // File is empty
            return new ResponseEntity<RunsEntity>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<RunsEntity>(HttpStatus.OK);
    }

    public String uploadFile(MultipartFile file) throws IOException, FileUploadException {
        String dirPath = "/home/franschl/Documents/Studienarbeit/uploadTest/";
        String fileName = String.valueOf(System.currentTimeMillis()) + ".gpx";
        if (file.isEmpty()) {
            throw new FileUploadException();
        }
        byte[] bytes = file.getBytes();

        //file name collision handling, though it is very unlikely
        while (Files.exists(Paths.get(dirPath + fileName))) {
            fileName = String.valueOf(System.currentTimeMillis()) + ".gpx";
        }

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(dirPath + fileName)));
        stream.write(bytes);
        stream.close();
        return dirPath + fileName;
    }
}
