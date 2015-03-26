package com.springapp.controllers;

import com.springapp.clientrequests.CreateRunRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.RunNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.RunsEntity;
import com.springapp.hibernatetx.Runs;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class RunController {

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.POST)
    //cannot combine multipart upload with json, instead base64 the file into a String (src http://stackoverflow.com/questions/24486864/java-jackson-with-multipartfile)
    public ResponseEntity<RunsEntity> createRun(@RequestBody CreateRunRequest request, @PathVariable("id") int userID) {
        try {
            Runs.createRun(request, userID);
        } catch(IOException e) {
            e.printStackTrace();
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploadException|GroupNotFoundException e) {
            // File is empty or given userID matches no group
            return new ResponseEntity<RunsEntity>(HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<RunsEntity>(HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<RunsEntity>> getAllRuns(@PathVariable("id") int userID) {
        try {
            return new ResponseEntity<ArrayList<RunsEntity>>(Runs.getAllByUser(userID), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<ArrayList<RunsEntity>>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ArrayList<RunsEntity>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/{userID}/run/{runID}", method = RequestMethod.GET)
    public ResponseEntity<RunsEntity> getRun(@PathVariable("userID") int userID, @PathVariable("runID") int runID) {
        try {
            return new ResponseEntity<RunsEntity>(Runs.getRunByID(userID, runID), HttpStatus.OK);
        } catch (RunNotFoundException e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<RunsEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
