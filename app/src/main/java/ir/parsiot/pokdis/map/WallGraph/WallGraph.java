package ir.parsiot.pokdis.map.WallGraph;

import java.util.ArrayList;
import java.util.HashMap;

public class WallGraph {
    HashMap<String, Double[]> wallGraphVertexes = new HashMap<String, Double[]>();
    ArrayList<ArrayList<String>> wallGraphEdge = new ArrayList<ArrayList<String>>();

    Double[][][] walls;

    public WallGraph(Double[][][] walls) {
        this.walls = walls;
    }

//    public Double[][] getEdgeVertexes(ArrayList<String> wall){
//        Double[][] vertexes = new Double[][]{
//                wallGraphVertexes.get(wall.get(0)),
//                wallGraphVertexes.get(wall.get(1)),
//        };
//        return vertexes;
//    }

    public boolean onSegment(Double[] p, Double[] q, Double[] r){
        if (q[0] <= Math.max(p[0], r[0]) && q[0] >= Math.min(p[0], r[0]) && q[1] <= Math.max(p[1], r[1]) && q[1] >= Math.min(p[1], r[1])){
            return true;
        }else{
            return false;
        }
    }

    public int orientation(Double[] p, Double[] q, Double[] r){
        Double val = (q[1] - p[1]) * (r[0] - q[0]) - (q[0] - p[0]) * (r[1] - q[1]);
        if (val == 0){
            return 0;
        }
        else if(val > 0){
            return 1;
        }
        else {
            return 2;
        }
    }

    public boolean haveIntersect(Double[][] line1, Double[][] line2){
        Double[] p1 = line1[0];
        Double[] q1 = line1[1];
        Double[] p2 = line2[0];
        Double[] q2 = line2[1];

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;
        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;
        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;
        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;
        if (o4 == 0 && onSegment(p2, q1, q2))
            return true;

        return false;
    }


    public Double[][] getCollision(final Double[] src, final Double[] dst){
        for(Double[][] wall : this.walls){

            Double[][] relocationLine = new Double[][]{
                   src,
                   dst,
            };
            if(haveIntersect( wall, relocationLine) ){
                return wall;
            }
        }
        return null;
    }
}
