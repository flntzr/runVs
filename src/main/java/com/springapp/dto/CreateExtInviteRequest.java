package com.springapp.dto;

/**
 * Created by franschl on 30.03.15.
 */
/*
* What the server needs to create an extInvite:
*   - host identity
*   - group
* */
public class CreateExtInviteRequest {
    private int hostID;
    private int groupID;

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
