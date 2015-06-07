package winzer.gh0strunner.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import winzer.gh0strunner.MainActivity;
import winzer.gh0strunner.login.LoginActivity;

public class RestClient {

    private static final String BASE_URL = "http://it14-jogging.dhbw-stuttgart.de:8080";

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private static AsyncHttpClient syncHttpClient = new SyncHttpClient();

    public static AsyncHttpClient getClient() {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null) {
            return syncHttpClient;
        } else {
            return asyncHttpClient;
        }
    }

    public static void moveToLoginPage(Context context) {
        getClient().removeAllHeaders();
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
        authenticationEdit.remove("token");
        authenticationEdit.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void logout(Context context) {
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
        authenticationEdit.remove("name");
        authenticationEdit.remove("password");
        authenticationEdit.remove("userID");
        authenticationEdit.remove("token");
        authenticationEdit.commit();
        moveToLoginPage(context);
    }

    public static void authenticateToken(final Context context, final AuthenticateTokenCallback atc) {
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        String token = authenticationPref.getString("token", "");
        setHeader("Authentication-token", token);
        get("/user/" + authenticationPref.getInt("userID", -1), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                atc.tokenAuthenticated();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                updateToken(context, atc);
            }
        });
    }

    public static void updateToken(final Context context, final AuthenticateTokenCallback atc) {
        final SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("name", authenticationPref.getString("name", ""));
            jsonParams.put("password", authenticationPref.getString("password", ""));
            StringEntity entity = new StringEntity(jsonParams.toString());
            post("/login", entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONObject json = new JSONObject(response);
                        String token = json.getString("token");
                        int userID = json.getInt("userID");
                        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
                        authenticationEdit.putString("token", token);
                        authenticationEdit.putInt("userID", userID);
                        authenticationEdit.commit();
                        setHeader("Authentication-token", token);
                        atc.tokenAuthenticated();
                    } catch (Exception e) {
                        e.printStackTrace();
                        moveToLoginPage(context);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    moveToLoginPage(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            moveToLoginPage(context);
        }
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        getClient().get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void post(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        getClient().post(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void put(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        getClient().put(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        getClient().delete(null, getAbsoluteUrl(url), responseHandler);
    }

    public static void setHeader(String header, String value) {
        getClient().removeAllHeaders();
        getClient().addHeader(header, value);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
