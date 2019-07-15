package ir.parsiod.NavigationInTheBuilding.map.Objects;

import java.util.ArrayList;
import java.util.List;

public   class Graph {
    public List<Edge> getEdges() {
        return edges;
    }

    private List<Edge> edges ;

    public Graph() {
        this.edges = new ArrayList<>();
    }

    public void addEdge(String ver1, String ver2){
        edges.add(new Edge().setVertexs(ver1,ver2));
        edges.size();

    }

    public Edge findNearEdge(String point){

        if(edges.size()>0){
            Edge near =null;
            float min =152555;

            for(int i=0;i<edges.size();i++){
                Edge A=edges.get(i);
                A.toString();
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






}