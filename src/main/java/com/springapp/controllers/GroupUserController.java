package com.springapp.controllers;

import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UsersEntity;
import com.springapp.hibernatetx.Groups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class GroupUserController {
    @RequestMapping(value = "group/{id}/user", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<UsersEntity>> getAll(@PathVariable("id") int gID) {
        try {
            return new ResponseEntity<ArrayList<UsersEntity>>(Groups.getMembers(gID), HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<ArrayList<UsersEntity>>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{id}/admin", method = RequestMethod.GET)
    public ResponseEntity<UsersEntity> getAdmin(@PathVariable("id") int gID) {
        UsersEntity admin = null;
        try {
            admin = Groups.getAdmin(gID);
            return new ResponseEntity<UsersEntity>(admin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<UsersEntity>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{gid}/user/{uid}", method = RequestMethod.GET)
    public ResponseEntity<UsersEntity> getAll(@PathVariable("gid") int gID, @PathVariable("uid") int uID) {
        try {
            return new ResponseEntity<UsersEntity>(Groups.getMember(gID, uID), HttpStatus.OK);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<UsersEntity>(HttpStatus.NOT_FOUND);
        }
    }
}
