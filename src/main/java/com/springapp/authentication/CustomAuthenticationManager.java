package com.springapp.authentication;

import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by franschl on 07.04.15.
 */
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());
        UserDAO user;
        try {
            user = Users.getUserByName(username);
            if (Users.areCredentialsValid(user, password)) throw new BadCredentialsException("Password not valid");
        } catch (UserNotFoundException e) {
            throw new BadCredentialsException("Username not valid");
        }

        // TODO Debuggish
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}
