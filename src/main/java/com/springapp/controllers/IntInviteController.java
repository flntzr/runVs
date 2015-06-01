package com.springapp.controllers;

import com.springapp.dto.CreateIntInviteRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.InviteNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.IntInvDAO;
import com.springapp.transactional.IntInvites;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class IntInviteController {

    final static Logger logger = Logger.getLogger(IntInviteController.class);

    @RequestMapping(value = "user/{uid}/intinvite", method = RequestMethod.POST)
    public ResponseEntity<IntInvDAO> createIntInvite(@PathVariable("uid") int id, @RequestBody CreateIntInviteRequest request) {
        try {
            return new ResponseEntity<>(IntInvites.createIntInvite(request, id), HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (GroupNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "user/{uid}/intinvite", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<IntInvDAO>> getIntInvites(@PathVariable("uid") int id) {
        try {
            return new ResponseEntity<>(IntInvites.getByUserID(id), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e)  {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "user/{uid}/intinvite/{invID}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> getIntInvites(@PathVariable("uid") int userID, @PathVariable("invID") int invID) {
        try {
            IntInvites.deleteInvite(invID);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (InviteNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
