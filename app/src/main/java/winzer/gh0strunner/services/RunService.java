package winzer.gh0strunner.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
        distance = intent.getIntExtra("distance", distance);
        ghosts = intent.getStringArrayExtra("ghosts");
        ghostDurations = intent.getLongArrayExtra("ghostDurations");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        return binder;
    }

    //GoogleAPI

    GoogleApiClient googleApiClient;
    boolean googleBound = false;

    @Override
    public void onConnected(Bundle connectionHint) {
        googleBound = true;
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
    boolean elevationBound = false;
    private ServiceConnection elevationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iService) {
            elevationBound = true;
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
            elevationBound = false;
        }
    };

    private int distance = 0; // in m
    private double distancePassed = 0.0; // in m
    private double actualDistance = 0.0;
    private double advancement = 0.0;
    private double avDistanceModifier = 0.0;
    private long startTime = 0;
    private long duration = 0;
    private String[] ghosts = null;
    private long[] ghostDurations = null;
    private final static int MAX_INDEX_LAST_LOCATIONS = 4;
    private Location[] locations = new Location[MAX_INDEX_LAST_LOCATIONS + 1];
    private int locationIndex = MAX_INDEX_LAST_LOCATIONS;
    private boolean firstLocation = true;

    public void endRun() {
        if (gpxLog != null) {
            closeGPX();
        }
        if (elevationBound) {
            unbindService(elevationConnection);
            elevationBound = false;
        }
        if (googleBound) {
            googleApiClient.disconnect();
            googleBound = false;
        }
    }

    public void execRun() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (firstLocation) {
                    //if first location, set starttime and initiate gpx
                    startTime = location.getTime();
                    initGPX();
                    location.setAltitude(elevationService.getElevation(location.getLatitude(), location.getLongitude()));
                    pushOnLocationsStack(location);
                    firstLocation = false;
                } else {
                    location.setAltitude(elevationService.getElevation(location.getLatitude(), location.getLongitude()));
                    duration = location.getTime() - startTime;
                    pushOnLocationsStack(location);

                    logGPX(locations[locationIndex]);

                    double stepDistance = location.distanceTo(locations[getDecLocIndex(locationIndex)]);
                    double distanceModifier = calcDistanceModifierFromSlope(locations, locationIndex);
                    distancePassed += stepDistance * distanceModifier;
                    actualDistance += stepDistance;
                    avDistanceModifier = distancePassed / actualDistance;
                    advancement = distancePassed / distance;

                    double[] ghostDistances = calcGhostDistances(duration);
                    double[] ghostAdvancements = calcGhostAdvancements(duration);
                    int position = calcPosition(duration, ghostAdvancements);

                    if (distancePassed >= 2) { //TODO change back to distance!!!
                        endRun();
                        runListener.finishRun(distance, actualDistance, duration, ghosts, ghostDurations, position);
                    } else {
                        runListener.updateRun(distance, distancePassed, actualDistance, avDistanceModifier, advancement, duration, ghosts, ghostDistances, ghostAdvancements, position);
                    }
                }
            }
        });
    }

    private int calcPosition(double advancement, double[] ghostAdvancements) {
        if (ghostAdvancements != null) {
            Arrays.sort(ghostAdvancements);
            int pos = 0;
            while (pos < ghostAdvancements.length && advancement > ghostAdvancements[pos]) {
                pos++;
            }
            return ghostAdvancements.length + 1 - pos;
        }
        return 1;
    }

    private double[] calcGhostDistances(double duration) {
        if (ghostDurations != null) {
            double[] ghostDistances = new double[ghostDurations.length];
            for (int i = 0; i < ghostDurations.length; i++) {
                if (ghostDurations[i] == 0) {
                    ghostDistances[i] = 0;
                } else {
                    ghostDistances[i] = duration / ghostDurations[i] * distance;
                    if (ghostDistances[i] > distance) {
                        ghostDistances[i] = distance;
                    }
                }
            }
            return ghostDistances;
        }
        return null;
    }

    private double[] calcGhostAdvancements(double duration) {
        if (ghostDurations != null) {
            double[] ghostAdvancements = new double[ghostDurations.length];
            for (int i = 0; i < ghostDurations.length; i++) {
                if (ghostDurations[i] == 0) {
                    ghostAdvancements[i] = 0;
                } else {
                    ghostAdvancements[i] = duration / ghostDurations[i];
                    if (ghostAdvancements[i] > 1) {
                        ghostAdvancements[i] = 1;
                    }
                }
            }
            return ghostAdvancements;
        }

        return null;
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

    File gpxLog = null;

    private String toReadableTime(long time) {
        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        String readableDate = formatDate.format(date);
        String readableTime = formatTime.format(date);
        return readableDate + "T" + readableTime + "Z";
    }

    private void initGPX() {
        File dir = new File(Environment.getExternalStorageDirectory(), "gh0strunner");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "Run_from_" + toReadableTime(startTime) + ".gpx";
        fileName.replace(":", "-");
        gpxLog = new File(dir + "/" + fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(gpxLog, true);
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<gpx version=\"1.1\">\n" +
                    "\t<name>" + fileName + "</name>\n" +
                    "\t<trk><name>Run</name><number>1</number><trkseg>\n";
            byte[] bytes = header.getBytes("UTF-8");
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logGPX(Location location) {
        try {
            FileOutputStream fos = new FileOutputStream(gpxLog, true);
            String wpt = "\t\t<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><ele>" + (int) location.getAltitude() + "</ele><time>" + toReadableTime(location.getTime()) + "</time></trkpt>\n";
            byte[] bytes = wpt.getBytes("UTF-8");
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeGPX() {
        try {
            FileOutputStream fos = new FileOutputStream(gpxLog, true);
            String end = "\t</trkseg></trk>\n</gpx>";
            byte[] bytes = end.getBytes("UTF-8");
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
