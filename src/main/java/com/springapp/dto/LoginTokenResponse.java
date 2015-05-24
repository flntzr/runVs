package com.springapp.dto;

/**
 * Created by franschl on 09.04.15.
 */
public class LoginTokenResponse {
    private String token;
    private int userID;

    public LoginTokenResponse(String token, int userID) {
        this.token = token;
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
