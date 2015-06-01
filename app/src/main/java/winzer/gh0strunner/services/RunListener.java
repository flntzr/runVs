package winzer.gh0strunner.services;

public interface RunListener {

    public void startRun();

    public void updateRun(int distance, double distancePassed, double actualDistance, double avDistanceModifier, double advancement, long duration, String[] ghosts, double[] ghostDistances, double[] ghostAdvancements, int position);

    public void finishRun(int distance, double actualDistance, long duration, String[] ghosts, long[] ghostDurations, int position);

}
