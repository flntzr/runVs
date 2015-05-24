package winzer.ghostrunner_app_final.Services;

import android.content.Context;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

public abstract class RetryAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    public static final int GET_REQUEST = 1;
    public static final int POST_REQUEST = 2;
    public static final int PUT_REQUEST = 3;
    public static final int DELETE_REQUEST = 4;
    String url;
    StringEntity json;
    Context context;
    int requestType; // 1 get 2 post 3 put 4 delete

    public RetryAsyncHttpResponseHandler(String url, StringEntity json, Context context, int requestType) {
        this.url = url;
        this.json = json;
        this.context = context;
        this.requestType = requestType;
    }

    public RetryAsyncHttpResponseHandler(String url, Context context, int requestType) {
        this.url = url;
        this.context = context;
        this.requestType = requestType;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onSuccessful(statusCode, headers, responseBody);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        RestClient.authenticateToken(context);
        switch (requestType) {
            case GET_REQUEST:
                RestClient.syncGet(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onSuccessful(statusCode, headers, responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onUnsuccessful(statusCode, headers, responseBody, error);
                    }
                });
                break;
            case POST_REQUEST:
                RestClient.syncPost(url, json, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onSuccessful(statusCode, headers, responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onUnsuccessful(statusCode, headers, responseBody, error);
                    }
                });
                break;
            case PUT_REQUEST:
                RestClient.syncPut(url, json, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onSuccessful(statusCode, headers, responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onUnsuccessful(statusCode, headers, responseBody, error);
                    }
                });
                break;
            case DELETE_REQUEST:
                RestClient.syncDelete(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onSuccessful(statusCode, headers, responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onUnsuccessful(statusCode, headers, responseBody, error);
                    }
                });
                break;
        }
    }

    public abstract void onSuccessful(int statusCode, Header[] headers, byte[] responseBody);

    public abstract void onUnsuccessful(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
}
