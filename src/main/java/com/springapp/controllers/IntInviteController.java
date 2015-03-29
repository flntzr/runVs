package com.springapp.controllers;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.IntInvitationsEntity;
import com.springapp.hibernatetx.IntInvites;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class IntInviteController {

    @RequestMapping(value = "user/{uid}/intinvite")
    public ResponseEntity<ArrayList<IntInvitationsEntity>> getIntInvites(@PathVariable("uid") int id) {
        try {
            return new ResponseEntity<ArrayList<IntInvitationsEntity>>(IntInvites.getByUserID(id), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<ArrayList<IntInvitationsEntity>>(HttpStatus.NOT_FOUND);
        } catch (Exception e)  {
            e.printStackTrace();
            return new ResponseEntity<ArrayList<IntInvitationsEntity>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
