package com.springapp.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by franschl on 02.04.15.
 */
public class CreateUserRequest {
    @Size(min = 4, max = 24, message = "Username must be between 4 and 24 characters long.")
    @NotBlank(message = "Username cannot be empty.")
    private String nick;
    @Email(message = "Not a valid e-mail.")
    private String email;
    @Size(min = 8, message = "Password must be 8 characters or longer.")
    @NotBlank(message = "Password cannot be empty.")
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
