package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 5/23/15.
 */
public class GroupsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static GroupsFragment newInstance(int sectionNumber) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void drawGroups(final int userID) {
        String relativeUrl = "/user/" + userID + "/group";
        RestClient.get(relativeUrl, new RetryAsyncHttpResponseHandler(relativeUrl, getActivity(), this, RetryAsyncHttpResponseHandler.GET_REQUEST) {

            @Override
            public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONArray json = new JSONArray(response);
                    TableLayout tl = (TableLayout) getActivity().findViewById(R.id.group_table);
                    // create each table row
                    for (int i = 0; i < json.length(); i++) {
                        TableRow row = new TableRow(getActivity());

                        TextView groupName = new TextView(getActivity());
                        groupName.setText(json.getJSONObject(i).getString("name"));
                        TextView groupDistance = new TextView(getActivity());
                        groupDistance.setText(json.getJSONObject(i).getString("distance"));
                        Button button = new Button(getActivity());
                        button.setText("View");
                        // set groupID as button tag
                        button.setTag(json.getJSONObject(i).getInt("groupID"));

                        row.addView(groupName);
                        row.addView(groupDistance);
                        row.addView(button);

                        button.setOnClickListener((View.OnClickListener) this.fragment);

                        tl.addView(row);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // TODO error
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        SharedPreferences authenticationPref = getActivity().getSharedPreferences("AuthenticationPref", 0);
        int userID = authenticationPref.getInt("userID", -1);
        drawGroups(userID);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    // Button pressed
    @Override
    public void onClick(View view) {
        FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
        tx.replace(R.id.container, GroupViewFragment.newInstance((int) view.getTag()));
        tx.addToBackStack(null);
        tx.commit();
    }
}
