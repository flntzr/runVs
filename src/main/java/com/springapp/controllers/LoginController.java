package com.springapp.controllers;

import com.springapp.authentication.TokenProvider;
import com.springapp.dto.LoginRequest;
import com.springapp.dto.LoginTokenResponse;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by franschl on 09.04.15.
 */
@RestController
public class LoginController {

    final static Logger logger = Logger.getLogger(LoginController.class);

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<LoginTokenResponse> login(@RequestBody LoginRequest request) {
        TokenProvider tokenProvider = new TokenProvider();
        String token;
        long tokenExpiry = DateTime.now().plusDays(1).getMillis();

        try {
            UserDAO user = Users.getUserByName(request.getName());

            // login is correct
            if (Users.areCredentialsValid(user, request.getPassword())) {
                token = tokenProvider.getToken(user, tokenExpiry);
                Users.setToken(user.getUserID(), token, tokenExpiry);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (UserNotFoundException e) {
            logger.error(e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(new LoginTokenResponse(token), HttpStatus.OK);
    }
}
