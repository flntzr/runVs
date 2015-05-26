package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.Header;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 5/24/15.
 */
public class GroupViewFragment extends Fragment {

    public static GroupViewFragment newInstance(int groupID) {
        GroupViewFragment groupViewFragment = new GroupViewFragment();
        Bundle args = new Bundle();
        args.putInt("groupID", groupID);
        groupViewFragment.setArguments(args);
        return groupViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groupview, container, false);
        String membersUrl = "/group/" + getArguments().getInt("groupID") + "/user";

        RestClient.get(membersUrl, new RetryAsyncHttpResponseHandler(membersUrl, getActivity(), this, RetryAsyncHttpResponseHandler.GET_REQUEST) {
            @Override
            public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
                
            }

            @Override
            public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
