package winzer.ghostrunner_app_final.Services;

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
import winzer.ghostrunner_app_final.Activities.LoginRegister;

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

    public static void login(String name, String password, Context context) {
        //TODO: encrypt credential storage!
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
        authenticationEdit.putString("name", name);
        authenticationEdit.putString("password", password);
        authenticationEdit.commit();
        updateToken(context);
    }

    public static void logout(Context context) {
        getClient().removeAllHeaders();
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
        authenticationEdit.remove("name");
        authenticationEdit.remove("password");
        authenticationEdit.remove("token");
        authenticationEdit.commit();
        Intent intent = new Intent(context, LoginRegister.class); //TODO Fix and change to login
        context.startActivity(intent);
    }

    public static void authenticateToken(final Context context) {
        SharedPreferences authenticationPref = context.getSharedPreferences("AuthenticationPref", 0);
        String token = authenticationPref.getString("token", "");
        setHeader("Authentication-token", token);
        get("/user", new AsyncHttpResponseHandler() { //TODO change to test URL instead of /user
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                updateToken(context);
            }
        });
    }

    public static void updateToken(final Context context) {
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
                        SharedPreferences.Editor authenticationEdit = authenticationPref.edit();
                        authenticationEdit.putString("token", token);
                        authenticationEdit.commit();
                        setHeader("Authentication-token", token);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logout(context);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    logout(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logout(context);
        }
    }

    public static void syncGet(String url, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void syncPost(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.post(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void syncPut(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.put(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void syncDelete(String url, AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.delete(null, getAbsoluteUrl(url), responseHandler);
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
