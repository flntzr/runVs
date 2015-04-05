package com.springapp.controllers;

import com.springapp.clientrequests.CreateGroupRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.transactional.Groups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class GroupController {

    @RequestMapping(value = "group", method = RequestMethod.POST)
    public ResponseEntity<GroupDAO> create(@RequestBody @Valid CreateGroupRequest request) {
        try {
            return new ResponseEntity<GroupDAO>(Groups.createGroup(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<GroupDAO>> getAll() {
        try {
            return new ResponseEntity<>(Groups.getGroupList(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group/{id}", method = RequestMethod.GET)
    public ResponseEntity<GroupDAO> getByID(@PathVariable("id") int id) {
        try  {
            return new ResponseEntity<GroupDAO>(Groups.getGroup(id), HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<GroupDAO>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") int id) {
        try  {
            Groups.deleteGroup(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
