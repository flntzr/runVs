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

import java.io.IOException;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class RunController {

    @RequestMapping(value = "/user/{id}/run", method = RequestMethod.POST)
    //file upload doesn't work with multipart, instead base64 the file into a String (src http://stackoverflow.com/questions/24486864/java-jackson-with-multipartfile)
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
}
