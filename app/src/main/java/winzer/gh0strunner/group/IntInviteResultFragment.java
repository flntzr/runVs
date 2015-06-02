package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.List;

import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.User;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 6/1/15.
 */
public class IntInviteResultFragment extends Fragment implements View.OnClickListener {

    public static IntInviteResultFragment newInstance(String searchTerm, int groupID) {
        IntInviteResultFragment fragment = new IntInviteResultFragment();
        Bundle args = new Bundle();
        args.putString("term", searchTerm);
        args.putInt("groupID", groupID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_int_invite_result, container, false);
        String url = "/user/";
        RestClient.get(url, new FindUsersResponseHandler(url, (Context) getActivity(), this, getArguments().getString("term"), getArguments().getInt("groupID"), getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1),  RetryAsyncHttpResponseHandler.GET_REQUEST));
        return rootView;
    }

    public class FindUsersResponseHandler extends RetryAsyncHttpResponseHandler implements View.OnClickListener {
        String searchTerm;
        List<User> users;
        int groupID;
        int hostID;


        public FindUsersResponseHandler(String url, Context context, Fragment fragment, String searchTerm, int groupID, int hostID, int requestType) {
            super(url, context, fragment, requestType);
            this.searchTerm = searchTerm;
            this.groupID = groupID;
            this.hostID = hostID;
        }

        public void applyFiltersToUserList() {
            List<User> filteredUsers = new ArrayList<>();
            for (User user : users) {
                if (user.getNick().contains(searchTerm) && user.getUserID() != hostID) {
                    filteredUsers.add(user);
                }
            }
            this.users = filteredUsers;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(new String(responseBody), listType);
            applyFiltersToUserList();

            for (User user : users) {
                TableLayout tl = (TableLayout) ((Activity) context).findViewById(R.id.search_result_table);
                TableRow row = new TableRow(context);
                TextView nameView = new TextView(context);
                nameView.setText(user.getNick());
                Button invButton = new Button(context);
                invButton.setText("Invite");
                invButton.setTag(user.getUserID());
                invButton.setOnClickListener(this);
                row.addView(nameView);
                row.addView(invButton);
                tl.addView(row);
            }
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }

        @Override
        public void onClick(View view) {
            // button tag contains user ID
            String url = "/user/" + view.getTag() + "/intinvite";
            JSONObject request = new JSONObject();
            try {
                request.put("hostID", getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1));
                request.put("groupID", groupID);
                StringEntity jsonStringEntity = new StringEntity(request.toString());
                RestClient.post(url, jsonStringEntity, new InviteUserResponseHandler(url, jsonStringEntity, context, fragment, groupID, RetryAsyncHttpResponseHandler.POST_REQUEST));
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public class InviteUserResponseHandler extends RetryAsyncHttpResponseHandler {
        int groupID;

        public InviteUserResponseHandler(String url, StringEntity json, Context context, Fragment fragment, int groupID, int requestType) {
            super(url, context, fragment, requestType);
            this.groupID = groupID;
            super.json = json;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
            tx.replace(R.id.container, GroupViewFragment.newInstance(groupID));
            tx.addToBackStack(null);
            tx.commit();
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            System.out.println("Async REST call failure, status code: " + statusCode);
            System.out.println(responseBody);
        }
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
        switch (view.getId()) {
            //...
        }
    }


}
