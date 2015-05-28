package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Group;
import winzer.gh0strunner.dto.Run;
import winzer.gh0strunner.dto.UserInGroupView;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 5/24/15.
 */
public class GroupViewFragment extends Fragment {

    final List<UserInGroupView> members = new ArrayList<>();

    public static GroupViewFragment newInstance(int groupID) {
        GroupViewFragment groupViewFragment = new GroupViewFragment();
        Bundle args = new Bundle();
        args.putInt("groupID", groupID);
        groupViewFragment.setArguments(args);
        return groupViewFragment;
    }

    public class CustomGroupMembersRestHandler extends RetryAsyncHttpResponseHandler {

        public CustomGroupMembersRestHandler(String url, Context context, Fragment fragment, int requestType) {
            super(url, context, fragment, requestType);
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // get all group members as jsonArray
            final String responseString = new String(responseBody);
            try {
                JSONArray json = new JSONArray(responseString);
                TableLayout tl = (TableLayout) ((Activity) context).findViewById(R.id.members_table);
                // each user at json.getJSONObject(i)
                for (int i = 0; i < json.length(); i++) {
                    int userID = json.getJSONObject(i).getInt("userID");
                    //members.add(new UserInGroupView(userID, json.getJSONObject(i).getString("nick")));
                    //members.add(new UserInGroupView(json.getJSONObject(i).getInt("userID"), json.getJSONObject(i).getString("nick")));

                    TableRow row = getOrCreateRowByUserID(userID, context);
                    // draw each row with username
                    TextView nameView = new TextView(context);
                    nameView.setText(json.getJSONObject(i).getString("nick"));

                    for (UserInGroupView member : members) {
                        if (member.getUserID() == userID) {
                            member.setName(json.getJSONObject(i).getString("nick"));
                            nameView.setId(member.getNameViewID());
                        }
                    }

                    row.addView(nameView);
                    tl.addView(row);
                }

                // get all current runs done in group
                String runUrl = "/group/" + fragment.getArguments().getInt("groupID") + "/run/current";
                RestClient.get(runUrl, new RetryAsyncHttpResponseHandler(runUrl, context, fragment, RetryAsyncHttpResponseHandler.GET_REQUEST) {

                    @Override
                    public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                        // update row if no run drawn yet
                        // update row if run drawn and duration of old run > duration of new run
                        String responseString = new String(responseBody);
                        try {
                            JSONArray json = new JSONArray(responseString);
                            // go through each eligible run
                            for (int i = 0; i < json.length(); i++) {
                                int userID = ((JSONObject) json.get(i)).getJSONObject("user").getInt("userID");
                                TableRow row = getOrCreateRowByUserID(userID, context);
                                // wenn member lauf nicht contained : draw
                                for (UserInGroupView member : members) {
                                    if (member.getUserID() == userID) {
                                        double duration = ((JSONObject) json.get(i)).getDouble("duration");
                                        // no run has been drawn yet: draw this run
                                        if (member.getRunTime() == -1) {
                                            member.setRunTime(duration);
                                            TextView durationView = new TextView(context);
                                            durationView.setText(((JSONObject) json.get(i)).getString("duration"));
                                            durationView.setId(member.getDurationViewID());
                                            row.addView(durationView);
                                        }
                                        // wenn member lauf contained UND schneller als vorheriger : update
                                        else if (member.getRunTime() > duration) {
                                            TextView durationView = (TextView) ((Activity) context).findViewById(member.getDurationViewID());
                                            durationView.setText(((JSONObject) json.get(i)).getString("duration"));
                                            member.setRunTime(duration);
                                        }
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        System.out.println("noi");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    private TableRow getOrCreateRowByUserID(int userID, Context context) {
        // find view, looks through members list and looks in activity if the ID specified in the member list exists
        // if so, return existing TableRow, otherwise create it
        for (UserInGroupView member : members) {
            // row exists
            if (member.getUserID() == userID && ((Activity) context).findViewById(member.getRowID()) != null) {
                return (TableRow) ((Activity) context).findViewById(member.getRowID());
            }
        }

        // row doesn't exist
        UserInGroupView user = new UserInGroupView(userID);
        members.add(user);
        TableRow row = new TableRow(context);
        row.setId(user.getRowID());
        return row;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context activity = getActivity();
        final Fragment fragment = this;
        View rootView = inflater.inflate(R.layout.fragment_groupview, container, false);
        SharedPreferences authenticationPref = getActivity().getSharedPreferences("AuthenticationPref", 0);
        int userID = authenticationPref.getInt("userID", -1);

        String membersUrl = "/group/" + getArguments().getInt("groupID") + "/user";

        RestClient.get(membersUrl, new CustomGroupMembersRestHandler(membersUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST));

        String runsByUserUrl = "/user/" + userID + "/run";
        String currentGroupUrl = "/group/" + getArguments().getInt("groupID");
        TimeUntilRunCallback timeUntilRunCallback = new TimeUntilRunCallback(getActivity(), this);

        RestClient.get(runsByUserUrl, new CustomUserRunHandler(runsByUserUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, timeUntilRunCallback));
        RestClient.get(currentGroupUrl, new CustomGroupHandler(currentGroupUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, timeUntilRunCallback));

        return rootView;
    }

    public class CustomUserRunHandler extends RetryAsyncHttpResponseHandler {
        private TimeUntilRunCallback callback;

        public CustomUserRunHandler(String url, Context context, Fragment fragment, int requestType, TimeUntilRunCallback callback) {
            super(url, context, fragment, requestType);
            this.callback = callback;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            String response = new String(responseBody);
            callback.runObtained(response);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    public class CustomGroupHandler extends RetryAsyncHttpResponseHandler {
        private TimeUntilRunCallback callback;

        public CustomGroupHandler(String url, Context context, Fragment fragment, int requestType, TimeUntilRunCallback callback) {
            super(url, context, fragment, requestType);
            this.callback = callback;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            String response = new String(responseBody);
            callback.groupOtained(response);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    public class TimeUntilRunCallback {
        private String myRunsJSON;
        private String groupJSON;
        private Context context;
        private Fragment fragment;

        public TimeUntilRunCallback(Context context, Fragment fragment) {
            this.context = context;
            this.fragment = fragment;
        }

        public void runObtained(String run) {
            myRunsJSON = run;
            iffCallbacksDoneDrawView();
        }

        public void groupOtained(String group) {
            groupJSON = group;
            iffCallbacksDoneDrawView();
        }

        private void iffCallbacksDoneDrawView() {
            if (myRunsJSON != null && groupJSON != null) {
                Gson gson = new Gson();
                //new JsonParser().parse(groupJSON).getAsJsonObject();
                Group group = gson.fromJson(groupJSON, Group.class);
                Type listType = new TypeToken<List<Run>>(){}.getType();
                List<Run> runs = gson.fromJson(myRunsJSON, listType);


                // "Last Run: 1.1.1970 13:45"
                TextView lastRunView = (TextView) ((Activity) context).findViewById(R.id.last_run);
                Run lastRun = null;
                if (runs!=null) lastRun = runs.get(0);
                for (Run run : runs) {
                    if (run.getTimestamp() < lastRun.getTimestamp()) lastRun = run;
                }
                lastRunView.setText(new Date(lastRun.getTimestamp()).toString());

                // "Submissions: n Runs since Tuesday"
                TextView runsThisWeekView = (TextView) ((Activity) context).findViewById(R.id.runs_this_week);
                Timestamp refTimestamp = getRefDayTimestamp(group.getRefWeekday());
                int runsThisWeek = 0;
                for (Run run : runs) {
                    if (run.getTimestamp() > refTimestamp.getTime()) {
                        runsThisWeek++;
                    }
                }
                runsThisWeekView.setText("" + runsThisWeek);

                // "Submission Countdown: 4days and 16hrs"
                TextView timeLeftToRunView = (TextView) ((Activity) context).findViewById(R.id.time_left_to_run);
                Period period = new Period(refTimestamp.getNanos(), System.nanoTime());
                timeLeftToRunView.setText(period.getDays() + "d " + period.getHours() + "h " + period.getMinutes() + "m");

                // "Distance: 10km"
                TextView distanceView = (TextView) ((Activity) context).findViewById(R.id.distance);
                distanceView.setText(group.getDistance()/1000 + "km");
            }
        }

        private Timestamp getRefDayTimestamp(int refDay) {
            DateTime refDate = new LocalDate().toDateTimeAtStartOfDay();
            while (refDate.getDayOfWeek() != (refDay + 1)) {
                refDate = refDate.minusDays(1);
            }
            return new Timestamp(refDate.getMillis());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
