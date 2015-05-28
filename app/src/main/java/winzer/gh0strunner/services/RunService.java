package winzer.gh0strunner.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class RunService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private final IBinder binder = new RunBinder();
    private RunListener runListener;

    public class RunBinder extends Binder {

        public RunService getService() {
            return RunService.this;
        }

        public void setListener(RunListener rListener) {
            runListener = rListener;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        context = this;
        distance = intent.getDoubleExtra("distance", distance);
        ghosts = intent.getStringArrayExtra("ghosts");
        ghostDurations = intent.getLongArrayExtra("times");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!resolvingLocationError) {
            googleApiClient.connect();
        }
        return binder;
    }

    //GoogleAPI

    GoogleApiClient googleApiClient;
    boolean resolvingLocationError = false;

    @Override
    public void onConnected(Bundle connectionHint) {
        new Thread(new Runnable() {
            public void run() {
                initiate();
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //TODO resolve error when location not activated
    }

    //ElevationService

    private void initiate() {
        while (!LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient).isLocationAvailable()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        Intent elevationIntent = new Intent(context, SRTMElevationService.class);
        elevationIntent.putExtra("lat", location.getLatitude());
        elevationIntent.putExtra("lon", location.getLongitude());
        elevationIntent.putExtra("distance", distance);
        bindService(elevationIntent, elevationConnection, Context.BIND_AUTO_CREATE);
    }

    SRTMElevationService elevationService;
    private ServiceConnection elevationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iService) {
            SRTMElevationService.ElevationBinder binder = (SRTMElevationService.ElevationBinder) iService;
            elevationService = binder.getService();
            binder.setListener(new ElevationListener() {
                @Override
                public void initiated() {
                    runListener.startRun();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private double distance = 0.0; // in m
    private double distancePassed = 0.0; // in m
    private double advancement = 0.0;
    private long startTime = 0;
    private long duration = 0;
    private String[] ghosts;
    private long[] ghostDurations;
    private final static int MAX_INDEX_LAST_LOCATIONS = 4;
    private Location[] locations = new Location[MAX_INDEX_LAST_LOCATIONS + 1];
    private int locationIndex = MAX_INDEX_LAST_LOCATIONS;


    public void execRun() {
        initGPX();
        Location firstLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startTime = firstLocation.getElapsedRealtimeNanos();
        pushOnLocationsStack(firstLocation);
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                location.setAltitude(elevationService.getElevation(location.getLatitude(), location.getLongitude()));
                duration = location.getElapsedRealtimeNanos() - startTime;
                pushOnLocationsStack(location);

                logGPX(locations[locationIndex]);

                double stepDistance = location.distanceTo(locations[getDecLocIndex(locationIndex)]);
                double distanceModifier = calcDistanceModifierFromSlope(locations, locationIndex);
                distancePassed += stepDistance * distanceModifier;
                advancement = distancePassed / distance;

                double[] ghostDistances = calcGhostDistances(duration);
                double[] ghostAdvancements = calcGhostAdvancements(duration);

                if (distancePassed >= distance) {
                    runListener.finishRun(duration);
                    //TODO stop service
                } else {
                    runListener.updateRun(distance, distancePassed, advancement, duration, ghosts, ghostDistances, ghostAdvancements);
                }
            }
        });
    }

    private double[] calcGhostDistances(double duration) {
        double[] ghostDistances = new double[ghostDurations.length];
        for (int i = 0; i < ghostDurations.length; i++) {
            if (ghostDurations[i] == 0) {
                ghostDistances[i] = 0;
            } else {
                ghostDistances[i] = duration / ghostDurations[i] * distance;
            }
        }
        return ghostDistances;
    }

    private double[] calcGhostAdvancements(double duration) {
        double[] ghostAdvancements = new double[ghostDurations.length];
        for (int i = 0; i < ghostDurations.length; i++) {
            if (ghostDurations[i] == 0) {
                ghostAdvancements[i] = 0;
            } else {
                ghostAdvancements[i] = duration / ghostDurations[i];
            }
        }
        return ghostAdvancements;
    }

    private void pushOnLocationsStack(Location location) {
        locationIndex = getIncLocIndex(locationIndex);
        locations[locationIndex] = location;
    }


    private int getDecLocIndex(int index) {
        if (index <= 0) {
            index = MAX_INDEX_LAST_LOCATIONS;
        } else {
            index--;
        }
        return index;
    }

    private int getIncLocIndex(int index) {
        if (index >= MAX_INDEX_LAST_LOCATIONS) {
            index = 0;
        } else {
            index++;
        }
        return index;
    }

    private double calcDistanceModifierFromSlope(Location[] locations, int locationIndex) {
        //TODO implement dummy
        return 1;
    }

    private void initGPX() {
        //TODO implement dummy
    }

    private void logGPX(Location gLocation) {
        //TODO implement dummy
    }

}
