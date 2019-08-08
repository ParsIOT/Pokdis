package ir.parsiot.pokdis.Localization.Beacon;

import org.altbeacon.beacon.Beacon;

public interface BeaconCallback {
    public void onBeaconDetection(Beacon beacon);
}

