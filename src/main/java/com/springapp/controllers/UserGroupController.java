package com.springapp.controllers;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.transactional.Groups;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by franschl on 5/26/15.
 */

@RestController
public class UserGroupController {

    final static Logger logger = Logger.getLogger(UserGroupController.class);

    @RequestMapping(value = "user/{id}/group", method = RequestMethod.GET)
    public ResponseEntity<Set<GroupDAO>> getGroupsByUser(@PathVariable("id") int userID) {
        try {
            return new ResponseEntity<>(Groups.getGroupsWithUserInIt(userID), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
