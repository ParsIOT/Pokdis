package ir.parsiot.pokdis.map;

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
import android.widget.Toast;


import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.parsiot.pokdis.Enums.ScanModeEnum;
import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.Items.ItemValues;
import ir.parsiot.pokdis.Listeners.OnWebViewClickListener;
import ir.parsiot.pokdis.Items.ItemClass;


/**
 * Created by hadi on 3/5/18.
 */

public class WebViewManager {

    private static final String TAG = WebViewManager.class.getSimpleName();
    private WebView webView;
    private Context context;
    private ScanModeEnum scanMode;
    private OnWebViewClickListener onWebViewClickListener;
    private String tagToJS;

    private String loctionOfMarker = MapConsts.initLocation;

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
//        Log.e("Map is loading...","ok");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:addMap(\'%s\',%d, %d)", map.getMapPath(), bounds.get(0), bounds.get(1)));

            }
        }, 2000);
    }

    public void addMarker(final String point) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:addMarker(\"%s\")", point));

            }
        }, 1000);
    }

    public void addItem(final String point, final String itemId, final String itemName, final String itemImgSrc) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:addItem(\"%s\",\"%s\",\"%s\",\"%s\")", point, itemId, itemName, itemImgSrc));

            }
        }, 1000);
    }

    public void clearItemDetails(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:clearItemDetails()"));
            }
        }, 1000);
    }

    public void drawLine(String location1, String location2) {
        final String js_location = String.format("javascript:drawLine(\"%s\",\"%s\")", location1, location2);
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

    public void updateLocation(String location) {
        loctionOfMarker = location;
        final String js_location = String.format("javascript:moveMarker(\'%s\')", location);
        //  webView.loadUrl(js_location);
        if (context != null && webView != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js_location);
                }
            });
        }
    }

    public void updateHeading(String heading){
        final String js_location = String.format("javascript:rotateMarker(\"%s\")", heading);
        //  webView.loadUrl(js_location);
        if (context != null && webView != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js_location);
                }
            });
        }
    }

    public void updateLocationAndHeading(String locationXY, String heading) {
//        final String js_location = String.format("javascript:moveMarker(\'%s\')", locationXY);
        final String js_location = String.format("javascript:moveMarker(\"%s\");rotateMarker(\"%s\")", locationXY, heading);
        //  webView.loadUrl(js_location);
        if (context != null && webView != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(js_location);
                }
            });
        }
    }


    public String getLoctionOfMarker() {
        return loctionOfMarker;
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

    public void destoryWebView() {
        webView.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        webView.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        webView.loadUrl("about:blank");

        webView.onPause();
        webView.removeAllViews();
        webView.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        webView.pauseTimers();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        webView.destroy();

        // Null out the reference so that you don't end up re-using it.
        webView = null;
    }

    public void addPopup(final String point, final String text) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(String.format(Locale.getDefault(), "javascript:makePopup(\"%s\",\"%s\")", point, text));

            }
        }, 1000);
    }

    public String getTagToJS() {
        return tagToJS;
    }

    public void setTagToJS(String tagToJS) {
        this.tagToJS = tagToJS;
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
        public void addItemToCart(String itemId) {
            Log.d("TAG", "addItemToCart: " + itemId);
//            onWebViewClickListener.onWebViewClick(text);
            String txtMessage;
            CartItems cartItems = new CartItems(mContext);
            ItemValues itemValues = new ItemValues();
            ItemClass item = itemValues.get_item(itemId);
            if (cartItems.put_item(item)){
                txtMessage = "این محصول به لیست خرید اضافه شد.";
            }else{
                txtMessage = "این محصول در سبد خرید از قبل وجود داشته است";
            }
            Toast.makeText(mContext,txtMessage,Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void startMap() {
            Intent mIntent = new Intent();
            ComponentName component = new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity");
            mIntent.setComponent(component);
        }


        /////
        // Test functions(Just for testing android and js connections) :
        @JavascriptInterface
        public void sendToAndroid(String text) {
            Log.d("TAG", "sendToAndroid: " + text);
            onWebViewClickListener.onWebViewClick(text);
        }

        @JavascriptInterface
        public void addIdToList(String text) {
            Toast.makeText(context, "+", Toast.LENGTH_SHORT);
            Log.e("tag", text);
        }

        @JavascriptInterface
        public String getFromAndroid() {
            return "This is from android.";
        }

        @JavascriptInterface
        public String getTagFromAndroid() {
            return tagToJS;
        }

        /////
    }


}