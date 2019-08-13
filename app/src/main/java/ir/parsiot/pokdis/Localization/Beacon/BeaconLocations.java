package ir.parsiot.pokdis.Localization.Beacon;

import java.util.HashMap;

public class BeaconLocations {
  public HashMap<String, Double[]> beaconCoordinates;



//    public BeaconLocations() {
//        beaconCoordinates = new HashMap<String, Double[]>() {{
//            put("01:17:C5:97:E7:B3", new Double[]{-320d, 330d});//1
//            put("01:17:C5:97:1B:44", new Double[]{-320d, -22d});//2
//            put("01:17:C5:97:87:84", new Double[]{-320d, -375d});//3
//
//            put("01:17:C5:97:58:C3", new Double[]{280d, 330d});//4
//            put("01:17:C5:97:DE:E8", new Double[]{280d, -22d});//5
//            put("01:17:C5:97:5B:1D", new Double[]{280d, -375d});//6
//
//            put("01:17:C5:97:B5:70", new Double[]{0d, 330d});//7
//            put("01:17:C5:97:44:BE", new Double[]{0d, -145d});//8
//            put("01:17:C5:97:3C:63", new Double[]{0d, -375d});//9
//
////            put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10
//        }};
//    }

    public BeaconLocations() {
        beaconCoordinates = new HashMap<String, Double[]>() {{
            put("01:17:C5:97:E7:B3", new Double[]{-320d, 330d});//1
            put("01:17:C5:97:1B:44", new Double[]{-320d, 100d});//2
            put("01:17:C5:97:87:84", new Double[]{-320d, -150d});//3

            put("01:17:C5:97:58:C3", new Double[]{280d, 330d});//4
            put("01:17:C5:97:DE:E8", new Double[]{280d, 100d});//5
            put("01:17:C5:97:5B:1D", new Double[]{280d, -150d});//6

            put("01:17:C5:97:B5:70", new Double[]{0d, 330d});//7
            put("01:17:C5:97:44:BE", new Double[]{0d, 100d});//8
            put("01:17:C5:97:3C:63", new Double[]{0d, -150d});//9

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
