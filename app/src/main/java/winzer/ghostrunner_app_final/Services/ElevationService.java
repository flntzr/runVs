package winzer.ghostrunner_app_final.Services;

import winzer.ghostrunner_app_final.Exceptions.ElevationNotFoundException;

public interface ElevationService {

    void initElevationService(double lat, double lon);

    short getElevation(double lat, double lon) throws ElevationNotFoundException;

}
