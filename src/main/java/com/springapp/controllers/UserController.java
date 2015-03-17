package com.springapp.controllers;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UsersEntity;
import com.springapp.hibernatetx.Users;
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
	public ResponseEntity<UsersEntity> create(@RequestBody final UsersEntity user) {
		try {
			UsersEntity createdUser = Users.createUser(user);
			HttpHeaders headers = new HttpHeaders();

			// header refers to newly created user
			headers.add("Location", "/user/" + createdUser.getUserID());

		return new ResponseEntity<UsersEntity>(createdUser, headers, HttpStatus.CREATED);
		} catch (UnsupportedEncodingException e) {
			return new ResponseEntity<UsersEntity>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NonUniqueResultException e) {
			return new ResponseEntity<UsersEntity>(HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody public ArrayList<UsersEntity> getAll() {
		return Users.getUserList();
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@ResponseBody public ResponseEntity<UsersEntity> getByID(@PathVariable("id") int id) {
		try {
			return new ResponseEntity<UsersEntity>(Users.getUser(id), HttpStatus.OK);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<UsersEntity>(HttpStatus.NOT_FOUND);
		}
	}
}