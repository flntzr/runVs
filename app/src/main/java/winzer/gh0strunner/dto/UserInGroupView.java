package winzer.gh0strunner.dto;

/**
 * Created by franschl on 5/26/15.
 */
public class UserInGroupView {
    private int userID;
    private String name;
    private double runTime;

    public UserInGroupView(int userID, String name) {
        this.userID = userID;
        this.name = name;
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
