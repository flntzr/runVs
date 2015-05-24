package winzer.gh0strunner.run;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import winzer.gh0strunner.R;
import winzer.gh0strunner.login.LoginFragment;
import winzer.gh0strunner.login.RegisterFragment;
import winzer.gh0strunner.run.InitRunFragment;
import winzer.gh0strunner.services.RunListener;
import winzer.gh0strunner.services.RunService;

public class InitRunFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InitRunFragment newInstance(String param1, String param2) {
        InitRunFragment fragment = new InitRunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InitRunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Intent runIntent = new Intent(getActivity(), RunService.class);
        getActivity().bindService(runIntent, runConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        if (runBound) {
            getActivity().unbindService(runConnection); //TODO correct?
        }
        runBound = false;
    }

    RunService runService;
    boolean runBound = false;
    private ServiceConnection runConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iService) {
            RunService.RunBinder binder = (RunService.RunBinder) iService;
            runService = binder.getService();

            binder.setListener(new RunListener() {

                @Override
                public void startRun() {
                    final Button initRunButton = (Button) getActivity().findViewById(R.id.button_init_run);
                            initRunButton.post(new Runnable() {
                        public void run() {
                            initRunButton.setClickable(true);
                            initRunButton.setEnabled(true);
                        }
                    });
                }

                @Override
                public void updateRun() {
                    System.out.println("test update run successful");
                }

                @Override
                public void finishRun() {
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            runBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_init_run, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_init_run);
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
        FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
        tx.replace(R.id.container, ExecRunFragment.newInstance("", ""));
        tx.addToBackStack(null);
        tx.commit();
    }
}
