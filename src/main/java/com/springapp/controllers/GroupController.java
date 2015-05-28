package com.springapp.controllers;

import com.springapp.dto.CreateGroupRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.transactional.Groups;
import org.apache.log4j.Logger;
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

    final static Logger logger = Logger.getLogger(GroupController.class);

    @RequestMapping(value = "group", method = RequestMethod.POST)
    public ResponseEntity<GroupDAO> create(@RequestBody @Valid CreateGroupRequest request) {
        try {
            return new ResponseEntity<GroupDAO>(Groups.createGroup(request), HttpStatus.CREATED);
        } catch(UserNotFoundException e) {
            return new ResponseEntity<GroupDAO>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<GroupDAO>> getAll() {
        try {
            return new ResponseEntity<>(Groups.getGroupList(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "group/{id}", method = RequestMethod.GET)
    public ResponseEntity<GroupDAO> getByID(@PathVariable("id") int id) {
        try  {
            return new ResponseEntity<GroupDAO>(Groups.getGroup(id), HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<GroupDAO>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "group/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") int id) {
        try  {
            Groups.deleteGroup(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // because empty response body
        } catch (GroupNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
