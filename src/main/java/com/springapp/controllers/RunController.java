package com.springapp.controllers;

import com.springapp.hibernate.RunsEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class RunController {

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.POST)
    //first only take care of file upload, then everything else
    public ResponseEntity<RunsEntity> createRun(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<RunsEntity>(HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            //TODO change this to proper locationg
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("/home/franschl/Documents/Studienarbeit/uploadTest/uploaded.jpg")));
            stream.write(bytes);
            stream.close();
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<RunsEntity>(HttpStatus.OK);
    }
}
