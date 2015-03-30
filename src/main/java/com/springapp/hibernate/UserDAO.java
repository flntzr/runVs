package com.springapp.hibernate;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "users", schema = "", catalog = "Ghostrunner")
public class UserDAO {
    private int userID;
    private String nick;
    private String password;
    @JsonIgnore
    private String salt;
    private String email;
    @JsonIgnore
    private String authToken;
    @JsonIgnore
    private Timestamp lastLogin;
    @JsonIgnore
    private Timestamp tokenExpiry;
    @JsonIgnore
    private Timestamp runTimestamp;
    @JsonIgnore
    private Collection<GroupDAO> groups = new ArrayList<>();
    @JsonIgnore
    private Collection<ExtInvDAO> extInvitations = new ArrayList<>();
    @JsonIgnore
    private Collection<IntInvDAO> host = new ArrayList<>();
    @JsonIgnore
    private Collection<IntInvDAO> invitees = new ArrayList<>();
    @JsonIgnore
    private Collection<RunDAO> runs = new ArrayList<>();
    //@JsonIgnore
    //private Collection<UserGroupEntity> userGroups;

    @Id
    @Column(name = "id")
    public int getUserID() {
        return userID;
    }

    public void setUserID(int id) {
        this.userID = id;
    }

    @Basic
    @Column(name = "nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    /* Weiß nicht warum JsonProperty hier so funktioniert. Eigentlich sollte das so gehen http://stackoverflow.com/questions/12505141/only-using-jsonignore-during-serialization-but-not-deserialization */
    @Basic
    @Column(name = "password")
    @JsonProperty String getPassword() {
        return password;
    }

    @JsonProperty void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "salt")
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "auth_token")
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Basic
    @Column(name = "token_expiry")
    public Timestamp getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Timestamp tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    @Basic
    @Column(name = "last_login")
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Basic
    @Column(name = "run_timestamp")
    public Timestamp getRunTimestamp() {
        return runTimestamp;
    }

    public void setRunTimestamp(Timestamp runTimestamp) {
        this.runTimestamp = runTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDAO that = (UserDAO) o;

        if (userID != that.userID) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (lastLogin != null ? !lastLogin.equals(that.lastLogin) : that.lastLogin != null) return false;
        if (nick != null ? !nick.equals(that.nick) : that.nick != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (runTimestamp != null ? !runTimestamp.equals(that.runTimestamp) : that.runTimestamp != null) return false;
        if (salt != null ? !salt.equals(that.salt) : that.salt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userID;
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (lastLogin != null ? lastLogin.hashCode() : 0);
        result = 31 * result + (runTimestamp != null ? runTimestamp.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "host")
    public Collection<ExtInvDAO> getExtInvitations() {
        return extInvitations;
    }

    public void setExtInvitations(Collection<ExtInvDAO> extInvitations) {
        this.extInvitations = extInvitations;
    }

    @OneToMany(mappedBy = "host")
    public Collection<IntInvDAO> getHost() {
        return host;
    }

    public void setHost(Collection<IntInvDAO> host) {
        this.host = host;
    }

    @OneToMany(mappedBy = "invitee")
    public Collection<IntInvDAO> getInvitees() {
        return invitees;
    }

    public void setInvitees(Collection<IntInvDAO> invitees) {
        this.invitees = invitees;
    }

    @OneToMany(mappedBy = "user")
    public Collection<RunDAO> getRuns() {
        return runs;
    }

    public void setRuns(Collection<RunDAO> runs) {
        this.runs = runs;
    }

    // Config in XML (ManyToMany)
    public Collection<GroupDAO> getGroups() {
        return this.groups;
    }

    public void setGroups(Collection<GroupDAO> groups) {
        this.groups = groups;
    }

    public void addGroup(GroupDAO group) {
        this.groups.add(group);
    }
}
