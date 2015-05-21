package winzer.ghostrunner_app_final.Services;

import android.os.Looper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import org.apache.http.entity.StringEntity;

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

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        getClient().get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void post(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        getClient().post(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        getClient().delete(null, getAbsoluteUrl(url), responseHandler);
    }

    public static void put(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        getClient().put(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void setHeader(String header, String value) {
        //getClient().removeAllHeaders();
        getClient().addHeader(header, value);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
