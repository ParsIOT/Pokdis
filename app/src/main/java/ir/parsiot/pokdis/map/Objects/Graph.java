package ir.parsiot.pokdis.map.Objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ir.parsiot.pokdis.map.ConstOfMap;
import ir.parsiot.pokdis.map.GraphBuilder;

public class Graph {

    private int[][] vertex;
    private List<Vertex> vertices;
    int[][] path;
    private List<Edge> edges;
    private String nearPath = "";
    private int size;
    //private int sizeOfedges;
    String lEdges = "";
    ConstOfMap constOfMap;

    public List<Edge> getEdges() {
        return edges;
    }

    public Graph() {
        this.edges = new ArrayList<>();
        vertices = new ArrayList<>();
        constOfMap = new ConstOfMap();
    }

    //ADD WITH KEY OF HASH IN constOfMap
    public void addEdge(String ver1, String ver2) {
        edges.add(new Edge().setVertexs(constOfMap.vertexOfGraph.get(ver1), ver1
                , constOfMap.vertexOfGraph.get(ver2), ver2));


        if (lEdges.length() < 1) {
            lEdges += ver1 + "-" + ver2;
        } else {
            lEdges += "," + ver1 + "-" + ver2;
        }

    }

    //search in edges list for find near edge
    public Edge findNearEdge(String point) {

        if (edges.size() > 0) {
            Edge near = null;
            float min = edges.get(0).distanceFromTheLine(point);

            for (int i = 0; i < edges.size(); i++) {
                Edge edg = edges.get(i);

                float dist = edg.distanceFromTheLine(point);
                if (dist == 0) {
                    return edg;
                }
                if (min > dist) {
                    min = dist;
                    near = edg;
                }

            }


            return near;
        }


        return null;
    }

    //getPathBetween to vertex on
    public String getPathBetween(String ver1, String ver2) {
        nearPath = "";

        makePaths();

        path(Integer.valueOf(ver1), Integer.valueOf(ver2));

        return nearPath;

    }

    //getPathBetween to vertex on
    public String getPathBetweenEdges(String edge1V, Edge edge1, String edge2V, Edge edge2) {
        /*
        First we use getPathBetween to find path between two the closest vertexes of the closest edges(edg11V and edge2V) to our source and destination point.
        Then we check that if both of edge1 vertexes are in the path, we delete one of the vertexes that may cause making the path longer. We do the same for edge2 too.
         */
        nearPath = "";

        String strPath = getPathBetween(edge1V,  edge2V);
        ArrayList<String> path = new ArrayList<String>(Arrays.asList(strPath.split(",")));
        if(path.get(0).equals("")){
            path = new ArrayList<String>();
        }
        path.add(0, edge1V);
        path.add(edge2V);
        nearPath = "";

        // 1. Check begin of the path :
        if (path.size()>=2) {
            // edge1.v1 --> edge1.v2 is in path
            if (path.get(0).equals(edge1.v1.tag) && path.size() >= 2) {
                if (path.get(1).equals(edge1.v2.tag)) {
                    path.remove(0);
                }
            }

            // edge1.v2 --> edge1.v1 is in path
            if (path.get(0).equals(edge1.v2.tag) && path.size() >= 2) {
                if (path.get(1).equals(edge1.v1.tag)) {
                    path.remove(0);
                }
            }

            // edge2.v1 --> edge2.v2 is in path
            if (path.get(0).equals(edge2.v1.tag) && path.size() >= 2) {
                if (path.get(1).equals(edge2.v2.tag)) {
                    path.remove(0);
                }
            }

            // edge2.v2 --> edge2.v1 is in path
            if (path.get(0).equals(edge2.v2.tag) && path.size() >= 2) {
                if (path.get(1).equals(edge2.v1.tag)) {
                    path.remove(0);
                }
            }
        }

        // 2. Check end of the path :
        int last = path.size()-1;
        int beforeLast = path.size()-2;

        if (path.size()>=2) {
            // edge1.v1 --> edge1.v2 is in path
            if (path.get(beforeLast).equals(edge1.v1.tag) && path.size() >= 2) {
                if (path.get(last).equals(edge1.v2.tag)) {
                    path.remove(last);
                }
            }

            // edge1.v2 --> edge1.v1 is in path
            if (path.get(beforeLast).equals(edge1.v2.tag) && path.size() >= 2) {
                if (path.get(last).equals(edge1.v1.tag)) {
                    path.remove(last);
                }
            }

            // edge2.v1 --> edge2.v2 is in path
            if (path.get(beforeLast).equals(edge2.v1.tag) && path.size() >= 2) {
                if (path.get(last).equals(edge2.v2.tag)) {
                    path.remove(last);
                }
            }

            // edge2.v2 --> edge2.v1 is in path
            if (path.get(beforeLast).equals(edge2.v2.tag) && path.size() >= 2) {
                if (path.get(last).equals(edge2.v1.tag)) {
                    path.remove(last);
                }
            }
        }

        return convertUsingStringBuilder(path);
    }


    // Use instead of String.join(",",list)
    public static String convertUsingStringBuilder(List<String> names)
    {
        StringBuilder namesStr = new StringBuilder();
        for(String name : names)
        {
            namesStr = namesStr.length() > 0 ? namesStr.append(",").append(name) : namesStr.append(name);
        }
        return namesStr.toString();
    }


    //work on nearPath String
    private void path(int q, int r) {

        if (path[q][r] != 0) {
            path(q, path[q][r]);
            if (nearPath.length() != 0) {
                nearPath += "," + path[q][r];
            } else {
                nearPath += String.valueOf(path[q][r]);
            }


            path(path[q][r], r);
        }
    }

