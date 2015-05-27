package winzer.gh0strunner.run;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
public class StartRunFragment extends Fragment implements View.OnClickListener{

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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), RunActivity.class);
        int distance = 5;
        String[] ghosts = {"ghost1", "ghost2", "ghost3", "ghost4"};
        long[] times = {1800000, 1200000, 1500000, 900000};
        intent.putExtra("distance", distance);
        intent.putExtra("ghosts", ghosts);
        intent.putExtra("times", times);
        startActivity(intent);
    }
}
