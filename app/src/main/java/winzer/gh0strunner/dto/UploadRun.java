package winzer.gh0strunner.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by droland on 5/29/15.
 */
public class UploadRun {
    private int distance;
    private long duration;
    private double actualDistance;
    private ArrayList<Integer> groupIDs;

    public ArrayList<Integer> getGroupIDs() {
        return groupIDs;
    }

    public void setGroupIDs(ArrayList<Integer> groupIDs) {
        this.groupIDs = groupIDs;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(double actualDistance) {
        this.actualDistance = actualDistance;
    }

}
