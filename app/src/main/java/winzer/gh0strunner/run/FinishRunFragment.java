package winzer.gh0strunner.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.Ghost;

public class FinishRunFragment extends Fragment implements View.OnClickListener {

    private static final String ACTUAL_DISTANCE = "actualDistance";
    private static final String DURATION = "duration";
    private static final String GHOSTS = "ghosts";
    private static final String GHOST_DURATIONS = "ghostDurations";
    private static final String POSITION = "position"; // 0 bis n

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

        ArrayList<Ghost> ghostList = new ArrayList<>();

        // add ghosts into list
        for (int i = 0; i < ghosts.length; i++) {
            ghostList.add(new Ghost(ghosts[i], ghostDurations[i]));
        }

        ghostList.add(new Ghost("This Run", duration));

        Collections.sort(ghostList, new Comparator<Ghost>() {
            @Override
            public int compare(Ghost g1, Ghost g2) {
                if (g1.getDuration() > g2.getDuration()) return 1;
                else if (g1.getDuration() < g2.getDuration()) return -1;
                else return 0;
            }
        });

//        1st: Name Time
//        2nd: Name Time
//        3rd: Name Time
//        4th: Name Time
//        5th: Name Time
//
//        You finished on place 7/10
//        You ran x meters in y minutes
        TableLayout tl = (TableLayout) rootView.findViewById(R.id.run_stat_table);
        int i = 0;
        for (Ghost ghost : ghostList) {
            ++i;
            TableRow row = new TableRow(getActivity());
            Period period = new Period(ghost.getDuration());

            TextView positionView = new TextView(getActivity());
            TextView nameView = new TextView(getActivity());
            TextView timeView = new TextView(getActivity());

            positionView.setTextAppearance(getActivity(), R.style.TableCell);
            nameView.setTextAppearance(getActivity(), R.style.TableCell);
            timeView.setTextAppearance(getActivity(), R.style.TableCell);

            positionView.setText("" + i);
            positionView.setTextAppearance(getActivity(), R.style.bold);
            nameView.setText(ghost.getName());
            timeView.setText(period.getHours() + "h " + period.getMinutes() + "m " + period.getSeconds() + "s");

            row.addView(positionView);
            row.addView(nameView);
            row.addView(timeView);
            tl.addView(row);
        }

        Period durationPeriod = new Period(duration);

        TextView resultView = (TextView) rootView.findViewById(R.id.your_result);
        TextView distanceView = (TextView) rootView.findViewById(R.id.your_distance);

        String roundedDistance = String.format("%.2f", actualDistance);

        resultView.setText("#" + (position + 1) + " out of " + (ghosts.length + 1));
        distanceView.setText("You ran " + roundedDistance + "m in " + durationPeriod.getHours() + "h " + durationPeriod.getMinutes() + "m " + durationPeriod.getSeconds() + "s");

        Button button = (Button) rootView.findViewById(R.id.button_return);
        button.setOnClickListener(this);
        return rootView;
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
