package winzer.gh0strunner.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.login.LoginFragment;
import winzer.gh0strunner.services.RestClient;

public class LoginFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_login_submit);
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
        String username = ((EditText) getActivity().findViewById(R.id.field_username)).getText().toString();
        String password = ((EditText) getActivity().findViewById(R.id.field_password)).getText().toString();
        login(username, password);
    }

    public void login(final String name, final String password) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("name", name);
            jsonParams.put("password", password);
            StringEntity entity = new StringEntity(jsonParams.toString());

            RestClient.post("/login", entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONObject json = new JSONObject(response);
                        String token = json.getString("token");
                        int userID = Integer.parseInt(json.getString("userID"));
                        //TODO: encrypt credential storage!
                        final SharedPreferences authenticationPref = getActivity().getSharedPreferences("AuthenticationPref", 0);
                        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
                        authenticationEdit.putString("name", name);
                        authenticationEdit.putString("password", password);
                        authenticationEdit.putString("token", token);
                        authenticationEdit.putInt("userID", userID);
                        authenticationEdit.commit();
                        RestClient.setHeader("Authentication-token", token);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    getActivity().findViewById(R.id.login_error).setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
