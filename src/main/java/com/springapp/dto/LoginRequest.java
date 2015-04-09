package com.springapp.dto;

import com.sun.istack.internal.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created by franschl on 09.04.15.
 */
public class LoginRequest {
    @Size(min = 4, max = 24, message = "Username must be between 4 and 24 characters long.")
    @NotBlank(message = "Username cannot be empty.")
    private String name;
    @Size(min = 8, message = "Password must be 8 characters or longer.")
    @NotBlank(message = "Password cannot be empty.")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
