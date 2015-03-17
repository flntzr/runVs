package com.springapp.clientrequests;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

/**
 * Created by franschl on 16.03.15.
 */

/* The client wants to create a run.
The information he gives:
    - gpx-file
    - distance
    - own identity
    - duration
    - score
    - timestamp
 */

public class CreateRunRequest {
    MultipartFile gpxFile;
    int distance;
    int userID;
    double duration;
    double score;
    Timestamp timestamp;

    public MultipartFile getGpxFile() {
        return gpxFile;
    }

    public void setGpxFile(MultipartFile gpxFile) {
        this.gpxFile = gpxFile;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
