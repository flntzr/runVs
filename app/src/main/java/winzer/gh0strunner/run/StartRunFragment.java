package winzer.gh0strunner.run;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Run;
import winzer.gh0strunner.dto.User;
import winzer.gh0strunner.services.LoadGhostsCallback;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franschl on 5/23/15.
 */
public class StartRunFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static StartRunFragment newInstance(int sectionNumber) {
        StartRunFragment fragment = new StartRunFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_run, container, false);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.distances_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.distances_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Button button = (Button) rootView.findViewById(R.id.button_start_run);
        button.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    int distance = 0;
    int groupsLoaded = 0;

    @Override
    public void onClick(View view) {
        //use selected groups for running ghosts
        final ArrayList<Integer> groups = new ArrayList<Integer>();
        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.checkbox_container);
        boolean useGhosts = false;
        for (int i = 0; i < ll.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) ll.getChildAt(i);
            if (checkBox.isChecked() && checkBox.getVisibility() == View.VISIBLE) {
                groups.add((Integer) checkBox.getTag(R.id.checkbox_groupid));
                useGhosts = true;
            }
        }

        final ArrayList<String> ghosts = new ArrayList<String>();
        final ArrayList<Long> ghostDurations = new ArrayList<Long>();
        if (useGhosts) {
            getGhosts(groups, new LoadGhostsCallback() {
                @Override
                public void ghostsLoaded(ArrayList<String> newGhosts, ArrayList<Long> newGhostDurations) {
                    groupsLoaded++;
                    ghosts.addAll(newGhosts);
                    ghostDurations.addAll(newGhostDurations);
                    if (groups.size() == groupsLoaded) {
                        Intent intent = new Intent(getActivity(), RunActivity.class);
                        intent.putExtra("groups", groups);
                        intent.putExtra("distance", distance);
                        String[] ghostArr = new String[ghosts.size()];
                        //convert to primitive long
                        long[] ghostDurationArr = new long[ghostDurations.size()];
                        for (int i = 0; i < ghostDurations.size(); i++) {
                            ghostDurationArr[i] = ghostDurations.get(i);
                        }
                        intent.putExtra("ghosts", ghosts.toArray(ghostArr));
                        intent.putExtra("ghostDurations", ghostDurationArr);
                        startActivity(intent);
                    }
                }
            });
        } else {
            Intent intent = new Intent(getActivity(), RunActivity.class);
            intent.putExtra("distance", distance);
            startActivity(intent);
        }

    }

    public void getGhosts(ArrayList<Integer> groups, final LoadGhostsCallback lgc) {

        final ArrayList<String> newGhosts = new ArrayList<String>();
        final ArrayList<Long> newGhostDurations = new ArrayList<Long>();
        for (int groupID : groups) {
            String url = "/group/" + groupID + "/run/current";
            RestClient.get(url, new RetryAsyncHttpResponseHandler(url, getActivity(), this, RetryAsyncHttpResponseHandler.GET_REQUEST) {
                @Override
                public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody, "UTF-8");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Run>>() {
                        }.getType();
                        List<Run> runs = gson.fromJson(response, listType);
                        for (Run run : runs) {
                            User user = run.getUser();
                            if (user.getUserID() != getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1)) {
                                newGhosts.add(user.getNick());
                                newGhostDurations.add(run.getDuration());
                            }
                        }
                        lgc.ghostsLoaded(newGhosts, newGhostDurations);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("Error");
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        final int selectedDistance = getDistanceFromAdapterID(i);
        final LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.checkbox_container);
        boolean alreadyLoaded = false;
        for (int j = 0; j < ll.getChildCount(); j++) {
            CheckBox checkBox = (CheckBox) ll.getChildAt(j);
            int checkBoxDistance = (int) checkBox.getTag(R.id.checkbox_distance);
            if (checkBoxDistance == selectedDistance) {
                alreadyLoaded = true;
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }

        }
        distance = selectedDistance;
        if (!alreadyLoaded) {
            SharedPreferences authenticationPref = getActivity().getSharedPreferences("AuthenticationPref", 0);
            int userID = authenticationPref.getInt("userID", -1);
            String url = "/user/" + userID + "/group";
            RestClient.get(url, new RetryAsyncHttpResponseHandler(url, getActivity(), this, RetryAsyncHttpResponseHandler.GET_REQUEST) {
                @Override
                public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    try {
                        JSONArray json = new JSONArray(response);
                        for (int i = 0; i < json.length(); i++) {
                            int groupDistance = json.getJSONObject(i).getInt("distance");
                            if (groupDistance == selectedDistance) {
                                CheckBox checkBox = new CheckBox(getActivity());
                                checkBox.setText(json.getJSONObject(i).getString("name"));
                                checkBox.setTag(R.id.checkbox_distance, selectedDistance);
                                checkBox.setTag(R.id.checkbox_groupid, json.getJSONObject(i).getInt("groupID"));
                                ll.addView(checkBox);
                                distance = selectedDistance;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
