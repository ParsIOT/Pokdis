package ir.parsiod.NavigationInTheBuilding.beacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

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

import ir.parsiod.NavigationInTheBuilding.Constants.Constants;

/**
 * Created by seyedalian on 11/6/2019.
 */

public class BeaconDiscovered implements BeaconConsumer {


    private ArrayList<BLEdevice> discoveredDevices;
    private BeaconManager beaconManager = null;

    private BLEdevice proposedBeacon=null;



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
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(Constants.ALTBEACON_LAYOUT));
        beaconManager.bind(this);

        //set between scan period
        beaconManager.setForegroundBetweenScanPeriod(Constants.PERIOD_TIME_BETWEEN_SCAN);


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



                        /*if(proposedBeacon == null){
                            proposedBeacon = discoveredDevices.get(0);
                            Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
                        }else {
                            if(!discoveredDevices.get(0).getMac().equals(proposedBeacon.getMac()))
                                if( discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
                                   proposedBeacon = discoveredDevices.get(1);
                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
                                }else if(!discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
                                    proposedBeacon = discoveredDevices.get(0);
                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
                                }
                        }*/

                        proposedBeacon = discoveredDevices.get(0);




                    }
                }
            }

        };


        try {

            //set uuid of beacons and their major for better discovering
            beaconRegion = new Region("beacon", Identifier.parse(Constants.COMMON_UUID_BEACON),null,null);
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

            beaconRegion = new Region("beacon",Identifier.parse(Constants.COMMON_UUID_BEACON),Identifier.parse(Constants.COMMON_MAJOR_BEACON),null);
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

    public BLEdevice getProposedBeacon() {
        return proposedBeacon;
    }

    public String getNearLoacationToString(){
       try {
          /*   if(discoveredDevices.get(0).getRssiAvg()
                    -discoveredDevices.get(1).getRssiAvg()<4){
                String nearMac1 = discoveredDevices.get(0).getMac();
                LocationOfBeacon locationOfBeacon1 = new LocationOfBeacon();
                Double [] location1 = locationOfBeacon1.beaconCoordinates.get(nearMac1);
                String nearMac2 = discoveredDevices.get(1).getMac();
                LocationOfBeacon locationOfBeacon2 = new LocationOfBeacon();
                Double [] location2 = locationOfBeacon2.beaconCoordinates.get(nearMac2);
                if(location1 != null){
                    String loc = ((location1[0]+location2[0])/2)+","+((location1[1]+location2[1])/2);
                    return loc;
                }


            }else {
                String nearMac = discoveredDevices.get(0).getMac();
                LocationOfBeacon locationOfBeacon = new LocationOfBeacon();
                Double [] location = locationOfBeacon.beaconCoordinates.get(nearMac);
                if(location != null){
                    String loc = location[0]+","+location[1];
                    return loc;
                }
            }*/


           String nearMac = discoveredDevices.get(0).getMac();
           LocationOfBeacon locationOfBeacon = new LocationOfBeacon();
           Double [] location = locationOfBeacon.beaconCoordinates.get(nearMac);
           if(location != null){
               String loc = location[0]+","+location[1];
               return loc;
           }


        }catch (RuntimeException e){

        }


        return null;
    }

}
