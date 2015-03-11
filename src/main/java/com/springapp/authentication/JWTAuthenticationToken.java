package com.springapp.authentication;

import com.sun.jna.StringArray;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by franschl on 16.02.15.
 */
public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final Object principal;
    private Object details;

    Collection authorities;
    public JWTAuthenticationToken( String jwtToken) {
        super(null);
        super.setAuthenticated(true); // must use super, as we override

        this.principal = "AAAA";

        this.setDetailsAuthorities();

    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
    private void setDetailsAuthorities() {
        String username = principal.toString();
        ArrayList<String> sl = new ArrayList<>();
        sl.add("AA");
        authorities=(Collection) sl;

    }

    @Override
    public Collection getAuthorities() {
        return authorities;
    }
}
