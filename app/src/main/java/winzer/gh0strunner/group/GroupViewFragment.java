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
            String responseString = new String(responseBody);
            try {
                JSONArray json = new JSONArray(responseString);
                TableLayout tl = (TableLayout) ((Activity) context).findViewById(R.id.members_table);
                // each user at json.getJSONObject(i)
                for (int i = 0; i < json.length(); i++) {
                    int userID = json.getJSONObject(i).getInt("userID");
                    members.add(new UserInGroupView(userID, json.getJSONObject(i).getString("nick")));
                    String runUrl = "/user/" + userID + "/run";
                    members.add(new UserInGroupView(json.getJSONObject(i).getInt("userID"), json.getJSONObject(i).getString("nick")));

                    TableRow row = new TableRow(context);
                    row.setTag(userID);
                    TextView nameView = new TextView(context);
                    nameView.setText(json.getJSONObject(i).getString("nick"));

                    row.addView(nameView);
                    tl.addView(row);
                    RestClient.get(runUrl, new RetryAsyncHttpResponseHandler(runUrl, context, fragment, RetryAsyncHttpResponseHandler.GET_REQUEST) {

                        @Override
                        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                            // TODO get run time and if done after ref date update corresponding row;
                        }

                        @Override
                        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
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
