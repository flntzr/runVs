package com.springapp.controllers;

import com.springapp.clientrequests.CreateExtInviteRequest;
import com.springapp.hibernate.ExtInvDAO;
import com.springapp.transactional.ExtInvites;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class ExtInviteController {

    @RequestMapping(value = "extinvite", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<ExtInvDAO>> getExtInvites(CreateExtInviteRequest request) {
        try {
            return new ResponseEntity<>(ExtInvites.getAll(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
