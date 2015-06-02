package winzer.gh0strunner.group;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import winzer.gh0strunner.R;

/**
 * Created by franschl on 6/2/15.
 */
public class ExtInviteFragment extends Fragment implements View.OnClickListener{
    public static ExtInviteFragment newInstance(int groupID) {
        ExtInviteFragment fragment = new ExtInviteFragment();
        Bundle args = new Bundle();
        args.putInt("groupID", groupID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ext_invite, container, false);
        return rootView;
    }

    @Override
    public void onClick(View view) {

    }
}
