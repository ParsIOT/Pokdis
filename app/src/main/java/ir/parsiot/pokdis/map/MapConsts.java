package ir.parsiot.pokdis.map;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ir.parsiot.pokdis.map.WallGraph.RectObstacle;
import ir.parsiot.pokdis.map.WallGraph.WallGraph;

public class MapConsts {
    public static double scale = 100;
//    public static int MAP_WIDTH = 909, MAP_HEIGHT = 769;
    public static int MAP_WIDTH = 1320, MAP_HEIGHT = 1200;


    // Note: change init location(latlng variable) in map.html too.
    public static double initHeading = 90.0;
//    public static String initLocation = "-320,330";
    public static String initLocation = "-500,490";
    public static ArrayList<Double> constInitState = new ArrayList<Double>(){{
        add(-500d);
        add(490d);
        add(90d);
    }};

    public static ArrayList<Float> getInitLocationFloat() {
        String[] loc = initLocation.split(",");
        ArrayList<Float> initLocFloat = new ArrayList<Float>();
        initLocFloat.add(Float.valueOf(loc[0]));
        initLocFloat.add(Float.valueOf(loc[1]));
        return initLocFloat;
    }

    public static ArrayList<Double> getInitLocationDouble() {
        String[] loc = initLocation.split(",");
        ArrayList<Double> initLocFloat = new ArrayList<Double>();
        initLocFloat.add(Double.valueOf(loc[0]));
        initLocFloat.add(Double.valueOf(loc[1]));
        return initLocFloat;
    }

    //    public static String initLocation = "0,-150";
    public static float epsilon = 0.01f;
    public HashMap<String, String> vertexOfGraph;

    public RectObstacle mapBorderRect;
    public ArrayList<Double[][]> standaloneWalls;
    public ArrayList<Double[][]> obstacleVertexes;
    public ArrayList<Double[][]> allWalls = new ArrayList<Double[][]>();
    public ArrayList<RectObstacle> rectObstacles = new ArrayList<RectObstacle>();
    public WallGraph wallGraph;

    // vertex location on map
    public MapConsts() {
//        vertexOfGraph = new HashMap<String, String>() {{
//            //index as 1
//            put("1", "275,-380");
//            put("2", "275,-140");
//            put("3", "275,100");
//            put("4", "275,330");
//
//            put("5", "0,-380");
//            put("6", "0,-140");
//            put("7", "0,100");
//            put("8", "0,330");
//
//            put("9", "-320,-380");
//            put("10", "-320,-140");
//            put("11", "-320,100");
//            put("12", "-320,330");
//
//            // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10
//        }};

        vertexOfGraph = new HashMap<String, String>() {{
            //index as 1
            put("1", "430,-560");
            put("2", "430,-200");
            put("3", "430,140");
            put("4", "430,490");

            put("5", "-30,-560");
            put("6", "-30,-200");
            put("7", "-30,140");
            put("8", "-30,490");

            put("9", "-490,-560");
            put("10", "-490,-200");
            put("11", "-490,140");
            put("12", "-490,490");

            // put("01:17:C5:97:37:39", new Double[]{700d,-888d }); //10
        }};


//        Double[] leftTopBorderDot = new Double[]{-450d, 340d};
//        Double[] rightBottomBorderDot = new Double[]{405d, -380d};
        Double[] leftTopBorderDot = new Double[]{-660d, 535d};
        Double[] rightBottomBorderDot = new Double[]{595d, -600d};

        mapBorderRect = new RectObstacle(leftTopBorderDot, rightBottomBorderDot);

        standaloneWalls = new ArrayList<Double[][]>() {{
//            add(new Double[][]{
//                    new Double[]{-380d,400d},
//                    new Double[]{-380d,-450d},
//            });
        }};

        // Obstacles
//        obstacleVertexes = new ArrayList<Double[][]>() {{
//            add(new Double[][]{
//                    new Double[]{165d, 200d},
//                    new Double[]{265d, -250d},
//            });
//
//
//            add(new Double[][]{
//                    new Double[]{-75d, 200d},
//                    new Double[]{30d, -250d},
//            });
//
//            add(new Double[][]{
//                    new Double[]{-310d, 200d},
//                    new Double[]{-210d, -250d},
//            });
//        }};
        obstacleVertexes = new ArrayList<Double[][]>() {{
            add(new Double[][]{
                    new Double[]{240d, 315d},
                    new Double[]{385d, -390d},
            });


            add(new Double[][]{
                    new Double[]{-105d, 315d},
                    new Double[]{40d, -390d},
            });

            add(new Double[][]{
                    new Double[]{-450d, 315d},
                    new Double[]{-305d, -390d},
            });
        }};

        for (Double[][] obstacle : obstacleVertexes) {
            RectObstacle newRectObstacle = new RectObstacle(obstacle[0], obstacle[1]);
            rectObstacles.add(newRectObstacle);
        }


        // Create allWalls
        for (Double[][] wall : mapBorderRect.getWalls()) {
            allWalls.add(wall);
        }
        for (Double[][] wall : standaloneWalls) {
            allWalls.add(wall);
        }
        for (RectObstacle rectObstacle : this.rectObstacles) {
            for (Double[][] wall : rectObstacle.getWalls()) {
                allWalls.add(wall);
            }
        }

        Double[][][] allWallsDouble = allWalls.toArray(new Double[allWalls.size()][][]);
        wallGraph = new WallGraph(allWallsDouble);
    }

    ArrayList<String> a;
    ArrayList<Double> b;

    public String isGraphVertex(String point) { // Check that the point is one of the graph vertexes or not

        for (Map.Entry mapElem : vertexOfGraph.entrySet()) {
            if (point.equals((String) mapElem.getValue())) {
                String tag = (String) mapElem.getKey();
                return tag;
            }
        }
        return null;
    }

}
