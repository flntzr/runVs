package winzer.gh0strunner.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.R;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        Button button = (Button) rootView.findViewById(R.id.button_register_submit);
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
        final String username = ((EditText) getActivity().findViewById(R.id.field_username_register)).getText().toString();
        String email = ((EditText) getActivity().findViewById(R.id.field_email_register)).getText().toString();
        final String password = ((EditText) getActivity().findViewById(R.id.field_password_register)).getText().toString();
        String password2 = ((EditText) getActivity().findViewById(R.id.field_password2_register)).getText().toString();
        if(!password.equals(password2)) {
            TextView error = (TextView) getActivity().findViewById(R.id.login_error_register);
            error.setText(R.string.error_password_matching);
            error.setVisibility(View.VISIBLE);
        } else {
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("nick", username);
                jsonParams.put("password", password);
                jsonParams.put("email", email);
                StringEntity entity = new StringEntity(jsonParams.toString());
                RestClient.post("/user", entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        login(username, password);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        TextView errorView = (TextView) getActivity().findViewById(R.id.login_error_register);
                        if (statusCode == 409) {
                            errorView.setText(R.string.error_creation_conflict);
                        } else if (statusCode == 400) {
                            try {
                                String response = new String(responseBody, "UTF-8");
                                JSONObject json = new JSONObject(response);
                                JSONArray messages = json.getJSONArray("messages");
                                String errorMessage = "";
                                for(int i = 0; i < messages.length(); i++) {
                                    errorMessage += messages.getString(i) + System.lineSeparator();
                                }
                                errorView.setText(errorMessage);
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                                errorView.setText(R.string.error_creation_failed);
                            }
                        } else {
                            errorView.setText(R.string.error_creation_failed);
                        }

                        errorView.setVisibility(View.VISIBLE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
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
                    getActivity().findViewById(R.id.login_error_register).setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
