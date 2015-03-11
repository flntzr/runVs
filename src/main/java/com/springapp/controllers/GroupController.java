package com.springapp.controllers;

import com.springapp.fromclient.CreateGroupRequest;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernatetransactions.Groups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}