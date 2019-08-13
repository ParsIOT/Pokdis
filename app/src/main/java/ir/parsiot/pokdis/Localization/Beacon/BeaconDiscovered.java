package ir.parsiot.pokdis.Localization.Beacon;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ir.parsiot.pokdis.Constants.Constants;

import static ir.parsiot.pokdis.Constants.Constants.MIN_RSS_TO_RELOCATE;

public class BeaconDiscovered implements BeaconConsumer {
    BeaconLocations beaconLocations = new BeaconLocations();
    private BeaconCallback callback;

    private ArrayList<BLEdevice> discoveredDevices;
    private BeaconManager beaconManager = null;

    private BLEdevice proposedBeacon = null;


    public ArrayList<BLEdevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

    private Region beaconRegion;
    Context context;
    RangeNotifier rangeNotifier;

    public BeaconDiscovered(final Context context) {
        this.context = context;
        discoveredDevices = new ArrayList<>();
        //setting of beacons Manager
        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(Constants.ALT_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        if (!beaconManager.isBound(this)) {
            beaconManager.bind(this);
        }

        //set between scan period
        beaconManager.setForegroundBetweenScanPeriod(Constants.PERIOD_TIME_BETWEEN_SCAN);
//        beaconManager.setDebug(true);

    }


    public void unbind() {
        beaconManager.unbind(this);
    }

//    public void startScan() {
//        if (!beaconManager.isBound(this)) {
//
//                    /*Only do this in onDestroy() not onPause()
//                    Can't be bind() & unbind() several times*/
//            beaconManager.bind(this);
//        }
//    }
//
//    public void stopScan() {
//        if (beaconManager.isBound(this)) {
//                    /*Only do this in onDestroy() not onPause()
//                    Can't be bind() & unbind() several times*/
//            beaconManager.unbind(this);
//        }
//    }


    public void startRangingBeaconsInRegion() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));

        } catch (RemoteException e) {
            Log.e("Error", e.getMessage());
        }
    }


//    public void startMonitoring() {
//
//        rangeNotifier = new RangeNotifier() {
//            @Override
//            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//
//                if (beacons.size() > 0) {
////                    Log.i("beacon", "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
//
//                    //covert collection<beacon> to list<beacon> for access to beacons
//                    List<Beacon> list = new ArrayList<Beacon>(beacons);
//
//                    if (list.size() > 0) {
//
//                        for (int i = 0; i < list.size(); i++) {
//                            Beacon beacon = list.get(i);
//                            boolean flag = false;
//
//                            for (int j = 0; j < discoveredDevices.size(); j++) {
//                                Integer a = beacon.getId3().toInt();
//                                Integer b = Integer.valueOf(discoveredDevices.get(j).getMinor());
//
//                                if (a.equals(b)
//                                ) {
//                                    discoveredDevices.get(j).InsertRssi(beacon.getRssi());
//
//                                    Log.i("beacon", "minor: " + discoveredDevices.get(j).getMac() + " add rssi=" + beacon.getRssi());
//                                    flag = true;
//
//
//                                }
//                            }
//
//                            if (!flag) {
//
//
//                                Log.i("beacon", "add minor=" + beacon.getId3().toString());
//                                BLEdevice blEdevice = new BLEdevice();
//                                blEdevice.setUUID(beacon.getId1().toString());
//                                blEdevice.setMajor(beacon.getId2().toString());
//                                blEdevice.setMinor(beacon.getId3().toString());
//                                blEdevice.InsertRssi(beacon.getRssi());
//                                blEdevice.setMac(beacon.getBluetoothAddress());
//                                Log.i("beacon", "mac" + blEdevice.getMac());
//                                discoveredDevices.add(blEdevice);
//
//
//                            }
//
//
//                        }
//
//                        //add zero to devices that no discovering in this scan
//                        for (int j = 0; j < discoveredDevices.size(); j++) {
//                            boolean flagOfExit = false;
//
//                            for (int i = 0; i < list.size(); i++) {
//                                Beacon beacon = list.get(i);
//                                if (beacon.getId3().equals(discoveredDevices.get(j).getMinor())) {
//                                    flagOfExit = true;
//                                }
//                            }
//                            if (flagOfExit) {
//                                discoveredDevices.get(j).InsertRssi(0);
//                            }
//                        }
//                        //remove devices that all their rssis = 0
//                        for (int i = 0; i < discoveredDevices.size(); i++) {
//                            if (discoveredDevices.get(i).canThatCanRemoveThisDivice()) {
//                                discoveredDevices.remove(i);
//                            }
//                        }
//
//                        sortDiscoveredDevices();
//
//
//
//                        /*if(proposedBeacon == null){
//                            proposedBeacon = discoveredDevices.get(0);
//                            Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                        }else {
//                            if(!discoveredDevices.get(0).getMac().equals(proposedBeacon.getMac()))
//                                if( discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
//                                   proposedBeacon = discoveredDevices.get(1);
//                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                                }else if(!discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
//                                    proposedBeacon = discoveredDevices.get(0);
//                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                                }
//                        }*/
////                        Log.d("beaconData:",String.valueOf(discoveredDevices.get(0).getRssiAvg()));
//
//                        if (discoveredDevices.get(0).getRssiAvg() >= MIN_RSS_TO_RELOCATE) {
//                            proposedBeacon = discoveredDevices.get(0);
//                        }
//
//
//                    }
//                }
//            }
//
//        };
//
//
//        try {
//
//            //set uuid of beacons and their major for better discovering
//            beaconRegion = new Region("beacon", Identifier.parse(Constants.COMMON_UUID_BEACON), null, null);
//            beaconManager.startRangingBeaconsInRegion(beaconRegion);
////            beaconManager.addRangeNotifier(rangeNotifier);
//            beaconManager.startRangingBeaconsInRegion(beaconRegion);
////            beaconManager.addRangeNotifier(rangeNotifier);
//
//        } catch (RemoteException e) {
//            Log.e("beacon_manager", e.getMessage());
//        }
//
//
//    }

    private void sortDiscoveredDevices() {

        for (int i = 0; i < discoveredDevices.size(); i++) {
            for (int j = i; j < discoveredDevices.size(); j++) {
                if (discoveredDevices.get(j).getRssiAvg() > discoveredDevices.get(i).getRssiAvg()) {
                    BLEdevice a = discoveredDevices.get(i);
                    discoveredDevices.set(i, discoveredDevices.get(j));
                    discoveredDevices.set(j, a);
                }
            }

        }
    }

    //stop discovering Beacons In Region
    public void stopMonitoring() {


        try {

            beaconRegion = new Region("beacon", Identifier.parse(Constants.COMMON_UUID_BEACON), Identifier.parse(Constants.COMMON_MAJOR_BEACON), null);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);

            beaconManager.stopRangingBeaconsInRegion(beaconRegion);

        } catch (RemoteException e) {
        }


    }


