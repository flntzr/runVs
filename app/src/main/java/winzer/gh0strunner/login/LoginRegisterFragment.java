package winzer.gh0strunner.login;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import winzer.gh0strunner.R;

public class LoginRegisterFragment extends Fragment implements View.OnClickListener {

    public static LoginRegisterFragment newInstance() {
        LoginRegisterFragment fragment = new LoginRegisterFragment();
        return fragment;
    }

    public LoginRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login_register, container, false);

        Button loginButton = (Button) rootView.findViewById(R.id.button_login);
        Button registerButton = (Button) rootView.findViewById(R.id.button_register);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

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
        switch(view.getId()) {
            case R.id.button_login:
                tx.replace(R.id.container, new LoginFragment());
                break;
            case R.id.button_register:
                tx.replace(R.id.container, new RegisterFragment());
                break;
        }
        tx.addToBackStack(null);
        tx.commit();
    }
}
