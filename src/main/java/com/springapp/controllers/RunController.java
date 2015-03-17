package com.springapp.controllers;

import com.springapp.clientrequests.CreateRunRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.RunsEntity;
import com.springapp.hibernatetx.Runs;
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
    public ResponseEntity<RunsEntity> createRun(@RequestBody CreateRunRequest request, @PathVariable("id") int groupID) {
        try {
            Runs.createRun(request, groupID);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploadException|GroupNotFoundException e) {
            // File is empty or given groupID matches no group
            return new ResponseEntity<RunsEntity>(HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<RunsEntity>(HttpStatus.OK);
    }
}
