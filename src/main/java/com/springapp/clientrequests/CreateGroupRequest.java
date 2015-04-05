package com.springapp.clientrequests;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @NotNull
    @NotBlank
    String name;
    @Min(0)
    @Max(1)
    Integer admin;
    @Pattern(regexp = "[2|5|8|10]")
    String distance;
    ArrayList<Integer> members = new ArrayList<>();

    public Integer getDistance() {
        return Integer.parseInt(distance);
    }

    public void setDistance(Integer distance) {
        this.distance = distance.toString();
    }

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
