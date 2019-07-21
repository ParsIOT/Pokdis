package ir.parsiot.pokdis1.map;

import ir.parsiot.pokdis1.map.Objects.Graph;


public class GraphBuilder {
   public Graph graph;

    public GraphBuilder() {


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
