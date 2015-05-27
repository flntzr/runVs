package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import winzer.gh0strunner.R;
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
                        // TODO get fastest current run by user and update corresponding row
                        // update row if no run drawn yet
                        // update row if run drawn and duration of old run > duration of new run
                        String responseString = new String(responseBody);
                        try {
                            JSONArray json = new JSONArray(responseString);
                            // go through each eligible run
                            for (int i = 0; i < json.length(); i++) {
                                int userID = ((JSONObject)json.get(i)).getJSONObject("user").getInt("userID");
                                TableRow row = getOrCreateRowByUserID(userID, context);
                                // wenn member lauf nicht contained : draw
                                for (UserInGroupView member : members) {
                                    if (member.getUserID() == userID) {
                                        double duration = ((JSONObject)json.get(i)).getDouble("duration");
                                        // no run has been drawn yet: draw this run
                                        if (member.getRunTime() == -1) {
                                            member.setRunTime(duration);
                                            TextView durationView = new TextView(context);
                                            durationView.setText(((JSONObject)json.get(i)).getString("duration"));
                                            durationView.setId(member.getDurationViewID());
                                            row.addView(durationView);
                                        }
                                        // wenn member lauf contained UND schneller als vorheriger : update
                                        else if (member.getRunTime() > duration) {
                                            TextView durationView = (TextView) ((Activity)context).findViewById(member.getDurationViewID());
                                            durationView.setText(((JSONObject)json.get(i)).getString("duration"));
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
        String membersUrl = "/group/" + getArguments().getInt("groupID") + "/user";

        RestClient.get(membersUrl, new CustomGroupMembersRestHandler(membersUrl, activity, this, RetryAsyncHttpResponseHandler.GET_REQUEST));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
