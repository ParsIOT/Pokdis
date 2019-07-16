package ir.parsiod.NavigationInTheBuilding.map;

import java.util.ArrayList;
import java.util.List;

import ir.parsiod.NavigationInTheBuilding.map.Objects.Graph;
import ir.parsiod.NavigationInTheBuilding.map.Objects.Objects;

public class ObjectLocation {
   public Graph graph;

    public ObjectLocation() {


         graph = new Graph();


        graph.addEdge("1","2");
        graph.addEdge("3","2");
        graph.addEdge("3","4");

        graph.addEdge("6","5");
        graph.addEdge("6","7");

        graph.addEdge("8","7");
        graph.addEdge("8","4");
        graph.addEdge("2","9");
        graph.addEdge("6","9");

        graph.addEdge("3","10");
        graph.addEdge("7","10");

        graph.addEdge("11","1");
        graph.addEdge("11","5");
        graph.addEdge("12","4");
        graph.addEdge("12","8");






    }



}
