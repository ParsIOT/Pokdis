package ir.parsiot.pokdis.MotionDna;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by hadi on 7/26/18.
 */

public class NaviSettings {
    public double scale = 100;
    public int scanPeriod = 100;
    public HashMap<String, Double[]> beaconCoordinates = new HashMap<String, Double[]>() {{
//        //Iust:29tir
//        put("01:17:C5:97:E7:B3", new Double[]{10.15 *settings.scale, 2.60 * settings.scale});
//        put("01:17:C5:97:1B:44", new Double[]{6.40 * settings.scale, -6.85 * settings.scale});
//        put("01:17:C5:97:87:84", new Double[]{-3.10 * settings.scale, -1.00 * settings.scale});
//        put("01:17:C5:97:58:C3", new Double[]{-1.75 * settings.scale, 8.00 * settings.scale});
//
//        put("01:17:C5:97:DE:E8", new Double[]{16.00 * settings.scale, -14.35 * settings.scale});
//        put("01:17:C5:97:5B:1D", new Double[]{16.00 * settings.scale, -2.60 * settings.scale});
//        put("01:17:C5:97:B5:70", new Double[]{16.20 * settings.scale, 8.75 * settings.scale});
//
//        put("01:17:C5:97:44:BE", new Double[]{6.30 * settings.scale, 13.15 * settings.scale});
//        put("01:17:C5:97:3C:63", new Double[]{-2.10 * settings.scale, 13.15 * settings.scale});
//        put("01:17:C5:97:37:39", new Double[]{-16.86 * settings.scale, 13.15 * settings.scale});

//        //arman: main
//        put("01:17:C5:97:E7:B3", new Double[]{-8.50 * settings.scale, -2.20 * settings.scale});
//        put("01:17:C5:97:1B:44", new Double[]{-8.25 * settings.scale, -7.10 * settings.scale});
//        put("01:17:C5:97:87:84", new Double[]{-0.90 * settings.scale, -3.40 * settings.scale});
//        put("01:17:C5:97:3C:63", new Double[]{8.35 * settings.scale, -2.20 * settings.scale});
//        put("01:17:C5:97:37:39", new Double[]{8.10 * settings.scale, -8.40 * settings.scale});


        put("01:17:C5:97:DE:E8", new Double[]{-3.75 * scale, 9.50 * scale});
        put("01:17:C5:97:5B:1D", new Double[]{-2.65 * scale, 4.20 * scale});
//        put("01:17:C5:97:B5:70", new Double[]{8.50 * settings.scale, 9.50 * settings.scale});
        put("01:17:C5:97:94:E1", new Double[]{8.50 * scale, 9.50 * scale});
//        put("01:17:C5:97:44:BE", new Double[]{8.60 * settings.scale, 2.25 * settings.scale});
        put("01:17:C5:9B:B2:CB", new Double[]{8.60 * scale, 2.25 * scale});
        //kontakt
//        put("F3:B5:8A:4C:AA:CB", new Double[]{-3.75 * settings.scale, 9.50 * settings.scale});
//        put("F7:38:03:58:5F:67", new Double[]{-2.65 * settings.scale, 4.20 * settings.scale});
//        put("E5:6B:13:8B:A7:CD", new Double[]{8.50 * settings.scale, 9.50 * settings.scale});
//        put("D3:88:EE:6C:33:17", new Double[]{8.60 * settings.scale, 2.25 * settings.scale});
//
        //radio land
//        put("FA:CF:CB:5D:0E:B8", new Double[]{-3.75 * settings.scale, 9.50 * settings.scale});
//        put("FA:C5:13:37:F5:09", new Double[]{-2.65 * settings.scale, 4.20 * settings.scale});
//        put("F0:AB:CE:31:10:B9", new Double[]{8.50 * settings.scale, 9.50 * settings.scale});
//        put("CB:16:FA:98:34:D7", new Double[]{8.60 * settings.scale, 2.25 * settings.scale});
//

    }};
    public HashMap<String, Double[]> beaconIsChosenCoordinate = new HashMap<>();

    public int maxDeltaHsLen1 = 10;
    public int maxDeltaHsLen2 = 30;
    public double cornerDetectionTreshold1 = 20;
    public double cornerDetectionTreshold2 = 60;
    public int minTimeToDetectNewRssi = 300;
    public int maxBeaconDelayms = 10 * 1000; // 10sec ;;
    public int rssHistoryMaxLen = 5; //moving avg window size ;;
    public int rssThreshold = -70;
    public int beaconDirectionRssThresholdDiff = -2;
    public int bySideRssThreshold = -63;
    public int countThreshold = 5;
    public int bySideCountThreshold = 3;
    public double adjencyCirecle = 6 * scale;
    public int maxValidCountInAdjency = 10;
    public int maxValidRelocationHDiff = 150;
    public int minTimeBetweenSameSidebySide = 10 * 1000;
    public double cornerCandidateDiff = 2 * scale;
    public double cornerDistanceDiff = 4 * scale;
    public int maxtoBeaconVectorhDiff = 60;
    public int betweenPeriod = 0;
    public int maxCountFar = 3;
    public long liveLocationInterval = 1000;

    public String logBeaconIsChosenMap() {
        StringBuilder sb = new StringBuilder("");
        for (Map.Entry<String, Double[]> entry : beaconIsChosenCoordinate.entrySet()) {
            sb.append(String.format(Locale.getDefault(), "{%s: %f,%f} ", entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "NaviSettings{" +
                "scale=" + scale +
                ", scanPeriod=" + scanPeriod +
//                ", beaconCoordinates=" + beaconCoordinates +
                ", beaconIsChosenCoordinate=" + logBeaconIsChosenMap() +
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
                ", adjencyCirecle=" + adjencyCirecle +
                ", maxValidCountInAdjency=" + maxValidCountInAdjency +
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
