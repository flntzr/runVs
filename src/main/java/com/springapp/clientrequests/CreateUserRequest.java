package com.springapp.clientrequests;

/**
 * Created by franschl on 02.04.15.
 */
public class CreateUserRequest {
    private String nick;
    private String email;
    private String password;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
