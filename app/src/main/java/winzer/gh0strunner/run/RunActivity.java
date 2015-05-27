package winzer.gh0strunner.run;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import winzer.gh0strunner.R;
import winzer.gh0strunner.services.RunListener;
import winzer.gh0strunner.services.RunService;

public class RunActivity extends Activity implements ExecRunFragment.ExecRunListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Intent intent = getIntent();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.add(R.id.container, InitRunFragment.newInstance("", "")).commit();//TODO pass arguments from intent
        Intent runIntent = new Intent(this, RunService.class);
        runIntent.putExtra("distance", intent.getIntExtra("distance", 0));
        runIntent.putExtra("ghosts", intent.getStringArrayExtra("ghosts"));
        runIntent.putExtra("times", intent.getLongArrayExtra("times"));
        bindService(runIntent, runConnection, Context.BIND_AUTO_CREATE);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    InitRunFragment initRunFragment = (InitRunFragment) getFragmentManager().findFragmentById(R.id.container);
                    initRunFragment.startRun();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void execRun() {
        System.out.println("executing run");
        runService.execRun();
    }
}
