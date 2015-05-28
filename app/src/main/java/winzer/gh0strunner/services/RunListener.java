package winzer.gh0strunner.services;

public interface RunListener {

    public void startRun();

    public void updateRun(double distance, double distancePassed, double advancement, long duration, String[] ghosts, double[] ghostDistances, double[] ghostAdvancements);

    public void finishRun(long duration);

}
