package ir.parsiod.NavigationInTheBuilding.map;

import java.util.ArrayList;
import java.util.List;

import ir.parsiod.NavigationInTheBuilding.map.Objects.Graph;
import ir.parsiod.NavigationInTheBuilding.map.Objects.Objects;

public class ObjectLocation {
    List<Objects>objects ;

    public ObjectLocation() {
        objects = new ArrayList<>();

        Objects shelf1 = new Objects();
        Graph shelf1Graph = new Graph();

        shelf1Graph.addEdge("508.38884353637695,-15.055652618408203","508.38884353637695,1088.9443473815918");
        shelf1Graph.addEdge("-263.61115646362305,1088.9443473815918","508.38884353637695,1088.9443473815918");
        shelf1Graph.addEdge("-263.61115646362305,1088.9443473815918","-263.61115646362305,-15.055652618408203");
        shelf1Graph.addEdge("508.38884353637695,-15.055652618408203","-263.61115646362305,-15.055652618408203");

        shelf1.setGraph(shelf1Graph);

        objects.add(shelf1);


    }



    public List<Objects> getObjects() {
        return objects;
    }
}
