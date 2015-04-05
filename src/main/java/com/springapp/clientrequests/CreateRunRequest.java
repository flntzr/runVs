package com.springapp.clientrequests;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.sun.istack.internal.NotNull;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by franschl on 16.03.15.
 */

/* The client wants to create a run.
The information he gives:
    - gpx-file
    - distance
    - groups the run will be submitted to
    - duration
    - score
    - timestamp
 */

public class CreateRunRequest {
    @NotNull
    // TODO pattern to make sure file is gpx
    File gpxFile;
    @Pattern(regexp = "[2|5|8|10]")
    String distance;
    @NotNull
    @DecimalMin("0.0")
    double duration;
    @NotNull
    @DecimalMin("0.0")
    double score;
    @Past
    Timestamp timestamp;
    ArrayList<Integer> groups = new ArrayList<>();

    public ArrayList<Integer> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Integer> groups) {
        this.groups = groups;
    }

    public File getGpxFile() {
        return gpxFile;
    }

    public void setGpxFile(File gpxFile) {
        this.gpxFile = gpxFile;
    }

    public String getBase64GpxFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(gpxFile);
        return new String(Base64.encodeBase64(baos.toByteArray()));
    }

    /* Help from http://www.java2s.com/Code/Java/File-Input-Output/Convertobjecttobytearrayandconvertbytearraytoobject.htm */
    public void setBase64GpxFile(String base64Str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(base64Str));
        ObjectInputStream ois = new ObjectInputStream(bais);
        gpxFile = (File) ois.readObject();
    }

    public Integer getDistance() {
        return Integer.parseInt(distance);
    }

    public void setDistance(Integer distance) {
        this.distance = distance.toString();
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
