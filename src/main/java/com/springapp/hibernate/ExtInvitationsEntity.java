package com.springapp.hibernate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "ext_invitations", schema = "", catalog = "Ghostrunner")
public class ExtInvitationsEntity {
    private int extInvID;
    private int pin;
    private Timestamp timestamp;
    private GroupsEntity groupsByGroupId;
    private UsersEntity usersByHostId;

    @Id
    @Column(name = "id")
    public int getExtInvID() {
        return extInvID;
    }

    public void setExtInvID(int id) {
        this.extInvID = id;
    }

    @Basic
    @Column(name = "pin")
    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
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

        ExtInvitationsEntity that = (ExtInvitationsEntity) o;

        if (extInvID != that.extInvID) return false;
        if (pin != that.pin) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = extInvID;
        result = 31 * result + pin;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    public GroupsEntity getGroupsByGroupId() {
        return groupsByGroupId;
    }

    public void setGroupsByGroupId(GroupsEntity groupsByGroupId) {
        this.groupsByGroupId = groupsByGroupId;
    }

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    public UsersEntity getUsersByHostId() {
        return usersByHostId;
    }

    public void setUsersByHostId(UsersEntity usersByHostId) {
        this.usersByHostId = usersByHostId;
    }
}