package ir.parsiod.NavigationInTheBuilding.beacon;

import java.util.HashMap;

/**
 * Created by seyedalian on 11/6/2019.
 */

public class LocationOfBeacon {
  public   HashMap<String, Double[]> beaconCoordinates;





    public LocationOfBeacon() {
        beaconCoordinates = new HashMap<String, Double[]>() {{

            put("01:17:C5:97:E7:B3", new Double[]{1524d,-244d});//1
            put("01:17:C5:97:1B:44", new Double[]{980d,1080d });//2
            put("01:17:C5:97:87:84", new Double[]{780d,-40d});//3
          //  put("01:17:C5:97:58:C3", new Double[]{-780d,1080d });//4

        /*    put("01:17:C5:97:DE:E8", new Double[]{-1184d, -1312d });
            put("01:17:C5:97:5B:1D", new Double[]{-1196d, -440d });
            put("01:17:C5:97:B5:70", new Double[]{-1180d,432d });
            put("01:17:C5:97:44:BE", new Double[]{-1168d,1268d });

          */ // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10


        }};



    }




}
