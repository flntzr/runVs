package com.springapp.controllers;

import com.springapp.dto.CreateRunRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.RunNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.RunDAO;
import com.springapp.transactional.Runs;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class RunController {

    final static Logger logger = Logger.getLogger(RunController.class);

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.POST)
    //cannot combine multipart upload with json, instead base64 the file into a String (src http://stackoverflow.com/questions/24486864/java-jackson-with-multipartfile)
    public ResponseEntity<RunDAO> createRun(@RequestBody @Valid CreateRunRequest request, @PathVariable("id") int userID) {
        try {
            Runs.createRun(request, userID);
        } catch(IOException e) {
            logger.error(e);
            return new ResponseEntity<RunDAO>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploadException|GroupNotFoundException e) {
            // File is empty or given userID matches no group
            logger.error(e);
            return new ResponseEntity<RunDAO>(HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<RunDAO>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<RunDAO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<RunDAO>(HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<RunDAO>> getAllRuns(@PathVariable("id") int userID) {
        try {
            return new ResponseEntity<ArrayList<RunDAO>>(Runs.getAllByUser(userID), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<ArrayList<RunDAO>>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<ArrayList<RunDAO>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/{userID}/run/{runID}", method = RequestMethod.GET)
    public ResponseEntity<RunDAO> getRun(@PathVariable("userID") int userID, @PathVariable("runID") int runID) {
        try {
            return new ResponseEntity<RunDAO>(Runs.getRunByID(userID, runID), HttpStatus.OK);
        } catch (RunNotFoundException|UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<RunDAO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/user/{userID}/run/{runID}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRun(@PathVariable("userID") int userID, @PathVariable("runID") int runID) {
        try {
            Runs.deleteRun(userID, runID);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (RunNotFoundException|UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
