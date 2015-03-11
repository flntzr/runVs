package com.springapp.hibernate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "int_invitations", schema = "", catalog = "Ghostrunner")
public class IntInvitationsEntity {
    private int intInvID;
    private Timestamp timestamp;
    private UsersEntity usersByHostId;
    private UsersEntity usersByInviteeId;
    private GroupsEntity groupsByGroupId;

    @Id
    @Column(name = "id")
    public int getIntInvID() {
        return intInvID;
    }

    public void setIntInvID(int id) {
        this.intInvID = id;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntInvitationsEntity that = (IntInvitationsEntity) o;

        if (intInvID != that.intInvID) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = intInvID;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    public UsersEntity getUsersByHostId() {
        return usersByHostId;
    }

    public void setUsersByHostId(UsersEntity usersByHostId) {
        this.usersByHostId = usersByHostId;
    }

    @ManyToOne
    @JoinColumn(name = "invitee_id", referencedColumnName = "id", nullable = false)
    public UsersEntity getUsersByInviteeId() {
        return usersByInviteeId;
    }

    public void setUsersByInviteeId(UsersEntity usersByInviteeId) {
        this.usersByInviteeId = usersByInviteeId;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    public GroupsEntity getGroupsByGroupId() {
        return groupsByGroupId;
    }

    public void setGroupsByGroupId(GroupsEntity groupsByGroupId) {
        this.groupsByGroupId = groupsByGroupId;
    }
}
