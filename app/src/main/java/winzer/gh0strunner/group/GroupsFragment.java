package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Group;
import winzer.gh0strunner.dto.IntInvite;
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
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Group>>() {
                }.getType();
                List<Group> groups = gson.fromJson(new String(responseBody), listType);
                TableLayout tl = (TableLayout) getActivity().findViewById(R.id.group_table);
                // create each table row
                for (Group group : groups) {
                    TableRow row = new TableRow(context);

                    TextView groupName = new TextView(context);
                    groupName.setText(group.getName());
                    TextView groupDistance = new TextView(context);
                    groupDistance.setText("" + group.getDistance());
                    Button button = new Button(context);
                    button.setText("View");
                    button.setTag(R.id.groups_tag, "ViewGroup");

                    // set groupID as button tag
                    button.setTag(group.getGroupID());

                    groupName.setTextAppearance(context, R.style.TableCell);
                    groupDistance.setTextAppearance(context, R.style.TableCell);
                    button.setTextAppearance(context, R.style.TableCell);

                    row.addView(groupName);
                    row.addView(groupDistance);
                    row.addView(button);

                    button.setOnClickListener((View.OnClickListener) this.fragment);

                    tl.addView(row);
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
        drawInvites(userID);
        return rootView;
    }

    public void drawInvites(int userID) {
        String invUrl = "/user/" + userID + "/intinvite";
        RestClient.get(invUrl, new InviteResponseHandler(invUrl, getActivity(), this, userID, RetryAsyncHttpResponseHandler.GET_REQUEST));
    }

    public class InviteResponseHandler extends RetryAsyncHttpResponseHandler {
        int userID;

        public InviteResponseHandler(String url, Context context, Fragment fragment, int userID, int requestType) {
            super(url, context, fragment, requestType);
            this.userID = userID;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<IntInvite>>() {
            }.getType();
            String responseString = new String(responseBody);
            List<IntInvite> invites = gson.fromJson(responseString, listType);
            TableLayout tl = (TableLayout) getActivity().findViewById(R.id.group_table);

            for (IntInvite invite : invites) {
                TableRow row = new TableRow(context);
                TextView nameView = new TextView(context);
                nameView.setText(invite.getGroup().getName());
                TextView distanceView = new TextView(context);
                distanceView.setText("" + invite.getGroup().getDistance());
                Button accept = new Button(context);
                accept.setText("Join");
                accept.setTag(invite.getGroup().getGroupID());
                accept.setTag(R.id.groups_tag, "AcceptInvite");
                accept.setTag(R.id.int_inv_tag, invite.getInvitationID());
                Button reject = new Button(context);
                reject.setTag(invite.getGroup().getGroupID());
                reject.setText("Reject");
                reject.setTag(R.id.groups_tag, "RejectInvite");
                reject.setTag(R.id.int_inv_tag, invite.getInvitationID());


                accept.setOnClickListener((GroupsFragment) fragment);
                reject.setOnClickListener((GroupsFragment) fragment);

                row.addView(nameView);
                row.addView(distanceView);
                row.addView(accept);
                row.addView(reject);

                tl.addView(row);
            }

            drawGroups(userID);
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onClick(View view) {
        int userID = getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1);

        switch ((String) view.getTag(R.id.groups_tag)) {
            case "ViewGroup": {
                FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
                tx.replace(R.id.container, GroupViewFragment.newInstance((int) view.getTag()));
                tx.addToBackStack(null);
                tx.commit();
                break;
            }
            case "AcceptInvite": {
                // getTag() returns groupID
                String acceptUrl = "/group/" + view.getTag() + "/user/";
                JSONObject request = new JSONObject();
                try {
                    request.put("userID", userID);
                    RestClient.post(acceptUrl, new StringEntity(request.toString()), new AcceptInviteResponseHandler(acceptUrl, new StringEntity(request.toString()), getActivity(), this, (Integer) view.getTag(R.id.int_inv_tag), RetryAsyncHttpResponseHandler.POST_REQUEST));
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "RejectInvite": {
                String rejectUrl = "/user/" + userID + "/intinvite/" + view.getTag(R.id.int_inv_tag);
                RestClient.delete(rejectUrl, new DeleteInviteResponseHandler(rejectUrl, getActivity(), this, RetryAsyncHttpResponseHandler.DELETE_REQUEST));
                break;
            }
        }
    }

    public class AcceptInviteResponseHandler extends RetryAsyncHttpResponseHandler {
        int invID;

        public AcceptInviteResponseHandler(String url, StringEntity json, Context context, Fragment fragment, int invID, int requestType) {
            super(url, context, fragment, requestType);
            this.invID = invID;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // remove invite
            String deleteUrl = "/user/" + getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1) + "/intinvite/" + invID;
            RestClient.delete(deleteUrl, new DeleteInviteResponseHandler(deleteUrl, context, fragment, RetryAsyncHttpResponseHandler.DELETE_REQUEST));

            FragmentTransaction ftx = getFragmentManager().beginTransaction();
            ftx.detach(fragment);
            ftx.attach(fragment);
            ftx.commit();
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            System.out.println("NOT ACCEPTED!");
        }
    }

    public class DeleteInviteResponseHandler extends RetryAsyncHttpResponseHandler {

        public DeleteInviteResponseHandler(String url, Context context, Fragment fragment, int requestType) {
            super(url, context, fragment, requestType);
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            FragmentTransaction ftx = getFragmentManager().beginTransaction();
            ftx.detach(fragment);
            ftx.attach(fragment);
            ftx.commit();
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            System.out.println("NOT REJECTED!");
        }
    }
}
