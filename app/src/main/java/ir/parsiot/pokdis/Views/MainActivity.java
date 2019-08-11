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


import com.navisens.motiondnaapi.MotionDnaApplication;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.parsiot.pokdis.Constants.Constants;
import ir.parsiot.pokdis.Enums.ScanModeEnum;
import ir.parsiot.pokdis.Items.ItemClass;
import ir.parsiot.pokdis.Listeners.OnWebViewClickListener;
import ir.parsiot.pokdis.Localization.Localization;
import ir.parsiot.pokdis.R;
import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.MapDetail;
import ir.parsiot.pokdis.map.GraphBuilder;
import ir.parsiot.pokdis.map.Objects.Graph;
import ir.parsiot.pokdis.map.Objects.Point;
import ir.parsiot.pokdis.map.WallGraph.RectObstacle;
import ir.parsiot.pokdis.map.WebViewManager;

import static ir.parsiot.pokdis.Constants.Constants.MAX_PROXIMITY_TO_ROUTE_THRESHOLD;
import static ir.parsiot.pokdis.Localization.MotionDna.NaviSettings.MAX_PROXIMITY_TO_ROUTE_THRESHOLD_IMU;

public class MainActivity extends AppCompatActivity implements WebViewManager.LocationUpdateCallback {

    boolean isMainPage = true;
    ArrayList<ArrayList<Point>> routePath = new ArrayList<ArrayList<Point>>();

    WebView webView;
    WebViewManager webViewManager;

    private static final int REQUEST_MDNA_PERMISSIONS = 1;
    Localization localization;

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
        ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions()
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
        if (MotionDnaApplication.checkMotionDnaPermissions(this)) // permissions already requested
        {

            // Starts a foreground service to ensure that the
            // App continues to sample the sensors in background
            localization = new Localization(this, getPackageManager(), getApplicationContext());
            localization.InitAndStart();
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
                localization.clearBeaconDataHistory();
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
        dimensions.add(MapConsts.MAP_WIDTH);
        dimensions.add(MapConsts.MAP_HEIGHT);
        mapDetail.setMapDimensions(dimensions);
        webViewManager.addMap(mapDetail);
        Log.e("map", mapDetail.toString());

    }


    @Override
    protected void onDestroy() {
        webViewManager.destoryWebView();

        localization.onDestory();

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

            // Start the MotionDna Core
//            startMotionDna();
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
            localization.clearBeaconDataHistory();
            routePath.clear();
            this.moveTaskToBack(true);
//            super.onBackPressed();
        }
    }


    // Draw line between current location and a destination point
    void pathToPoint(final String dstPoint) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String srcPoint = webViewManager.getLoctionOfMarker();
                GraphBuilder location = new GraphBuilder();
                ArrayList<ArrayList<Point>> path = location.graph.getPath(srcPoint, dstPoint);
                routePath = path;
                localization.updateRoutePath(routePath);

                for (ArrayList<Point> line : path) {
                    webViewManager.drawLine(line.get(0).toString(), line.get(1).toString());
                }
            }
        }, 1000);
    }


    @Override
    public void onLocationUpdate(String locationXY, String heading) {
        webViewManager.updateLocationAndHeading(locationXY, heading);
    }

    @Override
    public void onLocationUpdate(String locationXY) {
        webViewManager.updateLocation(locationXY);
    }

    public void drawConstLine(final String dot1, final String dot2) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                webViewManager.drawConstLine(dot1, dot2);
            }
        }, 3000);
    }

    @Override
    public void onHeadingUpdate(String heading) {
        webViewManager.updateHeading(heading);
    }

    @Override
    public void onUpdateParticles(ArrayList<ArrayList<String>> particles) {
        webViewManager.updateParticles(particles);
    }

}