//    @Override
//    public void onBeaconServiceConnect() {
////        beaconManager.setForegroundBetweenScanPeriod(Constants.PERIOD_TIME_BETWEEN_SCAN);
//
//        try {
//            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
//
//            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
//
//        } catch (RemoteException e) {
//            Log.e("Error", e.getMessage());
//        }
//    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(rangeNotifier);
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
//        return true;
    }

    public BLEdevice getProposedBeacon() {
        return proposedBeacon;
    }


    public ArrayList<String> getAllSortedDiscoveredBeaconLocations() {
        if (discoveredDevices != null) {
            if (discoveredDevices.size() > 0) {
                ArrayList<String> locations = new ArrayList<String>();
                for (BLEdevice beacon : discoveredDevices) {
                    String nearMac = beacon.getMac();
                    BeaconLocations beaconLocations = new BeaconLocations();
                    Double[] location = beaconLocations.beaconCoordinates.get(nearMac);
                    if (location != null) {
                        String loc = location[0] + "," + location[1];
                        locations.add(loc);
                    }
                }
                return locations;
            }
        }
        return null;
    }

    public String getNearLocationToString() {
        try {
//             if(discoveredDevices.get(0).getRssiAvg()
//                    -discoveredDevices.get(1).getRssiAvg()<4){
//                String nearMac1 = discoveredDevices.get(0).getMac();
//                BeaconLocations locationOfBeacon1 = new BeaconLocations();
//                Double [] location1 = locationOfBeacon1.beaconCoordinates.get(nearMac1);
//                String nearMac2 = discoveredDevices.get(1).getMac();
//                BeaconLocations locationOfBeacon2 = new BeaconLocations();
//                Double [] location2 = locationOfBeacon2.beaconCoordinates.get(nearMac2);
//                if(location1 != null){
//                    String loc = ((location1[0]+location2[0])/2)+","+((location1[1]+location2[1])/2);
//                    return loc;
//                }
//
//
//            }else {
//                String nearMac = discoveredDevices.get(0).getMac();
//                BeaconLocations locationOfBeacon = new BeaconLocations();
//                Double [] location = locationOfBeacon.beaconCoordinates.get(nearMac);
//                if(location != null){
//                    String loc = location[0]+","+location[1];
//                    return loc;
//                }
//            }


            if (proposedBeacon != null) {
                String nearMac = proposedBeacon.getMac();
                BeaconLocations beaconLocations = new BeaconLocations();
                Double[] location = beaconLocations.beaconCoordinates.get(nearMac);
                if (location != null) {
                    String loc = location[0] + "," + location[1];
                    return loc;
                }
            }


        } catch (RuntimeException e) {

        }


        return null;
    }

