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


import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
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
import ir.parsiod.NavigationInTheBuilding.map.ObjectLocation;
import ir.parsiod.NavigationInTheBuilding.map.Objects.Edge;
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

        initViews();

        try{
            beaconDiscovered.startMonitoring();
            updateLocation();
        }catch (RuntimeException e){

        }

      /* new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               webViewManager.drawLine("-540,988","-1236,1044");
               webViewManager.drawLine("-528.1111068725586,-167.8888931274414","815.8888931274414,-183.8888931274414");
               webViewManager.drawLine("-512.0555534362793,-135.9444465637207","-532.0555534362793,936.0555534362793");

           }
       },5000);*/
  /*    new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               String loc ="1376.777687072754,369.8886947631836";
               webViewManager.updateLocation(loc);

               ObjectLocation objectLocation = new ObjectLocation();
             Edge nearEdge= objectLocation.getObjects().get(0).getGraph().findNearEdge(loc);
             float []f =nearEdge.pointOnLineImage(loc);
             webViewManager.drawLine(f[0]+","+f[1],loc);

             String a =f[0]+","+f[1]+"|"+loc;
             a.toString();



           }
       },5000);*/
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
