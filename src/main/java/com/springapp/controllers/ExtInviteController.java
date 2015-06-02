package com.springapp.controllers;

import com.springapp.dto.CreateExtInviteRequest;
import com.springapp.exceptions.ExtInviteNotFoundException;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.ExtInvDAO;
import com.springapp.hibernate.GroupDAO;
import com.springapp.transactional.ExtInvites;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class ExtInviteController {

    final static Logger logger = Logger.getLogger(ExtInviteController.class);

    @RequestMapping(value = "extinvite", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<ExtInvDAO>> getExtInvites() {
        try {
            return new ResponseEntity<>(ExtInvites.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "user/{uid}/extinvite/accept/{pin}", method = RequestMethod.POST)
    public ResponseEntity<GroupDAO> acceptInvite(@PathVariable("uid") int userID, @PathVariable("pin") int pin) {
        try {
            return new ResponseEntity<>(ExtInvites.attemptGroupJoin(userID, pin), HttpStatus.CREATED);
        } catch (ExtInviteNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "extinvite", method = RequestMethod.POST)
    public ResponseEntity<Integer> createExtInvite(@RequestBody CreateExtInviteRequest request) {
        try {
            return new ResponseEntity<>(ExtInvites.createExtInv(request).getPin(), HttpStatus.OK);
        } catch (GroupNotFoundException|UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "extinvite/{invID}", method = RequestMethod.GET)
    public ResponseEntity<ExtInvDAO> getExtInviteByID(@PathVariable("invID") int extInvID) {
        try {
            return new ResponseEntity<>(ExtInvites.getByID(extInvID), HttpStatus.OK);
        } catch (ExtInviteNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
