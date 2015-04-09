package com.springapp.dto;

/**
 * Created by franschl on 09.04.15.
 */
public class LoginTokenResponse {
    private String token;

    public LoginTokenResponse(String token) {
        setToken(token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
