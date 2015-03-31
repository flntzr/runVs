package com.springapp.controllers;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.hibernate.NonUniqueResultException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@RestController
public class UserController {

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public ResponseEntity<UserDAO> create(@RequestBody final UserDAO user) {
		try {
			UserDAO createdUser = Users.createUser(user);
			HttpHeaders headers = new HttpHeaders();

			// header refers to newly created user (might be taken out later)
			headers.add("Location", "/user/" + createdUser.getUserID());

		return new ResponseEntity<UserDAO>(createdUser, headers, HttpStatus.CREATED);
		} catch (UnsupportedEncodingException e) {
			return new ResponseEntity<UserDAO>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NonUniqueResultException e) {
			return new ResponseEntity<UserDAO>(HttpStatus.CONFLICT);
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
	public ResponseEntity<Void> delete(@PathVariable("uid") int userID) {
		try {
			Users.deleteUser(userID);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}