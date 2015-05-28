package com.springapp.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "groups", schema = "", catalog = "Ghostrunner")
public class GroupDAO {
    private int groupID;
    private String name;
    private int distance;
    private int refWeekday;
    //private Timestamp groupTimestamp;
    //private Timestamp runTimestamp;
    //private Timestamp userTimestamp;
    @JsonIgnore
    private Set<ExtInvDAO> extInvitations = new HashSet<ExtInvDAO>();
    @JsonIgnore
    private Set<RunDAO> runs = new HashSet<RunDAO>();
    @JsonIgnore
    private Set<IntInvDAO> intInvitations = new HashSet<IntInvDAO>();
    private Set<UserDAO> users = new HashSet<UserDAO>();

    @Id
    @Column(name = "group_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupDAO that = (GroupDAO) o;

        if (distance != that.distance) return false;
        if (groupID != that.groupID) return false;
        if (refWeekday != that.refWeekday) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = groupID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + distance;
        result = 31 * result + refWeekday;
        return result;
    }

    @OneToMany(mappedBy = "group")
    public Set<ExtInvDAO> getExtInvitations() {
        return extInvitations;
    }

    public void setExtInvitations(Set<ExtInvDAO> extInvitationsesById) {
        this.extInvitations = extInvitationsesById;
    }

    //many to many done in XML
    public Set<RunDAO> getRuns() {
        return this.runs;
    }

    public void setRuns(Set<RunDAO> runs) {
        this.runs = runs;
    }

    public void addRun(RunDAO run) {
        runs.add(run);
    }

    @OneToMany(mappedBy = "group")
    public Set<IntInvDAO> getIntInvitations() {
        return intInvitations;
    }

    public void setIntInvitations(Set<IntInvDAO> intInvitationsesById) {
        this.intInvitations = intInvitationsesById;
    }

    // Config in XML (ManyToMany)
    public Set<UserDAO> getUsers() {
        return this.users;
    }

    public void setUsers(Set<UserDAO> users) {
        this.users = users;
    }

    public void addUser(UserDAO user) {
        this.users.add(user);
    }

    public void removeUser(UserDAO user) {
        this.users.remove(user);
    }
}
