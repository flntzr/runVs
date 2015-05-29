package winzer.gh0strunner.services;

import java.util.ArrayList;

/**
 * Created by droland on 5/29/15.
 */
public interface LoadGhostsCallback {
    void ghostsLoaded(ArrayList<String> newGhosts, ArrayList<Long> newGhostDurations);
}
