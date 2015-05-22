package winzer.ghostrunner_app_final.Services;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class UsageExample extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void loadRest(View view) throws JSONException, UnsupportedEncodingException {

        // Create Parameter Rest Object (as StringEntity)
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("name", "growland");
        jsonParams.put("password", "sehrsicher");
        StringEntity entity = new StringEntity(jsonParams.toString());

        // Perform post operation on "login" url
        RestClient.post("login", entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v("onsuccess", "StatusCode: " + statusCode);
                try {

                    //create json object from response
                    String response = new String(responseBody, "UTF-8");
                    JSONObject json = new JSONObject(response);

                    //use json
                    String token = null;
                    token = json.getString("token");
                    System.out.println(token);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                Log.v("MainActivity:", "StatusCode: " + statusCode);
            }

        });
    }

    public void loadRest2(View view) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("email", "dro@grow.de");
        StringEntity entity = new StringEntity(jsonParams.toString());
        RestClient.setHeader("Authentication-token", "ZHJvbGFuZCEhITE0MzA3NjYxNTM4MDchISHvv70OWFsoJ2Tvv73vv71877+9QETvv71MUg==");
        RestClient.put("user/42", entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v("onsuccess", "StatusCode: " + statusCode);
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray json = new JSONArray(response);
                    String token = null;
                    token = json.getJSONObject(0).getString("nick");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                Log.v("MainActivity:", "StatusCode: " + statusCode);
            }

        });
    }

}