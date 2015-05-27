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
    private int distance = 0; // in km
    private int distanceToRun = 0; // in m
    private String[] ghosts;
    private long[] times;
    private long startTime;
    private GhostrunnerLocation lastLocation;

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
        distance = intent.getIntExtra("distance", distance);
        ghosts = intent.getStringArrayExtra("ghosts");
        times = intent.getLongArrayExtra("times");
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
        //TODO resolve error when location not activated}
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

    public void execRun() {
        startTime = System.nanoTime();
        distanceToRun = distance * 1000;
        initGPX();
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                GhostrunnerLocation gLocation = createGhostrunnerLocation(location);
                logGPX(gLocation);
                int stepDistance = calcDistance(lastLocation, gLocation);
                double distanceModifier = calcDistanceModifierFromSlope(lastLocation, gLocation);
                distanceToRun = (int) (distanceToRun / stepDistance * distanceModifier);
                lastLocation = gLocation;
                runListener.updateRun();
            }
        });
    }

    private int calcDistance(GhostrunnerLocation oldLoc, GhostrunnerLocation newLoc) {
        //TODO implement dummy
        return 0;
    }

    private double calcDistanceModifierFromSlope(GhostrunnerLocation oldLoc, GhostrunnerLocation newLoc) {
        //TODO implement dummy
        return 0;
    }

    // creates GhostrunnerLocation object including elevation and time from java location object
    private GhostrunnerLocation createGhostrunnerLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        long time = location.getTime();
        return new GhostrunnerLocation(lat, lon, time, elevationService.getElevation(lat, lon));
    }

    private void initGPX() {
        //TODO implement dummy
    }

    private void logGPX(GhostrunnerLocation gLocation) {
        //TODO implement dummy
    }

}
