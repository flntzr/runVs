package com.springapp.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "ext_invitations", schema = "", catalog = "Ghostrunner")
public class ExtInvDAO {
    private int extInvID;
    @JsonIgnore
    private int pin;
    private Timestamp timestamp;
    private GroupDAO group;
    private UserDAO host;

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

        ExtInvDAO that = (ExtInvDAO) o;

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
    public GroupDAO getGroup() {
        return group;
    }

    public void setGroup(GroupDAO groupsByGroupId) {
        this.group = groupsByGroupId;
    }

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    public UserDAO getHost() {
        return host;
    }

    public void setHost(UserDAO usersByHostId) {
        this.host = usersByHostId;
    }
}
