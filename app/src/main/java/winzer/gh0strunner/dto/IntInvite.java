package winzer.gh0strunner.dto;

import java.sql.Timestamp;

/**
 * Created by franschl on 29.03.15.
 */
/*
* Client wants to create an invite. The following info are needed:
* - identity
* - invitee (already in path so not required again)
* - group
* */
public class IntInvite {
    private int invitationID;
    private long timestamp;
    private User host;
    private User invitee;
    private Group group;

    public int getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(int invitationID) {
        this.invitationID = invitationID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public User getInvitee() {
        return invitee;
    }

    public void setInvitee(User invitee) {
        this.invitee = invitee;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
