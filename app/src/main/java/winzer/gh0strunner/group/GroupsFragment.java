package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        //Button groupViewButton = (Button) rootView.findViewById(R.id.go_to_group_view);

        Button button = new Button(getActivity());
        button.setText("Dynamically created button");
        button.setId(1);
        RelativeLayout fragmentLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_groups_layout);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fragmentLayout.addView(button, lp);

        button.setOnClickListener(this);
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
        switch (view.getId()) {
            // onClick for go_to_group_view button
            case 1:
                FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
                tx.replace(R.id.container, new GroupViewFragment());
                tx.addToBackStack(null);
                tx.commit();
                break;
        }
    }
}
