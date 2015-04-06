package com.springapp.controllers;

import com.springapp.dto.CreateUserRequest;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.hibernate.NonUniqueResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class UserController {

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public ResponseEntity<UserDAO> createUser(@RequestBody CreateUserRequest request) {
		try {
			return new ResponseEntity<>(Users.createUser(request), HttpStatus.CREATED);
		} catch (NonUniqueResultException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/user/{uid}", method = RequestMethod.PUT)
	public ResponseEntity<UserDAO> editUser(@RequestBody CreateUserRequest request, @PathVariable("uid") int userID) {
		try {
			return new ResponseEntity<>(Users.editUser(request, userID), HttpStatus.OK);
		} catch (NonUniqueResultException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ArrayList<UserDAO> getAll() {
		return Users.getUserList();
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<UserDAO> getByID(@PathVariable("id") int id) {
		try {
			return new ResponseEntity<UserDAO>(Users.getUser(id), HttpStatus.OK);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<UserDAO>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/user/{uid}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteUser(@PathVariable("uid") int userID) {
		try {
			Users.deleteUser(userID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}