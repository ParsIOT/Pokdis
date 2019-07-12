package ir.parsiod.NavigationInTheBuilding;

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


import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.webkit.WebSettings;
import android.webkit.WebView;

import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;

import ir.parsiod.NavigationInTheBuilding.Constants.Constants;
import ir.parsiod.NavigationInTheBuilding.Enums.ScanModeEnum;
import ir.parsiod.NavigationInTheBuilding.Listeners.OnWebViewClickListener;
import ir.parsiod.NavigationInTheBuilding.beacon.BeaconDiscovered;
import ir.parsiod.NavigationInTheBuilding.beacon.LocationOfBeacon;
import ir.parsiod.NavigationInTheBuilding.map.MapDetail;
import ir.parsiod.NavigationInTheBuilding.map.WebViewManager;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
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


        beaconDiscovered = new BeaconDiscovered(this);




        beaconDiscovered.startMonitoring();
        initViews();



    }

    @Override
    protected void onResume() {

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String nearMac = beaconDiscovered.getDiscoveredDevices().get(0).getMac();
                LocationOfBeacon locationOfBeacon = new LocationOfBeacon();
                Double [] location = locationOfBeacon.beaconCoordinates.get(nearMac);
                String loc = location[0]+","+location[1];
                webViewManager.updateLocation(loc);
            }
        },3000,300);



    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        //init webView
        webViewManager = new WebViewManager(webView);
        webViewManager.setupManager(this, ScanModeEnum.track, new OnWebViewClickListener() {
            @Override
            public void onWebViewClick(String coordination) {

            }
        });
        MapDetail mapDetail = new MapDetail();
        mapDetail.setMapName("map");
        mapDetail.setMapPath("img/test-map.png");
        List<Integer> dimensions = new ArrayList<Integer>();
        dimensions.add(3600);
        dimensions.add(3400);
        mapDetail.setMapDimensions(dimensions);
        webViewManager.addMap(mapDetail);

        Log.e("map",mapDetail.toString());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconDiscovered.unbind();
    }







    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
