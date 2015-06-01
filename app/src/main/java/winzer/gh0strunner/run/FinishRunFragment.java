package winzer.gh0strunner.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;

public class FinishRunFragment extends Fragment implements View.OnClickListener {

    private static final String ACTUAL_DISTANCE = "actualDistance";
    private static final String DURATION = "duration";
    private static final String GHOSTS = "ghosts";
    private static final String GHOST_DURATIONS = "ghostDurations";
    private static final String POSITION = "position";

    private double actualDistance;
    private long duration;
    private String[] ghosts;
    private long[] ghostDurations;
    private int position;

    public static FinishRunFragment newInstance(double actualDistance, long duration, String[] ghosts, long[] ghostDurations, int position) {
        FinishRunFragment fragment = new FinishRunFragment();
        Bundle args = new Bundle();
        args.putDouble(ACTUAL_DISTANCE, actualDistance);
        args.putLong(DURATION, duration);
        args.putStringArray(GHOSTS, ghosts);
        args.putLongArray(GHOST_DURATIONS, ghostDurations);
        fragment.setArguments(args);
        return fragment;
    }

    public FinishRunFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actualDistance = getArguments().getDouble(ACTUAL_DISTANCE);
            duration = getArguments().getLong(DURATION);
            ghosts = getArguments().getStringArray(GHOSTS);
            ghostDurations = getArguments().getLongArray(GHOST_DURATIONS);
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish_run, container, false);
        TextView ui = (TextView) rootView.findViewById(R.id.run_statistics);
        String sGhost = "";
        if (ghosts != null) {
            for (int i = 0; i < ghosts.length; i++) {
                sGhost += ghosts[i] + " took " + ghostDurations[i] + "ms for this run";
            }
        }
        ui.setText("You ran " + actualDistance + "m in " + duration + "ms\n" + sGhost + "\nPosition:" + position + "/" + (sGhost.length() + 1) + "\nYour run has been tracked as gpx in /sdcard/gh0strunner");
        Button button = (Button) rootView.findViewById(R.id.button_return);
        button.setOnClickListener(this);
        return rootView;
//        1st: Name Time
//        2nd: Name Time
//        3rd: Name Time
//        4th: Name Time
//        5th: Name Time
//
//        You finished on place 7/10
//        You ran x meters in y minutes
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
