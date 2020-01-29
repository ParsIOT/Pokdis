package ir.parsiot.pokdis.map;

import ir.parsiot.pokdis.map.Objects.Graph;


public class GraphBuilder {
   public Graph graph;

    public GraphBuilder() {

        // Route:
        graph = new Graph();


        graph.addEdge("1","2");
        graph.addEdge("2","3");
        graph.addEdge("3","4");

        graph.addEdge("9","10");
        graph.addEdge("10","11");
        graph.addEdge("11","12");

        graph.addEdge("1","5");
        graph.addEdge("5","9");

        graph.addEdge("2","6");
        graph.addEdge("6","10");

        graph.addEdge("3","7");
        graph.addEdge("7","11");

        graph.addEdge("4","8");
        graph.addEdge("8","12");

    }



}
