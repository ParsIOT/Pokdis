package ir.parsiod.NavigationInTheBuilding.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by seyedalian on 11/6/2019.
 */

public class BeaconDiscovered implements BeaconConsumer {
    private  static  final String ALTBEACON_LAYOUT="m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static  final  long PERIOD_TIME_BETWEEN = 300l;


    private ArrayList<BLEdevice> discoveredDevices;
    private BeaconManager beaconManager = null;

    public ArrayList<BLEdevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

    private Region beaconRegion;
    Context context;

    public BeaconDiscovered(final Context context) {
        this.context =context;
        discoveredDevices = new ArrayList<>();
        //setting of beacons Manager
        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON_LAYOUT));
        beaconManager.bind(this);

        //set between scan period
        beaconManager.setForegroundBetweenScanPeriod(PERIOD_TIME_BETWEEN);


    }



    public void unbind(){
        beaconManager.unbind(this);
    }


    public void startRangingBeaconsInRegion(){
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));


        } catch (RemoteException e) {   }
    }


    public void startMonitoring(){

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if (beacons.size() > 0) {

                    // Log.i("beacon", "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());


                    //covert collection<beacon> to list<beacon> for access to beacons
                    List<Beacon> list = new ArrayList<Beacon>(beacons);

                    if(list.size()>0){

                        for(int i=0;i<list.size();i++){
                            Beacon beacon = list.get(i);
                            boolean flag =false;

                            for(int j=0;j<discoveredDevices.size();j++){
                                Integer a = beacon.getId3().toInt();
                                Integer b =Integer.valueOf(discoveredDevices.get(j).getMinor());

                                if(a.equals(b)
                                ){
                                    discoveredDevices.get(j).InsertRssi(beacon.getRssi());

                                    Log.i("beacon","minor: "+discoveredDevices.get(j).getMac()+" add rssi="+ beacon.getRssi());
                                    flag =true;



                                }
                            }

                            if(!flag){


                                Log.i("beacon","add minor="+ beacon.getId3().toString());
                                BLEdevice blEdevice = new BLEdevice();
                                blEdevice.setUUID(beacon.getId1().toString());
                                blEdevice.setMajor(beacon.getId2().toString());
                                blEdevice.setMinor(beacon.getId3().toString());
                                blEdevice.InsertRssi(beacon.getRssi());
                                blEdevice.setMac(beacon.getBluetoothAddress());
                                Log.i("beacon","mac"+blEdevice.getMac());
                                discoveredDevices.add(blEdevice);



                            }



                        }

                        //add zero to devices that no discovering in this scan
                        for(int j=0;j<discoveredDevices.size();j++){
                            boolean flagOfExit =false;

                            for(int i=0;i<list.size();i++){
                                Beacon beacon = list.get(i);
                                if(beacon.getId3().equals(discoveredDevices.get(j).getMinor())){
                                    flagOfExit=true;
                                }
                            }
                            if(flagOfExit){
                                discoveredDevices.get(j).InsertRssi(0);
                            }
                        }
                        //remove devices that all their rssis = 0
                        for(int i=0;i<discoveredDevices.size();i++){
                            if(discoveredDevices.get(i).canThatCanRemoveThisDivice()){
                                discoveredDevices.remove(i);
                            }
                        }

                        sortDiscoveredDevices();





                    }
                }
            }

        };


        try {

            //set uuid of beacons and their major for better discovering
            beaconRegion = new Region("beacon", Identifier.parse("23a01af0-232a-4518-9c0e-323fb773f5ef"),null,null);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
            beaconManager.addRangeNotifier(rangeNotifier);

        } catch (RemoteException e) {    }



    }

    private void sortDiscoveredDevices() {

        for(int i=0;i<discoveredDevices.size();i++){
            for(int j=i;j<discoveredDevices.size();j++){
                if(discoveredDevices.get(j).getRssiAvg()>discoveredDevices.get(i).getRssiAvg()){
                    BLEdevice a = discoveredDevices.get(i);
                    discoveredDevices.set(i,discoveredDevices.get(j));
                    discoveredDevices.set(j,a);
                }
            }

        }
    }

    //stop discovering Beacons In Region
    public void stopMonitoring(){


        try {

            beaconRegion = new Region("beacon",Identifier.parse("23a01af0-232a-4518-9c0e-323fb773f5ef"),Identifier.parse("1"),null);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);

            beaconManager.stopRangingBeaconsInRegion(beaconRegion);

        } catch (RemoteException e) {    }



    }


    @Override
    public void onBeaconServiceConnect() {

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));

            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));

        } catch (RemoteException e) {   }
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return true;
    }


}
