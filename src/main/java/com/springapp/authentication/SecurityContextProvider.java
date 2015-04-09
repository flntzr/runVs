package com.springapp.authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by franschl on 06.04.15.
 */
public class SecurityContextProvider {
    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }
}
