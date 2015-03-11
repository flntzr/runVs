package com.springapp.hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "runs", schema = "", catalog = "Ghostrunner")
public class RunsEntity {
    private int runID;
    private int distance;
    private String path;
    private double duration;
    private int score;
    private Timestamp timestamp;
    private Collection<GroupRunEntity> groupRunsById;
    private UsersEntity usersByUserId;

    @Id
    @Column(name = "id")
    public int getRunID() {
        return runID;
    }

    public void setRunID(int id) {
        this.runID = id;
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
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic
    @Column(name = "duration")
    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Basic
    @Column(name = "score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

        RunsEntity that = (RunsEntity) o;

        if (distance != that.distance) return false;
        if (Double.compare(that.duration, duration) != 0) return false;
        if (runID != that.runID) return false;
        if (score != that.score) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = runID;
        result = 31 * result + distance;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        temp = Double.doubleToLongBits(duration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + score;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "runsByRunId")
    public Collection<GroupRunEntity> getGroupRunsById() {
        return groupRunsById;
    }

    public void setGroupRunsById(Collection<GroupRunEntity> groupRunsById) {
        this.groupRunsById = groupRunsById;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UsersEntity getUsersByUserId() {
        return usersByUserId;
    }

    public void setUsersByUserId(UsersEntity usersByUserId) {
        this.usersByUserId = usersByUserId;
    }
}
