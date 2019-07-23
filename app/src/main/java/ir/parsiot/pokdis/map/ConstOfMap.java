package ir.parsiot.pokdis.map;

import java.util.HashMap;

public class ConstOfMap {

    public HashMap<String,String > vertexOfGraph;

// vertex location on map
    public ConstOfMap() {
        vertexOfGraph = new HashMap<String, String>() {{
                                //index as 1
            put("1","382,-483");
            put("2", "416,-211");
            put("3", "418,150");
            put("4", "380,406");

            put("5", "47,-540");
            put("6", "3,-196");
            put("7", "23,99");
            put("8", "59,459");

            put("9", "-329,-493");
            put("10", "-335,-195");
            put("11", "-353,126");
            put("12", "-327,446");

            // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10


        }};
    }
}
