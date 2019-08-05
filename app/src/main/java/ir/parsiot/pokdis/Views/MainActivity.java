package ir.parsiot.pokdis.Views;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;


import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.Toast;


import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ir.parsiot.pokdis.Constants.Constants;
import ir.parsiot.pokdis.Enums.ScanModeEnum;
import ir.parsiot.pokdis.Items.ItemClass;
import ir.parsiot.pokdis.Listeners.OnWebViewClickListener;
import ir.parsiot.pokdis.MotionDna.MotionDnaForegroundService;
import ir.parsiot.pokdis.MotionDna.NaviSettings;
import ir.parsiot.pokdis.R;
import ir.parsiot.pokdis.beacon.BeaconDiscovered;
import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.MapDetail;
import ir.parsiot.pokdis.map.GraphBuilder;
import ir.parsiot.pokdis.map.Objects.Graph;
import ir.parsiot.pokdis.map.Objects.Point;
import ir.parsiot.pokdis.map.WebViewManager;

import static android.os.SystemClock.elapsedRealtime;
import static ir.parsiot.pokdis.Constants.Constants.MAX_PROXIMITY_TO_ROUTE_THRESHOLD;
import static ir.parsiot.pokdis.MotionDna.Utils.Convert2zeroto360;
import static ir.parsiot.pokdis.MotionDna.Utils.DegreeDiff;

public class MainActivity extends AppCompatActivity implements MotionDnaInterface{

    private BeaconDiscovered beaconDiscovered;

    boolean isMainPage = true;
    ArrayList<ArrayList<Point>> RoutePath = new ArrayList<ArrayList<Point>>();
    int farFromRouteCnt = 0;

    WebView webView;
    WebViewManager webViewManager;

    // MotionDna:
    MotionDnaApplication motionDnaApplication;
    Hashtable<String, MotionDna> networkUsers = new Hashtable<String, MotionDna>();
    Hashtable<String, Double> networkUsersTimestamps = new Hashtable<String, Double>();
    private static final int REQUEST_MDNA_PERMISSIONS=1;
    Intent motionDnaServiceIntent;
    // Custom MotionDna :
    NaviSettings naviSettings = new NaviSettings();

    public double x;
    public double y;
    public double h;

    public double deltaH;
    ArrayList<Double> deltaHs = new ArrayList<Double>();

    double LastCornerX = MapConsts.getInitLocationFloat().get(1), LastCornerY = MapConsts.getInitLocationFloat().get(0);
    double LastCornerCandidateX = MapConsts.getInitLocationFloat().get(1), LastCornerCandidateY = MapConsts.getInitLocationFloat().get(0);

    public double localOffsetX = 0;
    public double localOffsetY = 0;
    public double localOffsetH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hawk.init(getApplicationContext()).build();
        isMainPage = true;
//        Log.e("TAG", "Main activity is created");
        setContentView(R.layout.activity_main);
        try {
            getSupportActionBar().setTitle("");
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }

        initBottomBar(this, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


        //Permission
        // MotionDna:
        ActivityCompat.requestPermissions(this,MotionDnaApplication.needsRequestingPermissions()
                , REQUEST_MDNA_PERMISSIONS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  ACCESS_COARSE_LOCATION Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                Constants.REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    }

                });
                builder.show();
            } else {
                // Enable location services
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Enable location services");
                    alertDialog.setMessage("Please enable location services");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }

            }
            //get READ STORAGE Permission for altbeacon
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs READ STORAGE Permission");
                builder.setMessage("Please grant READ STORAGE access");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE);

                    }

                });
                builder.show();
            }


//            List<ItemClass> items = new ArrayList<ItemClass>();
//            ItemValues initItems = new ItemValues();
//            items.add(initItems.get_item("1"));
//            Hawk.put("items", items);

        }
        List<ItemClass> value = Hawk.get("items");


        // beaconDiscovered = new BeaconDiscovered(this);

        initViews();

        // Get location from beacon manager
        try {
            beaconDiscovered = new BeaconDiscovered(this);
            //Todo: enable it when use beacon
//            beaconDiscovered.startMonitoring();
//            updateLocation();
        } catch (RuntimeException e) {
            Log.e("Error:", e.getMessage());
        }


        //get information from SalesListActivity
        final String locationMarker = getIntent().getStringExtra("locationMarker");
        String itemName = getIntent().getStringExtra("itemName");
        String itemImgSrc = getIntent().getStringExtra("itemImgSrc");
        String itemId = getIntent().getStringExtra("itemId");

        //if from SalesListActivity
        if (locationMarker != null && itemName != null) {
            isMainPage = false;
            webViewManager.addItem(locationMarker, itemId, itemName, "../" + itemImgSrc); // Todo: Sometimes
//            webViewManager.setTagToJS(itemId);
//            webViewManager.addMarker(locationMarker);
            pathToPoint(locationMarker);
            webViewManager.setTagToJS(itemId);
        }
