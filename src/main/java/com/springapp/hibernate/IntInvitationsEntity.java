package com.springapp.hibernate;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "int_invitations", schema = "", catalog = "Ghostrunner")
public class IntInvitationsEntity {
    private int invitationID;
    private Timestamp timestamp;
    private UsersEntity host;
    private UsersEntity invitee;
    private GroupsEntity group;

    @Id
    @Column(name = "int_inv_id")
    public int getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(int id) {
        this.invitationID = id;
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

        if (invitationID != that.invitationID) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = invitationID;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "user_id", nullable = false)
    public UsersEntity getHost() {
        return host;
    }

    public void setHost(UsersEntity usersByHostId) {
        this.host = usersByHostId;
    }

    @ManyToOne
    @JoinColumn(name = "invitee_id", referencedColumnName = "user_id", nullable = false)
    public UsersEntity getInvitee() {
        return invitee;
    }

    public void setInvitee(UsersEntity usersByInviteeId) {
        this.invitee = usersByInviteeId;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    public GroupsEntity getGroup() {
        return group;
    }

    public void setGroup(GroupsEntity groupsByGroupId) {
        this.group = groupsByGroupId;
    }
}
