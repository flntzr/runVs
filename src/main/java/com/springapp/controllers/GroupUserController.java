package com.springapp.controllers;

import com.springapp.hibernate.UsersEntity;
import com.springapp.hibernatetx.Groups;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
@RestController
public class GroupUserController {
    @RequestMapping(value = "group/{id}/user", method = RequestMethod.GET)
    public ArrayList<UsersEntity> getAll(@PathVariable("id") int gID) {
        return Groups.getMembers(gID);
    }
}
