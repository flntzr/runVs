package winzer.gh0strunner.services;

/**
 * Created by droland on 5/26/15.
 */
public class GhostrunnerLocation {

    private double lat;
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public short getElevation() {
        return elevation;
    }

    public void setElevation(short elevation) {
        this.elevation = elevation;
    }

    private long time;
    private short elevation;

    public GhostrunnerLocation(double lat, double lon, long time) {
        setLat(lat);
        setLon(lon);
        setTime(time);
    }

    public GhostrunnerLocation(double lat, double lon, long time, short elevation) {
        this(lat, lon, time);
        setElevation(elevation);
    }

}
