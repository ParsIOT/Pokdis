package ir.parsiot.pokdis1.map.Objects;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis1.map.ConstOfMap;

public   class Graph {

    private int [][] vertex;
    private List<Vertex>vertices;
    int [][] path;
    private List<Edge> edges ;
    private String nearPath="";
    private int size;
    //private int sizeOfedges;
    String lEdges="";
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
    public void addEdge(String ver1, String ver2){
        edges.add(new Edge().setVertexs(constOfMap.vertexOfGraph.get(ver1),ver1
                ,constOfMap.vertexOfGraph.get(ver2),ver2));


        if(lEdges.length()<1){
            lEdges +=ver1+"-"+ver2;
        }else {
            lEdges +=","+ver1+"-"+ver2;
        }

    }
//search in edges list for find near edge
    public Edge findNearEdge(String point){

        if(edges.size()>0){
            Edge near =null;
            float min =edges.get(0).distanceFromTheLine(point);

            for(int i=0;i<edges.size();i++){
                Edge A=edges.get(i);
                A.toString();
                if(edges.get(i).checkOnLine(point)){
                    return edges.get(i);
                }
                if(min
                        >edges.get(i).distanceFromTheLine(point)){
                    min = edges.get(i).distanceFromTheLine(point);

                    near =edges.get(i);
                }

            }


            return near;
        }



        return  null;
    }

    //getPathBetween to vertex on
   public String  getPathBetween(String ver1,String ver2){
        nearPath ="";

        makePaths();

        path(Integer.valueOf(ver1),Integer.valueOf(ver2));

       return nearPath;

   }

//work on nearPath String
   private void path (int q,int r) {

        if (path[ q ][ r ] !=0){
        path (q, path[q] [r]);
        if(nearPath.length() !=0){
            nearPath +=","+path[q][r];
        }else {
            nearPath+=String.valueOf(path[q][r]);
        }


        path (path[ q ] [ r ], r);
   }
    }

    //floyd algorithm
   private void makePaths () {
        makeVertex();

        path = new int[size][size];
        for(int i = 1; i < size; i++)
            for (int j = 1; j < size; j++)
                path[i][j] = 0;

        for (int k = 1; k < size; k++)
            for(int i = 1; i < size; i++)
                for(int j = 1; j < size; j++)
                    if (vertex[i][k] + vertex[k][j] < vertex[i][j]) {

                           path[i][j] = k;

                        vertex[i][j] = vertex[i][k] + vertex[k][j]; }
    }

    //make a matrix for floyd algorithm
    private void makeVertex(){
      size = constOfMap.vertexOfGraph.size();
        size++;
        vertex = new int [size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                vertex[i][j] =20;
                if(i==j){
                    vertex[i][j] =0;
                }
            }
        }

        String [] edge = lEdges.split(",");
        for(int i=0;i<edges.size();i++){
            String [] ver = edge[i].split("-");
            int j = Integer.valueOf(ver[0]);
            int k = Integer.valueOf(ver[1]);
            vertex[j][k]=1;
            vertex[k][j]=1;

        }


    }




}