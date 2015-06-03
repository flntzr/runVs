package winzer.gh0strunner.dto;

/**
 * Created by franschl on 6/3/15.
 */
public class Ghost {
    String name;
    long duration;

    public Ghost(String name, long duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }
}
