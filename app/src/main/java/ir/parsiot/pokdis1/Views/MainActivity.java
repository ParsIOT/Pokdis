package ir.parsiot.pokdis1.Views;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;

import ir.parsiot.pokdis1.Constants.Constants;
import ir.parsiot.pokdis1.Enums.ScanModeEnum;
import ir.parsiot.pokdis1.Listeners.OnWebViewClickListener;
import ir.parsiot.pokdis1.R;
import ir.parsiot.pokdis1.beacon.BeaconDiscovered;
import ir.parsiot.pokdis1.map.ConstOfMap;
import ir.parsiot.pokdis1.map.MapDetail;
import ir.parsiot.pokdis1.map.GraphBuilder;
import ir.parsiot.pokdis1.map.Objects.Edge;
import ir.parsiot.pokdis1.map.WebViewManager;

/**
 * Created by seyedalian on 11/6/2019.
 */

public class MainActivity extends AppCompatActivity {

    private BeaconDiscovered beaconDiscovered;


    WebView webView;
    WebViewManager webViewManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Permission
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
            }else {
                // Enable location services
                LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
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
                                Constants. REQUEST_CODE_READ_EXTERNAL_STORAGE);

                    }

                });
                builder.show();
            }






        }


       // beaconDiscovered = new BeaconDiscovered(this);

        initViews();

        //get location from albeacon
        try{
            beaconDiscovered.startMonitoring();
            updateLocation();
        }catch (RuntimeException e){

        }




    //get information from SalesListActivity
        final String locationMarker = getIntent().getStringExtra("locationMarker");
        String itemName = getIntent().getStringExtra("itemName");
        String itemID = getIntent().getStringExtra("itemID");

        //if from SalesListActivity
        if(locationMarker!=null && itemName!=null){
            pathToPoint(locationMarker);
            webViewManager.addMarker(locationMarker,"محصول"+itemName +"<br>"+"اضافه کردن به سبد خرید");

            webViewManager.setTagToJS(itemID);


        }


    }




    @Override
    protected void onResume() {
        //check BLUETOOTH is enable on any start program
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Device does not support Bluetooth",Toast.LENGTH_SHORT);
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_CODE_ENABLE_BLUETOOTH);
            }
        }


        super.onResume();
    }

    private void updateLocation() {
        // for move marker to near beacon

        try {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    String location = beaconDiscovered.getNearLoacationToString();
                    if(location!=null){

                        webViewManager.updateLocation(location);
                    }
                }
            },6000,Constants.PERIOD_OF_GET_TOP_BEACON);


        }catch (RuntimeException e){

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
        MapDetail mapDetail = new MapDetail();
        mapDetail.setMapName("map");
        mapDetail.setMapPath("map.png");
        List<Integer> dimensions = new ArrayList<Integer>();
        dimensions.add(1206);
        dimensions.add(1151);
        mapDetail.setMapDimensions(dimensions);
        webViewManager.addMap(mapDetail);

        Log.e("map",mapDetail.toString());


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //for unbind beaconDiscovered
       if(beaconDiscovered!=null){
           try {
               beaconDiscovered.unbind();
           }catch (RuntimeException e){
               Log.e("error",e.toString());
           }
       }
    }


    // a function for draw line between marker and point
    //note: location of marker is  in webViewManager
    //read TODO
    void pathToPoint (final String point){


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {






                GraphBuilder location =new GraphBuilder();
                ConstOfMap constOfMap = new ConstOfMap();


                try {







                    Edge nearEdge1=  location.graph.findNearEdge(point);
                    Edge nearEdge2=  location.graph.findNearEdge(webViewManager.getLoctionOfMarker());


                    String point1P = nearEdge1.pointOnLineImage(point);
                    String point2P = nearEdge2.pointOnLineImage(webViewManager.getLoctionOfMarker());


                    String vertex1 = nearEdge1.nearVertex(point1P);
                    String vertex2 = nearEdge2.nearVertex(point2P);

                    String strPath = location.graph.getPathBetween(vertex1,vertex2);
                    strPath.toString();
                    String [] path = strPath.split(",");

                    webViewManager.drawLine(point,constOfMap.vertexOfGraph.get(vertex1));
                     webViewManager.drawLine(webViewManager.getLoctionOfMarker(),constOfMap.vertexOfGraph.get(vertex2));
                     //TODO AFTER YOU GET NEAR POINT ON LINE ON COMMENT THIS COMMENT :)
                    // webViewManager.drawLine(point1P,constOfMap.vertexOfGraph.get(vertex1));
                    // webViewManager.drawLine(point2P,constOfMap.vertexOfGraph.get(vertex2));
                    String lastVertex = path[0];
                    for (int i=1;i<path.length;i++){
                        webViewManager.drawLine(constOfMap.vertexOfGraph.get(lastVertex)
                                ,constOfMap.vertexOfGraph.get(path[i]));
                        lastVertex = path[i];
                    }
                    //webViewManager.drawLine(point,constOfMap.vertexOfGraph.get(path[path.length]));
                    String.valueOf(path.length).toString();
                    if(path.length!=0){
                        webViewManager.drawLine(constOfMap.vertexOfGraph.get(vertex2)
                                ,constOfMap.vertexOfGraph.get(lastVertex));
                        webViewManager.drawLine(constOfMap.vertexOfGraph.get(vertex1)
                                ,constOfMap.vertexOfGraph.get(path[0]));
                    }
                    if(path[0]==""){
                        webViewManager.drawLine(constOfMap.vertexOfGraph.get(vertex1)
                                ,constOfMap.vertexOfGraph.get(vertex2));
                    }

                }catch (RuntimeException e){
                    Log.e("error",e.toString());
                }
            }
        },1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem search = menu.add("search").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {



                startActivity(new Intent(MainActivity.this,SalesListActivity.class));


                return false;
            }
        });
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        search.setIcon(R.drawable.serch);
        MenuItem cart = menu.add("Cart").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {



                startActivity(new Intent(MainActivity.this,CartActivity.class));


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
                    LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {

                }
                return;


            }
            case Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("tagRequest", "coarse location permission granted");
                } else {

                }
                return;


            }
        }
    }

}
