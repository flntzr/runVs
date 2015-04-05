package com.springapp.controllers;

import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Groups;
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
    public ResponseEntity<ArrayList<UserDAO>> getAll(@PathVariable("id") int gID) {
        try {
            return new ResponseEntity<ArrayList<UserDAO>>(Groups.getMembers(gID), HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<ArrayList<UserDAO>>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{gid}/user/{uid}", method = RequestMethod.POST)
    public ResponseEntity<GroupDAO> addMember(@PathVariable("gid") int gID, @PathVariable("uid") int uID) {
        try {
            return new ResponseEntity<>(Groups.addMember(gID, uID), HttpStatus.OK);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group/{gid}/user/{uid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeMember(@PathVariable("gid") int gID, @PathVariable("uid") int uID) {
        try {
            Groups.removeMember(gID, uID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group/{id}/admin", method = RequestMethod.GET)
    public ResponseEntity<UserDAO> getAdmin(@PathVariable("id") int gID) {
        UserDAO admin;
        try {
            admin = Groups.getAdmin(gID);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{gid}/user/{uid}", method = RequestMethod.GET)
    public ResponseEntity<UserDAO> getAll(@PathVariable("gid") int gID, @PathVariable("uid") int uID) {
        try {
            return new ResponseEntity<UserDAO>(Groups.getMember(gID, uID), HttpStatus.OK);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<UserDAO>(HttpStatus.NOT_FOUND);
        }
    }
}