//    private final Lock _mutex = new ReentrantLock(true);

    // MotionDna:
    public void startMonitoring() {

        rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                List<Beacon> beaconList = new ArrayList<Beacon>(beacons);
                if (beacons.size() > 0 && callback != null) {
                    // Sort beacon according to the rssi
                    Collections.sort(beaconList, new Comparator<Beacon>() {
                        public int compare(Beacon b1, Beacon b2) {
                            return ((Integer) b1.getRssi()).compareTo((Integer) b2.getRssi());
                        }
                    });

                    ;
                    ArrayList<Beacon> validBeacons = new ArrayList<>();

                    for (Beacon beacon : beaconList) {
                        Log.e("beacon", "didRangeBeaconsInRegion called with beacon count:  " + beaconList.toString());
                        final String beaconName = beacon.getBluetoothAddress();
                        HashMap<String, Double[]> beaconCoordinates = beaconLocations.beaconCoordinates;
                        if (beaconCoordinates.containsKey(beaconName)) {
                            validBeacons.add(beacon);
//                                callback.onBeaconDetection(beacon);
                        }
                    }
                    callback.onBeaconDetection(validBeacons);


//
//
//
//                    //covert collection<beacon> to list<beacon> for access to beacons
//                    List<Beacon> list = new ArrayList<Beacon>(beacons);
//
//                    if (list.size() > 0) {
//
//                        for (int i = 0; i < list.size(); i++) {
//                            Beacon beacon = list.get(i);
//                            boolean flag = false;
//
//                            for (int j = 0; j < discoveredDevices.size(); j++) {
//                                Integer a = beacon.getId3().toInt();
//                                Integer b = Integer.valueOf(discoveredDevices.get(j).getMinor());
//
//                                if (a.equals(b)
//                                ) {
//                                    discoveredDevices.get(j).InsertRssi(beacon.getRssi());
//
//                                    Log.i("beacon", "minor: " + discoveredDevices.get(j).getMac() + " add rssi=" + beacon.getRssi());
//                                    flag = true;
//
//
//                                }
//                            }
//
//                            if (!flag) {
//
//
//                                Log.i("beacon", "add minor=" + beacon.getId3().toString());
//                                BLEdevice blEdevice = new BLEdevice();
//                                blEdevice.setUUID(beacon.getId1().toString());
//                                blEdevice.setMajor(beacon.getId2().toString());
//                                blEdevice.setMinor(beacon.getId3().toString());
//                                blEdevice.InsertRssi(beacon.getRssi());
//                                blEdevice.setMac(beacon.getBluetoothAddress());
//                                Log.i("beacon", "mac" + blEdevice.getMac());
//                                discoveredDevices.add(blEdevice);
//
//
//                            }
//
//
//                        }
//
//                        //add zero to devices that no discovering in this scan
//                        for (int j = 0; j < discoveredDevices.size(); j++) {
//                            boolean flagOfExit = false;
//
//                            for (int i = 0; i < list.size(); i++) {
//                                Beacon beacon = list.get(i);
//                                if (beacon.getId3().equals(discoveredDevices.get(j).getMinor())) {
//                                    flagOfExit = true;
//                                }
//                            }
//                            if (flagOfExit) {
//                                discoveredDevices.get(j).InsertRssi(0);
//                            }
//                        }
//                        //remove devices that all their rssis = 0
//                        for (int i = 0; i < discoveredDevices.size(); i++) {
//                            if (discoveredDevices.get(i).canThatCanRemoveThisDivice()) {
//                                discoveredDevices.remove(i);
//                            }
//                        }
//
//                        sortDiscoveredDevices();
//
//
//
//                        /*if(proposedBeacon == null){
//                            proposedBeacon = discoveredDevices.get(0);
//                            Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                        }else {
//                            if(!discoveredDevices.get(0).getMac().equals(proposedBeacon.getMac()))
//                                if( discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
//                                   proposedBeacon = discoveredDevices.get(1);
//                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                                }else if(!discoveredDevices.get(1).getMac().equals(proposedBeacon.getMac())){
//                                    proposedBeacon = discoveredDevices.get(0);
//                                    Toast.makeText(context,"proposedBeacon Minor"+proposedBeacon.getMinor(),Toast.LENGTH_SHORT);
//                                }
//                        }*/
////                        Log.d("beaconData:",String.valueOf(discoveredDevices.get(0).getRssiAvg()));
//
//                        if (discoveredDevices.get(0).getRssiAvg() >= MIN_RSS_TO_RELOCATE) {
//                            proposedBeacon = discoveredDevices.get(0);
//                        }


//                    }
                }
            }

        };


        try {

            //set uuid of beacons and their major for better discovering
            beaconRegion = new Region("beacon", Identifier.parse(Constants.COMMON_UUID_BEACON), null, null);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
//            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
//            beaconManager.addRangeNotifier(rangeNotifier);

        } catch (RemoteException e) {
            Log.e("beacon_manager", e.getMessage());
        }


    }

    public void setCallback(BeaconCallback beaconCallback) {
        callback = beaconCallback;
    }
}
