package ir.parsiot.pokdis.Localization.Beacon;

import java.util.HashMap;

public class BeaconLocations {
  public HashMap<String, Double[]> beaconCoordinates;



//    public BeaconLocations() {
//        beaconCoordinates = new HashMap<String, Double[]>() {{
//            put("01:17:C5:97:E7:B3", new Double[]{-320d, 330d});//1
//            put("01:17:C5:97:1B:44", new Double[]{-320d, 100d});//2
//            put("01:17:C5:97:87:84", new Double[]{-320d, -150d});//3
//
//            put("01:17:C5:97:58:C3", new Double[]{280d, 330d});//4
//            put("01:17:C5:97:DE:E8", new Double[]{280d, 100d});//5
//            put("01:17:C5:97:5B:1D", new Double[]{280d, -150d});//6
//
//            put("01:17:C5:97:B5:70", new Double[]{0d, 330d});//7
//            put("01:17:C5:97:44:BE", new Double[]{0d, 100d});//8
//            put("01:17:C5:97:3C:63", new Double[]{0d, -150d});//9
//
//        }};
//    }

    public BeaconLocations() {
        beaconCoordinates = new HashMap<String, Double[]>() {{
            put("C1:00:BB:00:37:5A", new Double[]{343d, -345d});//1
            put("FD:2A:62:D4:A0:6E", new Double[]{331d, 345d});//2
            put("FA:6A:B5:78:0A:95", new Double[]{-345d, -344d});//3
            put("D1:21:95:1C:4F:59", new Double[]{-339d, 345d});//4

            put("01:17:C5:97:DE:E8", new Double[]{430d, -30d});//5
            put("01:17:C5:97:5B:1D", new Double[]{430d, -560d});//6

            put("01:17:C5:97:B5:70", new Double[]{-30d, 490d});//7
            put("01:17:C5:97:44:BE", new Double[]{-30d, 140d});//8
            put("01:17:C5:97:3C:63", new Double[]{-30d, -200d});//9
            put("01:17:C5:97:37:39", new Double[]{-30d, -560d});//10

        }};
    }

//    public BeaconLocations() {
//        beaconCoordinates = new HashMap<String, Double[]>() {{
////            put("01:17:C5:97:E7:B3", new Double[]{-320d, 220d});//1
////            put("01:17:C5:97:1B:44", new Double[]{-320d, -20d});//2
////            put("01:17:C5:97:87:84", new Double[]{-320d, -260d});//3
////
////            put("01:17:C5:97:58:C3", new Double[]{280d, 220d});//4
////            put("01:17:C5:97:DE:E8", new Double[]{280d, -20d});//5
////            put("01:17:C5:97:5B:1D", new Double[]{280d, -260d});//6
////
////            put("01:17:C5:97:B5:70", new Double[]{0d, 330d});//7
////            put("01:17:C5:97:44:BE", new Double[]{0d, 100d});//8
////            put("01:17:C5:97:3C:63", new Double[]{0d, -150d});//9
////
//
//        }};
//    }
}