//        webViewManager.updateLocation(MapConsts.initLocation);


        // MotionDna:
        // MotionDna:
        if (MotionDnaApplication.checkMotionDnaPermissions(this)) // permissions already requested
        {

            // Starts a foreground service to ensure that the
            // App continues to sample the sensors in background
            motionDnaServiceIntent = new Intent(getAppContext(), MotionDnaForegroundService.class);
            getAppContext().startService(motionDnaServiceIntent);

            // Start the MotionDna Core
            startMotionDna();
        }
    }

    protected void initBottomBar(final Context context, int iconNum) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(iconNum); // Map icon
        menuItem.setChecked(true);


        // This is a same function between MainActivity, SalesListActivity and CartActivity
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_map_page:
                        if (context.getClass() != MainActivity.class) {
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;

                    case R.id.ic_search_page:
                        if (context.getClass() != SalesListActivity.class) {
                            Intent intent = new Intent(context, SalesListActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                    case R.id.ic_buy_page:
                        if (context.getClass() != CartActivity.class) {
                            Intent intent = new Intent(context, CartActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        webViewManager.clearItemDetails();

        final String locationMarker = intent.getStringExtra("locationMarker");
        String itemName = intent.getStringExtra("itemName");
        String itemImgSrc = intent.getStringExtra("itemImgSrc");
        String itemId = intent.getStringExtra("itemId");
        try {
            Boolean isMainPageTemp = intent.getExtras().getBoolean("isMainPage");
            Log.d("isMainPageTemp: ", isMainPageTemp.toString());
            if (isMainPageTemp != null) {
                isMainPage = isMainPageTemp;
                farFromRouteCnt = 0;
            }
        } catch (Exception e) {

        }


        if (itemId != null) {
            Log.d("MainActivity", itemId);
        } else {
            Log.d("MainActivity", "There's not itemID");
        }
        //if from SalesListActivity
        if (locationMarker != null && itemName != null) {
            isMainPage = false;
            webViewManager.addItem(locationMarker, itemId, itemName, "../" + itemImgSrc); // Todo: Sometimes
//            webViewManager.setTagToJS(itemId);
//            webViewManager.addMarker(locationMarker);
            pathToPoint(locationMarker);
            webViewManager.setTagToJS(itemId);
        }
    }

    @Override
    protected void onResume() {
        //check BLUETOOTH is enable on any start program
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT);
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_CODE_ENABLE_BLUETOOTH);
            }
        }
        super.onResume();
    }

    private void updateLocation() {
        // update location of marker on the map
        try {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
//                    String location = beaconDiscovered.getNearLocationToString();
                    ArrayList<String> nearBeaconLocations = beaconDiscovered.getAllSortedDiscoveredBeaconLocations();

                    if (nearBeaconLocations != null) {
                        if (nearBeaconLocations.size() > 0) {
//                        Log.e("location:", location);
                            String location;
                            if (RoutePath.size() > 0) {
                                location = findNearLocationByPath(nearBeaconLocations, RoutePath);
                            } else {
                                location = nearBeaconLocations.get(0);
                            }
                            if (location != null) {
                                webViewManager.updateLocation(location);
                            } else {
                                Log.d("MainActivity", "Estimated location is far from route path");
                            }
                        }
                    }
                }
            }, 6000, Constants.PERIOD_OF_GET_TOP_BEACON);
        } catch (RuntimeException e) {

        }

    }

    //initViews
    private void initViews() {
        //init webView
        webView = findViewById(R.id.webView);


        webViewManager = new WebViewManager(webView);
        webViewManager.setupManager(this, ScanModeEnum.track, new OnWebViewClickListener() {
            @Override
            public void onWebViewClick(String coordination) {

            }
        });


        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        MapDetail mapDetail = new MapDetail();
        mapDetail.setMapName("map");
//        mapDetail.setMapPath("map.png");
        mapDetail.setMapPath("map1.png");
        List<Integer> dimensions = new ArrayList<Integer>();
//        dimensions.add(1206);
//        dimensions.add(1151);
        dimensions.add(909);
        dimensions.add(769);
        mapDetail.setMapDimensions(dimensions);
        webViewManager.addMap(mapDetail);
        Log.e("map", mapDetail.toString());

    }


    @Override
    protected void onDestroy() {

        //for unbind beaconDiscovered
        if (beaconDiscovered != null) {
            try {
                beaconDiscovered.unbind();
            } catch (RuntimeException e) {
                Log.e("error", e.toString());
            }
        }
        webViewManager.destoryWebView();

        // MotionDna:
        // Shuts downs the MotionDna Core
        motionDnaApplication.stop();

        // Handle destruction of the foreground service if
        // it is enabled
        if (motionDnaServiceIntent != null) {
            getAppContext().stopService(motionDnaServiceIntent);
        }
        //



        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem search = menu.add("search").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, SalesListActivity.class));
                return false;
            }
        });
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        search.setIcon(R.drawable.search);
        MenuItem cart = menu.add("Cart").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return false;
            }
        });
        cart.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        cart.setIcon(R.drawable.buy);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                }
                return;
            }
            case Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("tagRequest", "coarse location permission granted");
                } else {
                }
                return;
            }
        }

        // MotionDna:
        if (MotionDnaApplication.checkMotionDnaPermissions(this)) // permissions already requested
        {

            // Starts a foreground service to ensure that the
            // App continues to sample the sensors in background
            motionDnaServiceIntent = new Intent(getAppContext(), MotionDnaForegroundService.class);
            getAppContext().startService(motionDnaServiceIntent);

            // Start the MotionDna Core
            startMotionDna();
        }
    }

    @Override
    public void onBackPressed() {
        if (isMainPage) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("آیا میخواهید از برنامه خارج شوید؟")
                    .setCancelable(false)
                    .setPositiveButton("بله",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                    .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            builder.show();
        } else {
            isMainPage = true;
            farFromRouteCnt = 0;
            RoutePath.clear();
            this.moveTaskToBack(true);
//            super.onBackPressed();
        }
    }

    /////////
    // Map related functions :

    // Todo: We should create other class and place routing related function in it
    public String findNearLocationByPath(ArrayList<String> nearBeaconLocations, ArrayList<ArrayList<Point>> RoutePath) {
        /*
        According to the location of the near beacon and the path between
            source and destination of the route we find best location on the route
         */
        String firstBeaconLocation = nearBeaconLocations.get(0);
        if (Graph.dist2Route(firstBeaconLocation, RoutePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD) {  // estLocation is near the route graph
            farFromRouteCnt = 0;
            return firstBeaconLocation;
        } else {
            farFromRouteCnt++;
            if (farFromRouteCnt < Constants.MIN_COUNT_TO_IGNORE_PATH) {
                if (nearBeaconLocations.size() > 1) {
                    String secondBeaconLocation = nearBeaconLocations.get(1);
                    if (Graph.dist2Route(secondBeaconLocation, RoutePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD) {  // estLocation is near the route graph
                        return secondBeaconLocation;
                    }
                }
            } else {
                return firstBeaconLocation;
            }
        }
        return null; // Don't update location!
    }

    // Draw line between current location and a destination point
    void pathToPoint(final String dstPoint) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String srcPoint = webViewManager.getLoctionOfMarker();
                GraphBuilder location = new GraphBuilder();
                ArrayList<ArrayList<Point>> path = location.graph.getPath(srcPoint, dstPoint);
                RoutePath = path;

                for (ArrayList<Point> line : path) {
                    webViewManager.drawLine(line.get(0).toString(), line.get(1).toString());
                }
            }
        }, 1000);
    }


    // MotionDna:
    public void startMotionDna() {
        String devKey = "mpIR1Zf2euR74NJT7Pc9jl7kTJpI96pGma2BWimBF3kJugDRdWsw76fkU90dMD7O";

        motionDnaApplication = new MotionDnaApplication(this);
//        motionDnaApplication.setLocalHeading(90.0);

        //    This functions starts up the SDK. You must pass in a valid developer's key in order for
        //    the SDK to function. IF the key has expired or there are other errors, you may receive
        //    those errors through the reportError() callback route.

        motionDnaApplication.runMotionDna(devKey);

        //    Use our internal algorithm to automatically compute your location and heading by fusing
        //    inertial estimation with global location information. This is designed for outdoor use and
        //    will not compute a position when indoors. Solving location requires the user to be walking
        //    outdoors. Depending on the quality of the global location, this may only require as little
        //    as 10 meters of walking outdoors.

//        motionDnaApplication.setLocationNavisens();

        //   Set accuracy for GPS positioning, states :HIGH/LOW_ACCURACY/OFF, OFF consumes
        //   the least battery.

//        motionDnaApplication.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);

        //    Manually sets the global latitude, longitude, and heading. This enables receiving a
        //    latitude and longitude instead of cartesian coordinates. Use this if you have other
        //    sources of information (for example, user-defined address), and need readings more
        //    accurate than GPS can provide.
//        motionDnaApplication.setLocationLatitudeLongitudeAndHeadingInDegrees(37.787582, -122.396627, 0);

        //    Set the power consumption mode to trade off accuracy of predictions for power saving.

        motionDnaApplication.setPowerMode(MotionDna.PowerConsumptionMode.PERFORMANCE);

        //    Connect to your own server and specify a room. Any other device connected to the same room
        //    and also under the same developer will receive any udp packets this device sends.

//        motionDnaApplication.startUDP();

        //    Allow our SDK to record data and use it to enhance our estimation system.
        //    Send this file to support@navisens.com if you have any issues with the estimation
        //    that you would like to have us analyze.

        motionDnaApplication.setBinaryFileLoggingEnabled(true);

        //    Tell our SDK how often to provide estimation results. Note that there is a limit on how
        //    fast our SDK can provide results, but usually setting a slower update rate improves results.
        //    Setting the rate to 0ms will output estimation results at our maximum rate.

        motionDnaApplication.setCallbackUpdateRateInMs(0);

        //    When setLocationNavisens is enabled and setBackpropagationEnabled is called, once Navisens
        //    has initialized you will not only get the current position, but also a set of latitude
        //    longitude coordinates which lead back to the start position (where the SDK/App was started).
        //    This is useful to determine which building and even where inside a building the
        //    person started, or where the person exited a vehicle (e.g. the vehicle parking spot or the
        //    location of a drop-off).
//        motionDnaApplication.setBackpropagationEnabled(true);

        //    If the user wants to see everything that happened before Navisens found an initial
        //    position, he can adjust the amount of the trajectory to see before the initial
        //    position was set automatically.
        motionDnaApplication.setBackpropagationBufferSize(2000);
        motionDnaApplication.setLocalHeadingOffsetInDegrees(MapConsts.initHeading);
        //    Enables AR mode. AR mode publishes orientation quaternion at a higher rate.
//        motionDnaApplication.setARModeEnabled(true);
    }

    //    This event receives the estimation results using a MotionDna object.
    //    Check out the Getters section to learn how to read data out of this object.

    @Override
    public void receiveNetworkData(MotionDna.NetworkCode networkCode, Map<String, ?> map) {

    }
    public double getMainHeading() {
        return Convert2zeroto360(h + localOffsetH);
    }
    @Override
    public void receiveNetworkData(MotionDna motionDna) {

        networkUsers.put(motionDna.getID(),motionDna);
        double timeSinceBootSeconds = elapsedRealtime() / 1000.0;
        networkUsersTimestamps.put(motionDna.getID(),timeSinceBootSeconds);
        StringBuilder activeNetworkUsersStringBuilder = new StringBuilder();
        List<String> toRemove = new ArrayList();

        activeNetworkUsersStringBuilder.append("Network Shared Devices:\n");
        for (MotionDna user: networkUsers.values()) {
            if (timeSinceBootSeconds - networkUsersTimestamps.get(user.getID()) > 2.0) {
                toRemove.add(user.getID());
            } else {
                activeNetworkUsersStringBuilder.append(user.getDeviceName());
                MotionDna.XYZ location = user.getLocation().localLocation;
                activeNetworkUsersStringBuilder.append(String.format(" (%.2f, %.2f, %.2f)",location.x, location.y, location.z));
                activeNetworkUsersStringBuilder.append("\n");
            }

        }
        for (String key: toRemove) {
            networkUsers.remove(key);
            networkUsersTimestamps.remove(key);
        }

//        networkTextView.setText(activeNetworkUsersStringBuilder.toString());
    }

    @Override
    public void receiveMotionDna(MotionDna motionDna)
    {
        // mupdate
//        Log.e("receiveMotionDna:", "receiveMotionDna");

//        String str = "Navisens MotionDna Location Data:\n";
//        str += "Lat: " + motionDna.getLocation().globalLocation.latitude + " Lon: " + motionDna.getLocation().globalLocation.longitude + "\n";
//        MotionDna.XYZ location = motionDna.getLocation().localLocation;
//        str += String.format(" (%.2f, %.2f, %.2f)\n",location.x*scale, location.y*scale, location.z);

        if (this.webView != null) {
            MotionDna.Location location = motionDna.getLocation();

//            final String str = String.format("%.2f,%.2f",
//                    location.y*scale + MapConsts.getInitLocationFloat().get(0),
//                    location.x*scale + MapConsts.getInitLocationFloat().get(1));


            this.x = location.localLocation.x * naviSettings.scale;
            this.y = location.localLocation.y * naviSettings.scale;
            this.deltaH = DegreeDiff(this.h, Convert2zeroto360(Convert2zeroto360(-1 * location.heading) + 90.0D));

            int deltaHsSize = deltaHs.size();
            if (deltaHsSize <= naviSettings.maxDeltaHsLen2) {
                deltaHs.add(this.deltaH);
            } else {
                deltaHs.remove(0); //delete older h
                deltaHs.add(this.deltaH);
            }

            double cumulativeDelta1 = 0;
            double cumulativeDelta2 = 0;
            for (int i = deltaHsSize - 1; i >= 0; i--) {
                cumulativeDelta2 += deltaHs.get(i);
                if (i <= naviSettings.maxDeltaHsLen2 / naviSettings.maxDeltaHsLen1) {
                    cumulativeDelta1 += deltaHs.get(i);
                }
            }


            if (Math.sqrt(Math.pow(LastCornerX - this.x, 2) + Math.pow(LastCornerY - this.y, 2)) > naviSettings.cornerCandidateDiff) {
                LastCornerCandidateX = this.x;
                LastCornerCandidateY = this.y;
            }
            if (Math.sqrt(Math.pow(LastCornerX - this.x, 2) + Math.pow(LastCornerY - this.y, 2)) > naviSettings.cornerDistanceDiff) {
                LastCornerX = LastCornerCandidateX;
                LastCornerY = LastCornerCandidateY;
            }
            if (cumulativeDelta1 > naviSettings.cornerDetectionTreshold1 && cumulativeDelta2 > naviSettings.cornerDetectionTreshold2) {
                // Find corner
                LastCornerX = this.x;
                LastCornerY = this.y;
                LastCornerCandidateX = this.x;
                LastCornerCandidateY = this.y;
            }
            Log.e("location.heading", String.valueOf(location.heading));
            this.h = Convert2zeroto360(Convert2zeroto360(-1 * location.heading) + 90.0D + MapConsts.initHeading);


//            String script = String.format(Locale.getDefault(), "moveMarker(\"%d,%d\");rotateMarker(\"%d\")", , );
            String locationXY = String.format("%d,%d",(int) (this.y + MapConsts.getInitLocationFloat().get(0)), (int) (this.x + MapConsts.getInitLocationFloat().get(1)));
            String heading = String.format("%d", (int) Convert2zeroto360(-1 * getMainHeading() + 180));
//            (int) Convert2zeroto360(-1 * getMainHeading() + 180)

//            final String str = String.format("%.2f,%.2f",
//                    location.y + MapConsts.getInitLocationFloat().get(0),
//                    location.x + MapConsts.getInitLocationFloat().get(1));

            webViewManager.updateLocationAndHeading(locationXY, heading);
//            webViewManager.updateLocation(str);
        }

//        str += "Hdg: " + motionDna.getLocation().heading +  " \n";
//        str += "motionType: " + motionDna.getMotion().motionType + "\n";
//        textView.setTextColor(Color.BLACK);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Log.e("Location:", str);
            }
        });
    }

    @Override
    public void reportError(MotionDna.ErrorCode errorCode, String s) {
        switch (errorCode) {
            case ERROR_AUTHENTICATION_FAILED:
                System.out.println("Error: authentication failed " + s);
                break;
            case ERROR_SDK_EXPIRED:
                System.out.println("Error: SDK expired " + s);
                break;
            case ERROR_PERMISSIONS:
                System.out.println("Error: permissions not granted " + s);
                break;
            case ERROR_SENSOR_MISSING:
                System.out.println("Error: sensor missing " + s);
                break;
            case ERROR_SENSOR_TIMING:
                System.out.println("Error: sensor timing " + s);
                break;
        }
    }

    @Override
    public PackageManager getPkgManager() {
        return getPackageManager();
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }
}
