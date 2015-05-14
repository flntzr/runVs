package winzer.ghostrunner_app_final.Services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.entity.StringEntity;

public class RestClient {

    private static final String BASE_URL = "http://it14-jogging.dhbw-stuttgart.de:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void post(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(null, getAbsoluteUrl(url), responseHandler);
    }

    public static void put(String url, StringEntity json, AsyncHttpResponseHandler responseHandler) {
        client.put(null, getAbsoluteUrl(url), json, "application/json", responseHandler);
    }

    public static void setHeader(String header, String value) {
        client.removeAllHeaders();
        client.addHeader(header, value);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
