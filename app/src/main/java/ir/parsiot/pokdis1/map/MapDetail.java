package ir.parsiot.pokdis1.map;


import java.util.List;



/**
 * Created by hadi on 9/15/18.
 */

public class MapDetail {

    private String mapName;

    private String mapPath;

    private boolean success;

    private List<Integer> mapDimensions;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Integer> getMapDimensions() {
        return mapDimensions;
    }

    public void setMapDimensions(List<Integer> mapDimensions) {
        this.mapDimensions = mapDimensions;
    }


    @Override
    public String toString() {
        return "MapDetail{" +
                "mapName='" + mapName + '\'' +
                ", mapPath='" + getMapPath() + '\'' +
                ", success=" + success +
                ", mapDimensions=" + mapDimensions +
                '}';
    }
}
