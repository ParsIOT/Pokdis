package ir.parsiot.pokdis.beacon;

import org.altbeacon.beacon.Beacon;

public interface BeaconCallback {
    public void onBeaconDetection(Beacon beacon);
}

