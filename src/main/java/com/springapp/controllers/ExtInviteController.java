package com.springapp.controllers;

import com.springapp.clientrequests.CreateExtInviteRequest;
import com.springapp.exceptions.ExtInviteNotFoundException;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.ExtInvDAO;
import com.springapp.transactional.ExtInvites;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class ExtInviteController {

    @RequestMapping(value = "extinvite", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<ExtInvDAO>> getExtInvites() {
        try {
            return new ResponseEntity<>(ExtInvites.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "extinvite", method = RequestMethod.POST)
    public ResponseEntity<Integer> createExtInvite(@RequestBody CreateExtInviteRequest request) {
        try {
            return new ResponseEntity<>(ExtInvites.createExtInv(request).getPin(), HttpStatus.OK);
        } catch (GroupNotFoundException|UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "extinvite/{invID}", method = RequestMethod.GET)
    public ResponseEntity<ExtInvDAO> getExtInviteByID(@PathVariable("invID") int extInvID) {
        try {
            return new ResponseEntity<>(ExtInvites.getByID(extInvID), HttpStatus.OK);
        } catch (ExtInviteNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
