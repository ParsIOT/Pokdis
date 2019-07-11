package ir.parsiod.NavigationInTheBuilding;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import ir.parsiod.NavigationInTheBuilding.beacon.BLEdevice;
import ir.parsiod.NavigationInTheBuilding.beacon.BeaconDiscovered;

public class MainActivity extends AppCompatActivity {

    private BeaconDiscovered beaconDiscovered;

    Button bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                12);
                    }

                });
                builder.show();
            }
        }


        beaconDiscovered = new BeaconDiscovered(this);

        bottom = findViewById(R.id.bottom);
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconDiscovered.startMonitoring();
                BLEdevice a =   beaconDiscovered.getDiscoveredDevices().get(0);
               Log.e("beacon", a.getMac());
            }
        });

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
            case 12: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("tagRequest", "coarse location permission granted");
                } else {

                }
                return;
            }
        }
    }

}
