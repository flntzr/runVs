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
        //TODO getElevation and calculations
        runListener.updateRun();
    }

}
