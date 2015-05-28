package winzer.gh0strunner.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 5/28/15.
 */
public class Group {
    private int groupID;
    private String name;
    private int distance;
    private int refWeekday;
    private Set<User> users = new HashSet<User>();


    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public int getGroupID() {
        return groupID;

    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getRefWeekday() {
        return refWeekday;
    }

    public void setRefWeekday(int refWeekday) {
        this.refWeekday = refWeekday;
    }
}
