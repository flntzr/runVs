package winzer.gh0strunner.dto;

/**
 * Created by franschl on 5/28/15.
 */
public class Group {
    private int groupID;
    private String name;
    private int distance;
    private int refWeekday;

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getRefWeekday() {
        return refWeekday;
    }

    public void setRefWeekday(int refWeekday) {
        this.refWeekday = refWeekday;
    }
}
