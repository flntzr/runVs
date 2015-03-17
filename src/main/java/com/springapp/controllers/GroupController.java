package com.springapp.controllers;

import com.springapp.clientrequests.CreateGroupRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernatetx.Groups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class GroupController {

    @RequestMapping(value = "group", method = RequestMethod.POST)
    public ResponseEntity<GroupsEntity> create(@RequestBody CreateGroupRequest request) {
        return new ResponseEntity<GroupsEntity>(Groups.createGroup(request), HttpStatus.CREATED);
    }

    @RequestMapping(value = "group", method = RequestMethod.GET)
    public ArrayList<GroupsEntity> getAll() {
        return Groups.getGroupList();
    }

    @RequestMapping(value = "group/{id}", method = RequestMethod.GET)
    public ResponseEntity<GroupsEntity> getByID(@PathVariable("id") int id) {
        try  {
            return new ResponseEntity<GroupsEntity>(Groups.getGroup(id), HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<GroupsEntity>(HttpStatus.NOT_FOUND);
        }
    }

}
