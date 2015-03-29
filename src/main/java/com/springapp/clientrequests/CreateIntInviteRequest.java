package com.springapp.clientrequests;

/**
 * Created by franschl on 29.03.15.
 */
/*
* Client wants to create an invite. The following info are needed:
* - identity
* - invitee (already in path so not required again)
* - group
* */
public class CreateIntInviteRequest {
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
