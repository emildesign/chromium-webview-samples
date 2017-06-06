package jsinterfacesample.android.chrome.google.com.jsinterface_example;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by elad.r on 13/04/2017.
 */
public class WebViewInterface2 {

    interface OnWebViewLoadedListener {
        public void onWebViewLoaded(WebView wv);
    }
    boolean loadingFinished = true;
    boolean redirect = false;
    private OnWebViewLoadedListener mListener;

    public WebViewInterface2(OnWebViewLoadedListener mListener) {
        this.mListener = mListener;
    }

    @UiThread
    public void onWebViewDetected(final WebView wv) {
        loadingFinished = true;
        redirect = false;

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                //wv.setAlpha(0.1f);
                loadingFinished = false;
                view.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                //ABLogger.d("onWebViewDetected() onPageStarted");
                updateTag(view, false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(!redirect){
                    loadingFinished = true;
                }

                if(loadingFinished && !redirect){
                    //ABLogger.d("onWebViewDetected() onPageFinished");
                    updateTag(view, true);
                    mListener.onWebViewLoaded(view);
                } else{
                    redirect = false;
                }
            }

            private void updateTag(View v, boolean isFinished) {
               // v.setTag(R.id.abbi_WebView_wasLoadingFinished, isFinished);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                updateTag(view, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //ABLogger.e("Failed to load site with url '%s'. error: %s", view.getUrl(), error.getDescription());
                } else {
                    //ABLogger.e("Failed to load site with url '%s'. error: %s", view.getUrl(), error.toString());
                }
                mListener.onWebViewLoaded(view);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                updateTag(view, true);
                //ABLogger.e("Failed to load site with url '%s'. error: %s", view.getUrl(), description);
                mListener.onWebViewLoaded(view);
            }
        });
    }
}
