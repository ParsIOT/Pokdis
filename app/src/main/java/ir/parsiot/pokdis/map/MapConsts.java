package ir.parsiot.pokdis.map;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.parsiot.pokdis.map.WallGraph.WallGraph;

public class MapConsts {
    public static double scale = 100;
    public static int MAP_WIDTH = 909 , MAP_HEIGHT = 769;


    // Note: change init location(latlng variable) in map.html too.
    public static double initHeading = 90.0;
    public static String initLocation = "-320,330";
    public static ArrayList<Float> getInitLocationFloat(){
        String[] loc = initLocation.split(",");
        ArrayList<Float> initLocFloat = new ArrayList<Float>();
        initLocFloat.add(Float.valueOf(loc[0]));
        initLocFloat.add(Float.valueOf(loc[1]));
        return initLocFloat;
    }

    public static ArrayList<Double> getInitLocationDouble(){
        String[] loc = initLocation.split(",");
        ArrayList<Double> initLocFloat = new ArrayList<Double>();
        initLocFloat.add(Double.valueOf(loc[0]));
        initLocFloat.add(Double.valueOf(loc[1]));
        return initLocFloat;
    }

//    public static String initLocation = "0,-150";
    public static float epsilon = 0.01f;
    public HashMap<String,String > vertexOfGraph;
    public HashMap<String,String > wallGraphVertexes;
    public ArrayList<ArrayList<String>> wallGraphEdges;
    public static WallGraph wallGraph;

// vertex location on map
    public MapConsts() {
        vertexOfGraph = new HashMap<String, String>() {{
                                //index as 1
            put("1","275,-380");
            put("2", "275,-140");
            put("3", "275,100");
            put("4", "275,330");

            put("5", "0,-380");
            put("6", "0,-140");
            put("7", "0,100");
            put("8", "0,330");

            put("9", "-320,-380");
            put("10", "-320,-140");
            put("11", "-320,100");
            put("12", "-320,330");

            // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10
        }};


        wallGraphVertexes = new HashMap<String, String>() {{
            //index as 1
            put("1","-380,400");
            put("2", "-380,-450");
            put("3", "380,400");
            put("4", "340,-450");
            // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10
        }};
        wallGraphEdges = new ArrayList<ArrayList<String>>() {{
            add(new ArrayList<String>(){{add("1");add("2");}});
            add(new ArrayList<String>(){{add("2");add("4");}});
            add(new ArrayList<String>(){{add("1");add("3");}});
            add(new ArrayList<String>(){{add("3");add("4");}});
        }};

        wallGraph = new WallGraph(wallGraphVertexes, wallGraphEdges);

//        Double[] srcDot = new Double[]{260d,-260d};
//        Double[] dstDot = new Double[]{588d,-310d};
//
//        Double[][] collidedWall = wallGraph.Get_collision(srcDot, dstDot);
//        if (collidedWall == null){
//            Log.e("collidedWall",null);
//        }else{
//            Log.e("collidedWall",collidedWall.toString());
//        }

    }

    public String isGraphVertex(String point){ // Check that the point is one of the graph vertexes or not
        for(Map.Entry mapElem : vertexOfGraph.entrySet()){
            if (point.equals((String)mapElem.getValue())){
                String tag = (String)mapElem.getKey();
                return tag;
            }
        }
        return null;
    }

}
