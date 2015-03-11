package com.springapp.fromclient;

import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */

/* The client wants to create a group.
    The information he gives:
        - group name
        - own identity --> admin
        - other members (variable length list)
        - group distance
*/
public class CreateGroupRequest {
    String name;
    Integer admin;
    ArrayList<Integer> members = new ArrayList<>();

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    int distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer adminID) {
        this.admin = adminID;
    }

    public ArrayList<Integer> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Integer> members) {
        this.members = members;
    }
}
