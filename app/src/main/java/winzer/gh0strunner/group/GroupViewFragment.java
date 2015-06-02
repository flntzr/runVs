package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;

import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Group;
import winzer.gh0strunner.dto.Run;
import winzer.gh0strunner.dto.User;
import winzer.gh0strunner.dto.UserInGroupView;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by franschl on 5/24/15.
 */
public class GroupViewFragment extends Fragment implements MenuItem.OnMenuItemClickListener {

    final List<UserInGroupView> members = new ArrayList<>();
    int int_invite_id;
    int ext_invite_id;

    public static GroupViewFragment newInstance(int groupID) {
        GroupViewFragment groupViewFragment = new GroupViewFragment();
        Bundle args = new Bundle();
        args.putInt("groupID", groupID);
        groupViewFragment.setArguments(args);
        return groupViewFragment;
    }

    public void share(String pin) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("plain/text");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "You were invited to join a group in Gh0strunner");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Get the Gh0strunner app and enter the following code in the app to join the group:\n" + pin);
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, "Get the app and enter the following code in the app to join the group:<br/><b>" + pin + "</b>");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
        if (menuItem.getItemId() == int_invite_id) {
            tx.replace(R.id.container, IntInviteFragment.newInstance(getArguments().getInt("groupID")));
            tx.addToBackStack(null);
            tx.commit();
            return true;
        } else if (menuItem.getItemId() == ext_invite_id) {
            String url = "/extinvite";
            JSONObject request = new JSONObject();
            try {
                request.put("hostID", getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1));
                request.put("groupID", getArguments().getInt("groupID"));
                RestClient.post(url, new StringEntity(request.toString()), new CreateExtInviteResponseHandler(url, new StringEntity(request.toString()), getActivity(), RetryAsyncHttpResponseHandler.POST_REQUEST));

            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public class CreateExtInviteResponseHandler extends RetryAsyncHttpResponseHandler {

        public CreateExtInviteResponseHandler(String url, StringEntity json, Context context, int requestType) {
            super(url, json, context, requestType);
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            share(new String(responseBody));
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    public class CustomGroupMembersResponseHandler extends RetryAsyncHttpResponseHandler {
        ViewMembersCallback callback;

        public CustomGroupMembersResponseHandler(String url, Context context, Fragment fragment, int requestType, ViewMembersCallback callback) {
            super(url, context, fragment, requestType);
            this.callback = callback;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // get all group members
            final String responseString = new String(responseBody);
            callback.usersOtained(responseString);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    public class CustomAdminRestHandler extends RetryAsyncHttpResponseHandler {
        ViewMembersCallback callback;

        public CustomAdminRestHandler(String url, Context context, Fragment fragment, int requestType, ViewMembersCallback callback) {
            super(url, context, fragment, requestType);
            this.callback = callback;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // get all group members
            final String responseString = new String(responseBody);
            callback.adminObtained(responseString);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    private class ViewMembersCallback {
        List<User> userList;
        List<Run> runList;
        User admin;

        private Context context;
        Gson gson = new Gson();

        public ViewMembersCallback(Context context) {
            this.context = context;
        }

        public void adminObtained(String admin) {
            this.admin = gson.fromJson(admin, User.class);
            iffCallbacksDoneDrawView();
        }

        public void runsObtained(String runs) {
            Type listType = new TypeToken<List<Run>>() {
            }.getType();
            this.runList = gson.fromJson(runs, listType);
            iffCallbacksDoneDrawView();
        }

        public void usersOtained(String users) {
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            this.userList = gson.fromJson(users, listType);
            iffCallbacksDoneDrawView();
        }

        private void iffCallbacksDoneDrawView() {
            if (runList == null || userList == null || admin == null) {
                return;
            }

            // set Admin flag
            boolean isAdmin = (admin.getUserID() == context.getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1));

            // Sort current run list by duration
            Collections.sort(runList, new Comparator<Run>() {
                @Override
                public int compare(Run run, Run run2) {
                    if (run.getDuration() > run2.getDuration()) return 1;
                    else if (run.getDuration() < run2.getDuration()) return -1;
                    else return 0;
                }
            });

            // filter out multiple runs done by one user
            for (int i = 0; i < runList.size(); i++) {
                if (runList.size() - 1 > i) {
                    if (runList.get(i).getUser().getUserID() == runList.get(i + 1).getUser().getUserID()) {
                        runList.remove(i);
                        i--;
                    }
                }
            }
            // add medals
            int medal = 0;
            TableLayout tl = (TableLayout) ((Activity) context).findViewById(R.id.members_table);
            for (int i = 0; i < runList.size(); i++) {

                // if this is last iteration
                if (runList.size() == i + 1 && runList.size() != 1 && runList.get(i).getDuration() > runList.get(i-1).getDuration()) {
                    medal++;
                }
                // If duration not equal
                if (runList.size() > i + 1 && runList.get(i).getDuration() < runList.get(i + 1).getDuration()) {
                    medal++;
                }

                TableRow row = new TableRow(context);
                TextView userView = new TextView(context);
                TextView durationView = new TextView(context);
                TextView medalView = new TextView(context);
                userView.setText(runList.get(i).getUser().getNick());
                durationView.setText("" + runList.get(i).getDuration());
                medalView.setText("" + medal);
                row.addView(userView);
                row.addView(durationView);
                row.addView(medalView);

                if (isAdmin) {
                    Button kickButton = new Button(context);
                    kickButton.setText("Kick");
                    row.addView(kickButton);
                }

                tl.addView(row);
            }

            // add Rows for users without runs
            for (User user : userList) {
                boolean isContained = false;
                for (Run run : runList) {
                    if (run.getUser().getUserID() == user.getUserID()) {
                        isContained = true;
                    }
                }
                if (!isContained) {
                    TableRow row = new TableRow(context);
                    TextView userView = new TextView(context);
                    TextView durationView = new TextView(context);
                    TextView medalView = new TextView(context);
                    userView.setText(user.getNick());
                    durationView.setText("No Runs");
                    medalView.setText("-");
                    row.addView(userView);
                    row.addView(durationView);
                    row.addView(medalView);
                    tl.addView(row);

                    if (isAdmin) {
                        Button kickButton = new Button(context);
                        kickButton.setText("Kick");
                        row.addView(kickButton);
                    }
                }
            }

        }

    }

    private class CustomCurrentRunResponseHandler extends RetryAsyncHttpResponseHandler {
        ViewMembersCallback callback;

        public CustomCurrentRunResponseHandler(String url, Context context, Fragment fragment, int requestType, ViewMembersCallback callback) {
            super(url, context, fragment, requestType);
            this.callback = callback;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // update row if no run drawn yet
            // update row if run drawn and duration of old run > duration of new run
            String responseString = new String(responseBody);
            callback.runsObtained(responseString);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            error.printStackTrace();
        }
    }

    public void unhideCustomActionBarItems() {
        //Menu actionBar = (Menu) getActivity().findViewById(R.id.action_bar_main_menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //MenuItem intInviteItem = menu.add("Invite");
        inflater.inflate(R.menu.groupview, menu);
        MenuItem intInviteItem = menu.findItem(R.id.int_inv);
        intInviteItem.setIcon(R.drawable.ic_action_add_w);
        intInviteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        intInviteItem.setOnMenuItemClickListener(this);
        int_invite_id = intInviteItem.getItemId();
        //MenuItem extInviteItem = menu.add("Invite external user");
        MenuItem extInviteItem = menu.findItem(R.id.ext_inv);
        extInviteItem.setIcon(R.drawable.ic_action_ext_inv);
        extInviteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        extInviteItem.setOnMenuItemClickListener(this);
        ext_invite_id = extInviteItem.getItemId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == int_invite_id){
            // TODO add user
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unhideCustomActionBarItems();
        final Context activity = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_groupview, container, false);
        SharedPreferences authenticationPref = getActivity().getSharedPreferences("AuthenticationPref", 0);
        int userID = authenticationPref.getInt("userID", -1);

        String membersUrl = "/group/" + getArguments().getInt("groupID") + "/user";
        String runUrl = "/group/" + getArguments().getInt("groupID") + "/run/current";
        String adminUrl = "/group/" + getArguments().getInt("groupID") + "/admin";

        ViewMembersCallback callback = new ViewMembersCallback(getActivity());

        RestClient.get(adminUrl, new CustomAdminRestHandler(adminUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, callback));
        RestClient.get(membersUrl, new CustomGroupMembersResponseHandler(membersUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, callback));
        RestClient.get(runUrl, new CustomCurrentRunResponseHandler(runUrl, getActivity(), this, RetryAsyncHttpResponseHandler.GET_REQUEST, callback));

        String runsByUserUrl = "/user/" + userID + "/run";
        String currentGroupUrl = "/group/" + getArguments().getInt("groupID");
        ViewAdditionalInfoCallback viewAdditionalInfoCallback = new ViewAdditionalInfoCallback(getActivity());

        RestClient.get(runsByUserUrl, new CustomUserRunHandler(runsByUserUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, viewAdditionalInfoCallback));
        RestClient.get(currentGroupUrl, new CustomGroupHandler(currentGroupUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST, viewAdditionalInfoCallback));

        return rootView;
    }

    public class CustomUserRunHandler extends RetryAsyncHttpResponseHandler {
        private ViewAdditionalInfoCallback callback;

        public CustomUserRunHandler(String url, Context context, Fragment fragment, int requestType, ViewAdditionalInfoCallback callback) {
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
        private ViewAdditionalInfoCallback callback;

        public CustomGroupHandler(String url, Context context, Fragment fragment, int requestType, ViewAdditionalInfoCallback callback) {
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

    public class ViewAdditionalInfoCallback {
        private String myRunsJSON;
        private String groupJSON;
        private Context context;

        public ViewAdditionalInfoCallback(Context context) {
            this.context = context;
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
                Type listType = new TypeToken<List<Run>>() {
                }.getType();
                List<Run> runs = gson.fromJson(myRunsJSON, listType);


                // "Last Run: 1.1.1970 13:45"
                TextView lastRunView = (TextView) ((Activity) context).findViewById(R.id.last_run);
                Run lastRun = null;
                if (runs.size() != 0) lastRun = runs.get(0);
                for (Run run : runs) {
                    if (run.getTimestamp() < lastRun.getTimestamp()) lastRun = run;
                }
                if (lastRun != null) {
                    DateTime dt = new DateTime(lastRun.getTimestamp());
                    lastRunView.setText(dt.toString("d MMM YYYY HH:mm"));
                }

                // "Submissions: n Runs since Tuesday"
                TextView runsThisWeekView = (TextView) ((Activity) context).findViewById(R.id.runs_this_week);
                Timestamp refTimestamp = getRefDayTimestamp(group.getRefWeekday());
                int runsThisWeek = 0;
                for (Run run : runs) {
                    if (run.getGroups().contains(group) && run.getTimestamp() > refTimestamp.getTime()) {
                        runsThisWeek++;
                    }
                }
                runsThisWeekView.setText("" + runsThisWeek);

                // "Submission Countdown: 4days and 16hrs"
                TextView timeLeftToRunView = (TextView) ((Activity) context).findViewById(R.id.time_left_to_run);
                Period period = new Period(refTimestamp.getNanos() / 1000, new Date().getTime() / 1000);
                timeLeftToRunView.setText(period.getDays() + "d " + period.getHours() + "h " + period.getMinutes() + "m");

                // "Distance: 10km"
                TextView distanceView = (TextView) ((Activity) context).findViewById(R.id.distance);
                distanceView.setText(group.getDistance() / 1000 + "km");
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
