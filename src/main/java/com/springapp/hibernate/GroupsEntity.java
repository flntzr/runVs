package com.springapp.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "groups", schema = "", catalog = "Ghostrunner")
public class GroupsEntity {
    private int groupID;
    private String name;
    private int distance;
    private int refWeekday;
    private Timestamp groupTimestamp;
    private Timestamp runTimestamp;
    private Timestamp userTimestamp;
    @JsonIgnore
    private Set<ExtInvitationsEntity> extInvitations = new HashSet<ExtInvitationsEntity>();
    @JsonIgnore
    private Set<RunsEntity> runs = new HashSet<RunsEntity>();
    @JsonIgnore
    private Set<IntInvitationsEntity> intInvitations = new HashSet<IntInvitationsEntity>();
    @JsonIgnore
    private Set<UsersEntity> users = new HashSet<UsersEntity>();

    @Id
    @Column(name = "id")
    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int id) {
        this.groupID = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "distance")
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Basic
    @Column(name = "ref_weekday")
    public int getRefWeekday() {
        return refWeekday;
    }

    public void setRefWeekday(int refWeekday) {
        this.refWeekday = refWeekday;
    }

    @Basic
    @Column(name = "group_timestamp")
    public Timestamp getGroupTimestamp() {
        return groupTimestamp;
    }

    public void setGroupTimestamp(Timestamp groupTimestamp) {
        this.groupTimestamp = groupTimestamp;
    }

    @Basic
    @Column(name = "run_timestamp")
    public Timestamp getRunTimestamp() {
        return runTimestamp;
    }

    public void setRunTimestamp(Timestamp runTimestamp) {
        this.runTimestamp = runTimestamp;
    }

    @Basic
    @Column(name = "user_timestamp")
    public Timestamp getUserTimestamp() {
        return userTimestamp;
    }

    public void setUserTimestamp(Timestamp userTimestamp) {
        this.userTimestamp = userTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupsEntity that = (GroupsEntity) o;

        if (distance != that.distance) return false;
        if (groupID != that.groupID) return false;
        if (refWeekday != that.refWeekday) return false;
        if (groupTimestamp != null ? !groupTimestamp.equals(that.groupTimestamp) : that.groupTimestamp != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (runTimestamp != null ? !runTimestamp.equals(that.runTimestamp) : that.runTimestamp != null) return false;
        if (userTimestamp != null ? !userTimestamp.equals(that.userTimestamp) : that.userTimestamp != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + distance;
        result = 31 * result + refWeekday;
        result = 31 * result + (groupTimestamp != null ? groupTimestamp.hashCode() : 0);
        result = 31 * result + (runTimestamp != null ? runTimestamp.hashCode() : 0);
        result = 31 * result + (userTimestamp != null ? userTimestamp.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "groupsByGroupId")
    public Set<ExtInvitationsEntity> getExtInvitations() {
        return extInvitations;
    }

    public void setExtInvitations(Set<ExtInvitationsEntity> extInvitationsesById) {
        this.extInvitations = extInvitationsesById;
    }

    //many to many done in XML
    public Set<RunsEntity> getRuns() {
        return this.runs;
    }

    @OneToMany(mappedBy = "groupsByGroupId")
    public Set<IntInvitationsEntity> getIntInvitations() {
        return intInvitations;
    }

    public void setIntInvitations(Set<IntInvitationsEntity> intInvitationsesById) {
        this.intInvitations = intInvitationsesById;
    }

    // Config in XML (ManyToMany)
    public Set<UsersEntity> getUsers() {
        return this.users;
    }

    public void setUsers(Set<UsersEntity> users) {
        this.users = users;
    }

    public void addUser(UsersEntity user) {
        this.users.add(user);
    }
}
