package winzer.ghostrunner_app_final.Services;

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
        if (!resolvingGoogleError) {
            googleApiClient.connect();
        }
        return binder;
    }

    //GoogleAPI

    GoogleApiClient googleApiClient;
    boolean resolvingGoogleError = false;
    private static final String DIALOG_ERROR = "Could not connect to Google Play Services";
    private static final int REQUEST_RESOLVE_ERROR = 1001;

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
//        if (resolvingGoogleError) {
//            return;
//        } else if (result.hasResolution()) {
//            try {
//                resolvingGoogleError = true;
//                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                googleApiClient.connect();
//            }
//        } else {
//            showErrorDialog(result.getErrorCode());
//            resolvingGoogleError = true;
//        }
    }

//    private void showErrorDialog(int errorCode) {
//        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
//        Bundle args = new Bundle();
//        args.putInt("Could not connect to Google Play Services", errorCode);
//        dialogFragment.setArguments(args);
//        dialogFragment.show(getSupportFragmentManager(), "errordialog");
//    }
//
//    public void onDialogDismissed() {
//        resolvingLocationError = false;
//    }
//
//    public static class ErrorDialogFragment extends DialogFragment {
//        public ErrorDialogFragment() {
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
//            return GooglePlayServicesUtil.getErrorDialog(errorCode,
//                    this.getActivity(), REQUEST_RESOLVE_ERROR);
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            ((StartRun) getActivity()).onDialogDismissed();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_RESOLVE_ERROR) {
//            resolvingLocationError = false;
//            if (resultCode == RESULT_OK) {
//                if (!googleApiClient.isConnecting() &&
//                        !googleApiClient.isConnected()) {
//                    googleApiClient.connect();
//                }
//            }
//        }
//    }

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

    ElevationService elevationService;
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
