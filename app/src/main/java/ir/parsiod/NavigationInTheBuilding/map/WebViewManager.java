package ir.parsiod.NavigationInTheBuilding.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;


import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.parsiod.NavigationInTheBuilding.Enums.ScanModeEnum;
import ir.parsiod.NavigationInTheBuilding.Listeners.OnWebViewClickListener;


/**
 * Created by hadi on 3/5/18.
 */

public class WebViewManager {

    private static final String TAG = WebViewManager.class.getSimpleName();
    private WebView webView;
    private Context context;
    private ScanModeEnum scanMode;
    private OnWebViewClickListener onWebViewClickListener;


    public WebViewManager(WebView webView) {
        this.webView = webView;

        if (webView == null) {
            throw new NullPointerException("webview is null, please initial webview for representing location");
        }

    }

    public void setScanMode(ScanModeEnum scanMode) {
        this.scanMode = scanMode;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void setupManager(Context context, ScanModeEnum scanMode, OnWebViewClickListener listener) {
        this.scanMode = scanMode;
        this.context = context;
        this.onWebViewClickListener = listener;
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/leaflet/map.html");
        webView.addJavascriptInterface(new WebAppInterface(context), "Android");


    }

    public void addMap(final MapDetail map) {
        Log.d(TAG, "addMap: " + map.toString());
        final List<Integer> bounds = map.getMapDimensions();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:addMap(\'%s\',%d, %d)", map.getMapPath(), bounds.get(0), bounds.get(1)));

            }
        }, 1000);
    }

    public void updateLocation(String location) {
        final String js_location = String.format("javascript:moveMarker(\'%s\')", location);
      //  webView.loadUrl(js_location);
        if (context != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js_location);
                }
            });
        }
    }


    public void addLearnLocations(List<String> locations) {
        for (String l : locations) {
            String regex = "(.*),(.*)";
            float x = 0, y = 0;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(l);

            if (matcher.matches()) {
                x = Float.parseFloat(matcher.group(1));
                y = Float.parseFloat(matcher.group(2));
            }

            final String js_location = String.format("javascript:addMarker(%f, %f)", x, y);
            if (context != null) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(js_location);
                    }
                });
            }
        }
    }

    public void setOnClickListener(OnWebViewClickListener onClickListener) {
        this.onWebViewClickListener = onClickListener;
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendToAndroid(String text) {
            Log.d(TAG, "sendToAndroid: " + text);
            onWebViewClickListener.onWebViewClick(text);
        }

        @JavascriptInterface
        public String getFromAndroid() {
            return "This is from android.";
        }

        @JavascriptInterface
        public void startMap() {
            Intent mIntent = new Intent();
            ComponentName component = new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity");
            mIntent.setComponent(component);
//            startActivity(mIntent);
        }
    }


}