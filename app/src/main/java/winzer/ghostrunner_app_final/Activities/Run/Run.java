package winzer.ghostrunner_app_final.Activities.Run;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import winzer.ghostrunner_app_final.R;
import winzer.ghostrunner_app_final.Services.RunListener;
import winzer.ghostrunner_app_final.Services.RunService;

public class Run extends ActionBarActivity {

    private View startRunButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        startRunButton = findViewById(R.id.startRunButton);
        startRunButton.setClickable(false);
        startRunButton.setEnabled(false);
        Intent runIntent = new Intent(this, RunService.class);
        bindService(runIntent, runConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (runBound) {
            unbindService(runConnection); //TODO correct?
        }
        runBound = false;
    }

    RunService runService;
    boolean runBound = false;
    private ServiceConnection runConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iService) {
            RunService.RunBinder binder = (RunService.RunBinder) iService;
            runService = binder.getService();

            binder.setListener(new RunListener() {

                @Override
                public void startRun() {
                    startRunButton.post(new Runnable() {
                        public void run() {
                            startRunButton.setClickable(true);
                            startRunButton.setEnabled(true);
                        }
                    });
                }

                @Override
                public void updateRun() {
                    System.out.println("test update run successful");
                }

                @Override
                public void finishRun() {
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            runBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startRun(View view) {
        runService.execRun();
    }

    public void goToAbortRun(View view) {
        Intent intent = new Intent(this, AbortRun.class);
        startActivity(intent);
    }

    public void goToRunStatistics(View view) {
        Intent intent = new Intent(this, RunStatistics.class);
    }
}
