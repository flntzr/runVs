package com.springapp.hibernate;

import javax.persistence.*;

/**
 * Created by franschl on 31.01.15.
 */
@Entity
@Table(name = "group_run", schema = "", catalog = "Ghostrunner")
public class GroupRunEntity {
    private int groupRunID;
    private RunsEntity runsByRunId;
    private GroupsEntity groupsByGroupId;

    @Id
    @Column(name = "id")
    public int getGroupRunID() {
        return groupRunID;
    }

    public void setGroupRunID(int id) {
        this.groupRunID = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupRunEntity that = (GroupRunEntity) o;

        if (groupRunID != that.groupRunID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return groupRunID;
    }

    @ManyToOne
    @JoinColumn(name = "run_id", referencedColumnName = "id", nullable = false)
    public RunsEntity getRunsByRunId() {
        return runsByRunId;
    }

    public void setRunsByRunId(RunsEntity runsByRunId) {
        this.runsByRunId = runsByRunId;
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
