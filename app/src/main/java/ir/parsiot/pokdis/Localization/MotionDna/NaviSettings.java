package ir.parsiot.pokdis.Localization.MotionDna;

import ir.parsiot.pokdis.map.MapConsts;

/**
 * Created by hadi on 7/26/18.
 */

public class NaviSettings {
    public double scale = MapConsts.scale;

    public int maxDeltaHsLen1 = 10;
    public int maxDeltaHsLen2 = 30;
    public double cornerDetectionTreshold1 = 20;
    public double cornerDetectionTreshold2 = 60;
    public int minTimeToDetectNewRssi = 300;

    // Note: Critical to change in sharvand proj
    public double adjacencyCircle = 6 * scale;
    public int rssThreshold = -70;
    public int bySideRssThreshold = -63;


    // Note: Change it in sharvand proj
    public int maxBeaconDelayms = 10 * 1000; // 10sec  // If we don't see a beacon for this time ,We delete it from history
    public int beaconDirectionRssThresholdDiff = -2; // Todo: It's so weak and breakable logic behind it, change it.
    public int rssHistoryMaxLen = 5; //moving avg window size ;;
    public int bySideCountThreshold = 3;
    public int minTimeBetweenSameSidebySide = 10 * 1000;


    public int countThreshold = 5;
    public int maxValidCountInAdjacency = 10;
    public int maxValidRelocationHDiff = 150;
    public double cornerCandidateDiff = 2 * scale;
    public double cornerDistanceDiff = 4 * scale;
    public int maxtoBeaconVectorhDiff = 60;
    public int betweenPeriod = 0;
    public int maxCountFar = 3;
    public long liveLocationInterval = 1000;



    public static final int MAX_PROXIMITY_TO_ROUTE_THRESHOLD_IMU = 50;



    @Override
    public String toString() {
        return "NaviSettings{" +
                "scale=" + scale +
//                ", beaconCoordinates=" + beaconCoordinates +
                ", maxDeltaHsLen1=" + maxDeltaHsLen1 +
                ", maxDeltaHsLen2=" + maxDeltaHsLen2 +
                ", cornerDetectionTreshold1=" + cornerDetectionTreshold1 +
                ", cornerDetectionTreshold2=" + cornerDetectionTreshold2 +
                ", minTimeToDetectNewRssi=" + minTimeToDetectNewRssi +
                ", maxBeaconDelayms=" + maxBeaconDelayms +
                ", rssHistoryMaxLen=" + rssHistoryMaxLen +
                ", rssThreshold=" + rssThreshold +
                ", beaconDirectionRssThresholdDiff=" + beaconDirectionRssThresholdDiff +
                ", bySideRssThreshold=" + bySideRssThreshold +
                ", countThreshold=" + countThreshold +
                ", bySideCountThreshold=" + bySideCountThreshold +
                ", adjencyCirecle=" + adjacencyCircle +
                ", maxValidCountInAdjency=" + maxValidCountInAdjacency +
                ", maxValidRelocationHDiff=" + maxValidRelocationHDiff +
                ", minTimeBetweenSameSidebySide=" + minTimeBetweenSameSidebySide +
                ", cornerCandidateDiff=" + cornerCandidateDiff +
                ", cornerDistanceDiff=" + cornerDistanceDiff +
                ", maxtoBeaconVectorhDiff=" + maxtoBeaconVectorhDiff +
                ", betweenPeriod=" + betweenPeriod +
                ", maxCountFar=" + maxCountFar +
                ", liveLocationInterval=" + liveLocationInterval +
                '}';
    }
}
