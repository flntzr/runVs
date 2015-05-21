package winzer.ghostrunner_app_final.Services;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public abstract class AsyncHttpResponseHandlerHack extends BinaryHttpResponseHandler {

    private String[] mAllowedContentTypes = new String[] {
            RequestParams.APPLICATION_OCTET_STREAM,
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/zip*"
    };

}
