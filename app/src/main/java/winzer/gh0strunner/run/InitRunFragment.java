package winzer.gh0strunner.run;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.login.LoginFragment;
import winzer.gh0strunner.login.RegisterFragment;
import winzer.gh0strunner.run.InitRunFragment;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RunListener;
import winzer.gh0strunner.services.RunService;

public class InitRunFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_init_run, container, false);
        Button buttonRun = (Button) rootView.findViewById(R.id.button_init_run);
        buttonRun.setOnClickListener(this);
        Button buttonCancel = (Button) rootView.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InitRunListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ExecRunListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void startRun() {
        final Button button = (Button) getActivity().findViewById(R.id.button_init_run);
        button.post(new Runnable() {
            public void run() {
                button.setClickable(true);
                button.setEnabled(true);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_init_run:
                FragmentTransaction tx = getActivity().getFragmentManager().beginTransaction();
                tx.replace(R.id.container, new ExecRunFragment());
                tx.addToBackStack(null);
                tx.commit();
                break;
            case R.id.button_cancel:
                listener.cancelRun();
        }
    }

    InitRunListener listener;

    public interface InitRunListener {
        public void cancelRun();
    }

}
