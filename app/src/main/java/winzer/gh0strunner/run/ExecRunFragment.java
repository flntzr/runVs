package winzer.gh0strunner.run;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import winzer.gh0strunner.R;

public class ExecRunFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener.execRun();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exec_run, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_abort);
        button.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ExecRunListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ExecRunListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateUI(double distance, double distancePassed, double avDistanceModifier, double advancement, long duration, String[] ghosts, double[] ghostDistances, double[] ghostAdvancements, int position) {
//        ##################
//        #                #
//        # TimeRan: x min #
//        # Pos:     x     #
//        #                #
//        ##################
//        MsRan:   x m
//        MsToRun: x m
//        Advancement: x m
        TextView ui = (TextView) getActivity().findViewById(R.id.run_ui);
        String sGhost = "";
        if (ghosts != null) {
            for (int i = 0; i < ghosts.length; i++) {
                sGhost += ghosts[i] + ": " + (ghostAdvancements[i] * 100) + "%, " + ghostDistances[i] + "m\n";
            }
        }
        ui.setText("Distance: " + distance + "m\nDistance Passed: " + distancePassed + "m\nAverage Distance Multiplier caused by slope" + avDistanceModifier + "\nAdvancement: " + (advancement * 100) + "%\n " + "Time: " + duration + "ms\n" + sGhost + "\nPosition: " + position);
    }

    @Override
    public void onClick(View view) {
        listener.abortRun();
    }

    ExecRunListener listener;

    public interface ExecRunListener {
        public void execRun();
        public void abortRun();
    }

}
