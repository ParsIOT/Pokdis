package ir.parsiod.NavigationInTheBuilding.map.Objects;

import java.util.ArrayList;
import java.util.List;

public   class Graph {
    private List<Edge> edges ;

    public Graph() {
        this.edges = new ArrayList<>();
    }

    public void addEdge(String ver1, String ver2){
        edges.add(new Edge().setVertexs(ver1,ver2));

    }

    public Edge findNearEdge(String point){

        if(edges.size()>0){
            Edge near = edges.get(0);
            for(int i=1;i<edges.size();i++){
                if(near.distanceFromTheLine(point)
                        >edges.get(i).distanceFromTheLine(point)){
                    near = edges.get(i);
                }

            }
            return near;
        }



        return  null;
    }






}