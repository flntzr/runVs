package winzer.gh0strunner.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import winzer.gh0strunner.R;
import winzer.gh0strunner.dto.IntInvite;
import winzer.gh0strunner.services.RestClient;
import winzer.gh0strunner.services.RetryAsyncHttpResponseHandler;

/**
 * Created by franschl on 6/1/15.
 */
public class InvitationsFragment extends Fragment {

    // TODO DELETE ENTIRE CLASS
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitations, container, false);
        String invUrl = "/user/" + getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1) + "intinvite";
        RestClient.get(invUrl, new InvitationsResponseHandler(invUrl, getActivity(), this, getActivity().getSharedPreferences("AuthenticationPref", 0).getInt("userID", -1), RetryAsyncHttpResponseHandler.GET_REQUEST));
        return rootView;
    }

    public class InvitationsResponseHandler extends RetryAsyncHttpResponseHandler {
        int userID;
        List<IntInvite> invites;

        public InvitationsResponseHandler(String url, Context context, Fragment fragment, int userID, int requestType) {
            super(url, context, fragment, requestType);
            this.userID = userID;
        }

        @Override
        public void onSuccessful(int statusCode, Header[] headers, byte[] responseBody) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<IntInvite>>() {}.getType();
            invites = gson.fromJson(new String(responseBody), listType);

            TableLayout tl = (TableLayout) ((Activity) context).findViewById(R.id.invitations_table);
            for (IntInvite invite : invites) {
                TableRow row = new TableRow(context);
                TextView nameView = new TextView(context);
                nameView.setText(invite.getHost().getNick());
                Button button = new Button(context);
                button.setText("Accept");
                row.addView(nameView);
                row.addView(button);
                tl.addView(row);
            }

        }

        @Override
        public void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            System.out.println("Noi");
        }
    }
}
