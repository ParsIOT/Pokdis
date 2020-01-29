package ir.parsiot.pokdis.Localization.Beacon;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;

public interface BeaconCallback {
//    public void onBeaconDetection(Beacon beacon);
//    public void onBeaconDetection(ArrayList<Beacon> beacons);
    public void onBeaconDetection(ArrayList<BLEdevice> sortedDiscoveredDevices);
}

