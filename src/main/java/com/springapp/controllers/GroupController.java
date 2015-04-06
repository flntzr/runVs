package com.springapp.controllers;

import com.springapp.dto.CreateGroupRequest;
import com.springapp.exceptions.ConstraintViolatedException;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.transactional.Groups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // because empty response body
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConstraintViolatedException> handleValidationErrors(MethodArgumentNotValidException exception) {
        ConstraintViolatedException cve = new ConstraintViolatedException();
        for (ObjectError e : exception.getBindingResult().getAllErrors()) {
            cve.addMessage(e.getDefaultMessage());
        }
        return new ResponseEntity(cve, HttpStatus.BAD_REQUEST);
    }

}
