package winzer.gh0strunner.group;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import winzer.gh0strunner.R;
import winzer.gh0strunner.login.LoginFragment;

/**
 * Created by franschl on 6/1/15.
 */
public class IntInviteFragment extends Fragment implements View.OnClickListener {
    public static IntInviteFragment newInstance(int groupID) {
        IntInviteFragment fragment = new IntInviteFragment();
        Bundle args = new Bundle();
        args.putInt("groupID", groupID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_int_invite, container, false);
        Button button = (Button) rootView.findViewById(R.id.search_submit);
        button.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.search_submit:
                String args = ((EditText)getActivity().findViewById(R.id.search_term)).getText().toString();
                tx.replace(R.id.container, IntInviteResultFragment.newInstance(args, getArguments().getInt("groupID")));
                break;
        }
        tx.addToBackStack(null);
        tx.commit();
    }
}
