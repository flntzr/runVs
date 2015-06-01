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
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.UploadRun;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;
import winzer.gh0strunner.services.RunListener;
import winzer.gh0strunner.services.RunService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class RunActivity extends Activity implements ExecRunFragment.ExecRunListener, InitRunFragment.InitRunListener {

    private ArrayList<Integer> groups;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_run);
        Intent intent = getIntent();
        groups = intent.getIntegerArrayListExtra("groups");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.add(R.id.container, new InitRunFragment()).commit();
        Intent runIntent = new Intent(this, RunService.class);
        runIntent.putExtra("distance", intent.getIntExtra("distance", 0));
        runIntent.putExtra("ghosts", intent.getStringArrayExtra("ghosts"));
        runIntent.putExtra("ghostDurations", intent.getLongArrayExtra("ghostDurations"));
        bindService(runIntent, runConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runBound) {
            unbindService(runConnection);
        }
        runBound = false;
    }

    RunService runService;
    boolean runBound = false;
    private ServiceConnection runConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iService) {
            runBound = true;
            RunService.RunBinder binder = (RunService.RunBinder) iService;
            runService = binder.getService();
            binder.setListener(new RunListener() {

                @Override
                public void startRun() {
                    InitRunFragment initRunFragment = (InitRunFragment) getFragmentManager().findFragmentById(R.id.container);
                    initRunFragment.startRun();
                }

                @Override
                public void updateRun(int distance, double distancePassed, double actualDistance, double avDistanceModifier, double advancement, long duration, String[] ghosts, double[] ghostDistances, double[] ghostAdvancements, int position) {
                    ExecRunFragment execRunFragment = (ExecRunFragment) getFragmentManager().findFragmentById(R.id.container);
                    execRunFragment.updateUI(distance, distancePassed, avDistanceModifier, advancement, duration, ghosts, ghostDistances, ghostAdvancements, position);
                }

                @Override
                public void finishRun(int distance, double actualDistance, long duration, String[] ghosts, long[] ghostDurations, int position) {
                    if (runBound) {
                        unbindService(runConnection);
                    }
                    runBound = false;
                    String url = "/user/" + getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1) + "/run";
                    UploadRun run = new UploadRun();
                    run.setDistance(distance);
                    run.setActualDistance(actualDistance);
                    run.setDuration(duration);
                    run.setGroupIDs(groups);
                    Gson gson = new Gson();
                    StringEntity entity = null;
                    try {
                        String json = gson.toJson(run);
                        System.out.println(json);
                        entity = new StringEntity(gson.toJson(run));
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    RestClient.post(url, entity, new RetryAsyncHttpResponseHandler(url, entity, context, RetryAsyncHttpResponseHandler.POST_REQUEST) {
                        @Override
                        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println("uploaded run");
                        }

                        @Override
                        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            String body = new String(responseBody);
                            System.out.println("failed run upload: " + body);
                        }
                    });
                    FragmentTransaction tx = getFragmentManager().beginTransaction();
                    tx.replace(R.id.container, FinishRunFragment.newInstance(actualDistance, duration, ghosts, ghostDurations, position)).commit();
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
        runService.execRun();
    }

    @Override
    public void abortRun() {
        if (runBound) {
            runService.endRun();
            unbindService(runConnection);
        }
        runBound = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void cancelRun() {
        if (runBound) {
            runService.endRun();
            unbindService(runConnection);
        }
        runBound = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
