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
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.Header;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Group;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 6/2/15.
 */
public class FollowExtInviteFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FollowExtInviteFragment newInstance(int sectionNumber) {
        FollowExtInviteFragment fragment = new FollowExtInviteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow_ext_invite, container, false);
        rootView.findViewById(R.id.pin_submit).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        String invUrl = "/user/" + getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1) + "/extinvite/accept/" + ((EditText)getActivity().findViewById(R.id.enter_pin)).getText();
        RestClient.post(invUrl, null, new FollowInvitationResponseHandler(invUrl, getActivity(), this, RetryAsyncHttpResponseHandler.POST_REQUEST));
    }

    public class FollowInvitationResponseHandler extends RetryAsyncHttpResponseHandler {

        public FollowInvitationResponseHandler(String url, Context context, Fragment fragment, int requestType) {
            super(url, context, fragment, requestType);
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            Gson gson = new Gson();
            Group group = gson.fromJson(new String(responseBody), Group.class);
            FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
            tx.replace(R.id.container, GroupViewFragment.newInstance(group.getGroupID()));
            tx.addToBackStack(null);
            tx.commit();
        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            TextView pin_err_view = (TextView)((Activity)context).findViewById(R.id.pin_error);
            pin_err_view.setVisibility(View.VISIBLE);
        }
    }
}
