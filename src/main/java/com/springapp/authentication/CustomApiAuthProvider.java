package com.springapp.authentication;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

/**
 * Created by franschl on 06.04.15.
 */
public class CustomApiAuthProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDAO user = null;
        try {
            user = Users.getUserByName(authentication.getName());
        } catch (UserNotFoundException e) {
            throw new BadCredentialsException("Username not found");
        }

        return new org.springframework.security.core.userdetails.User(user.getNick(), user.getPassword(), Collections.<GrantedAuthority>emptySet());
    }
}
