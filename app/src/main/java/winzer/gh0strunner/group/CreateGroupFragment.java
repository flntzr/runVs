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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Group;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 6/3/15.
 */
public class CreateGroupFragment extends Fragment implements View.OnClickListener {
    int distance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.create_group_distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.distances_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new DistanceListener());
        Button submit = (Button) rootView.findViewById(R.id.create_group_submit);
        submit.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        String groupName = ((EditText)getActivity().findViewById(R.id.create_group_name)).getText().toString();
        String createGroupUrl = "/group/";
        try {
            JSONObject request = new JSONObject();
            request.put("name", groupName);
            request.put("admin", getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1));
            request.put("distance", distance);
            RestClient.post(createGroupUrl, new StringEntity(request.toString()), new CreateGroupResponseHandler(createGroupUrl, new StringEntity(request.toString()), getActivity(), this, RetryAsyncHttpResponseHandler.POST_REQUEST));
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class CreateGroupResponseHandler extends RetryAsyncHttpResponseHandler {

        public CreateGroupResponseHandler(String url, StringEntity json, Context context, Fragment fragment, int requestType) {
            super(url, json, context, requestType);
            super.fragment = fragment;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            // transform json to object
            Gson gson = new Gson();
            Group group = gson.fromJson(new String(responseBody), Group.class);

            FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
            tx.replace(R.id.container, GroupViewFragment.newInstance(group.getGroupID()));
            tx.addToBackStack(null);
            tx.commit();
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            TextView errorView = (TextView) ((Activity) context).findViewById(R.id.create_group_error);
            // TODO error only on BAD_REQUEST
            errorView.setVisibility(View.VISIBLE);
        }
    }

    public class DistanceListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            distance = getDistanceFromAdapterID(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    private int getDistanceFromAdapterID(int id) {
        int distance;
        switch (id) {
            case 0:
                distance = 2000;
                break;
            case 1:
                distance = 5000;
                break;
            case 2:
                distance = 8000;
                break;
            case 3:
                distance = 10000;
                break;
            case 4:
                distance = 15000;
                break;
            case 5:
                distance = 20000;
                break;
            default:
                distance = 0;
        }
        return distance;
    }
}
