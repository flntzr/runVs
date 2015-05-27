package winzer.gh0strunner.dto;

import android.view.View;

/**
 * Created by franschl on 5/26/15.
 */
public class UserInGroupView {
    private int userID;
    private String name;
    private double runTime = -1;
    // view IDs so they can be accessed later
    private int rowID;
    private int nameViewID;
    private int durationViewID;
    private int rankViewID;

    public int getRowID() {
        return rowID;
    }

    public int getDurationViewID() {
        return durationViewID;
    }

    public int getRankViewID() {
        return rankViewID;
    }

    public int getNameViewID() {
        return nameViewID;
    }

    public UserInGroupView(int userID) {
        this.userID = userID;
        rowID = View.generateViewId();
        durationViewID = View.generateViewId();
        rankViewID = View.generateViewId();
        nameViewID = View.generateViewId();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

}