    //floyd algorithm
    private void makePaths() {
        makeVertex();

        path = new int[size][size];
        for (int i = 1; i < size; i++)
            for (int j = 1; j < size; j++)
                path[i][j] = 0;

        for (int k = 1; k < size; k++)
            for (int i = 1; i < size; i++)
                for (int j = 1; j < size; j++)
                    if (vertex[i][k] + vertex[k][j] < vertex[i][j]) {

                        path[i][j] = k;

                        vertex[i][j] = vertex[i][k] + vertex[k][j];
                    }
    }

    //make a matrix for floyd algorithm
    private void makeVertex() {
        size = constOfMap.vertexOfGraph.size();
        size++;
        vertex = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                vertex[i][j] = 20;
                if (i == j) {
                    vertex[i][j] = 0;
                }
            }
        }

        String[] edge = lEdges.split(",");
        for (int i = 0; i < edges.size(); i++) {
            String[] ver = edge[i].split("-");
            int j = Integer.valueOf(ver[0]);
            int k = Integer.valueOf(ver[1]);
            vertex[j][k] = 1;
            vertex[k][j] = 1;

        }


    }


    public ArrayList<ArrayList<Point>> getPath(String srcPoint, String dstPoint){
        ConstOfMap constOfMap = new ConstOfMap();
        ArrayList<ArrayList<Point>> resPath = new ArrayList<ArrayList<Point>>();
        ArrayList<Point> tempLine = new ArrayList<Point>();

        try {

            // We relocate the dst and src point if they are one of vertexes accidently, because our routing algorithm doesn't work in these situations
            if (constOfMap.isGraphVertex(srcPoint) != null){
                String[] srcPointStrList = srcPoint.split(",");
                float xTemp = Float.valueOf(srcPointStrList[0]) + ConstOfMap.epsilon;
                float yTemp = Float.valueOf(srcPointStrList[1]) + ConstOfMap.epsilon;
                srcPoint = xTemp+","+yTemp;
            }
            if (constOfMap.isGraphVertex(dstPoint) != null){
                String[] dstPointStrList = dstPoint.split(",");
                float xTemp = Float.valueOf(dstPointStrList[0]) + ConstOfMap.epsilon;
                float yTemp = Float.valueOf(dstPointStrList[1]) + ConstOfMap.epsilon;
                dstPoint = xTemp+","+yTemp;
            }

            // Routing :
            // Find closet edges
            Edge srcEdge = this.findNearEdge(srcPoint); // nearest edge to the source point
            Edge dstEdge = this.findNearEdge(dstPoint); // nearest edge to the destination point

            String srcEdgePoint = srcEdge.pointOnLineImage(srcPoint); // find the nearest point on the nearest edge to the source
            String dstEdgePoint = dstEdge.pointOnLineImage(dstPoint); // find the nearest point on the nearest edge to the destination

            String srcEdgeClosestVertex = srcEdge.nearVertex(srcEdgePoint); // find closet vertex of the nearest edge to srcEdgePoint
            String dstEdgeClosestVertex = dstEdge.nearVertex(dstEdgePoint); // find closet vertex of the nearest edge to dstEdgePoint

            // Find tempPath between two edges according to the closest vetexes
            String strPath = this.getPathBetweenEdges(dstEdgeClosestVertex, dstEdge, srcEdgeClosestVertex, srcEdge);
            String[] tempPath = strPath.split(",");

            // Draw the tempPath
            tempLine = new ArrayList<Point>();
            tempLine.add(new Point(srcPoint));
            tempLine.add(new Point(srcEdgePoint));
            resPath.add(tempLine);


            tempLine = new ArrayList<Point>();
            tempLine.add(new Point(dstPoint));
            tempLine.add(new Point(dstEdgePoint));
            resPath.add(tempLine);

//            webViewManager.drawLine(srcPoint, srcEdgePoint);
//            webViewManager.drawLine(dstPoint, dstEdgePoint);

            String lastVertex = tempPath[0];
            for (int i = 1; i < tempPath.length; i++) {
                tempLine = new ArrayList<Point>();
                tempLine.add(new Point(constOfMap.vertexOfGraph.get(lastVertex)));
                tempLine.add(new Point(constOfMap.vertexOfGraph.get(tempPath[i])));
                resPath.add(tempLine);


//                webViewManager.drawLine(constOfMap.vertexOfGraph.get(lastVertex)
//                        , constOfMap.vertexOfGraph.get(tempPath[i]));
                lastVertex = tempPath[i];
            }

            tempLine = new ArrayList<Point>();
            tempLine.add(new Point(dstEdgePoint));
            tempLine.add(new Point(constOfMap.vertexOfGraph.get(tempPath[0])));
            resPath.add(tempLine);


            tempLine = new ArrayList<Point>();
            tempLine.add(new Point(srcEdgePoint));
            tempLine.add(new Point(constOfMap.vertexOfGraph.get(tempPath[tempPath.length-1])));
            resPath.add(tempLine);


//            webViewManager.drawLine(dstEdgePoint, constOfMap.vertexOfGraph.get(tempPath[0]));
//            webViewManager.drawLine(srcEdgePoint, constOfMap.vertexOfGraph.get(tempPath[tempPath.length-1]));


            if (tempPath[0].equals("")) {

                tempLine = new ArrayList<Point>();
                tempLine.add(new Point(constOfMap.vertexOfGraph.get(dstEdgeClosestVertex)));
                tempLine.add(new Point(constOfMap.vertexOfGraph.get(srcEdgeClosestVertex)));
                resPath.add(tempLine);

//                webViewManager.drawLine(constOfMap.vertexOfGraph.get(dstEdgeClosestVertex)
//                        , constOfMap.vertexOfGraph.get(srcEdgeClosestVertex));
            }

            return resPath;

        } catch (RuntimeException e) {
            Log.e("error", e.toString());
            return null;
        }
    }

}