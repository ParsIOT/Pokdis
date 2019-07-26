package ir.parsiot.pokdis.map.Objects;

public class Point {
    public float x;
    public float y;

    public Point(String tag, float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Point(String xyStr) {
        String [] xyStrList = xyStr.split(",");
        this.x = Float.valueOf(xyStrList[0]);
        this.y  = Float.valueOf(xyStrList[1]);
    }
    public Point() {
        this.x = 0;
        this.y = 0;
    }
    public float distanceFromThis(String point){
        String [] loc = point.split(",");
        Float [] locOfPoint=new Float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);

        float distance = (float) Math.sqrt(Math.pow(x-locOfPoint[0],2)+Math.pow(y-locOfPoint[1],2));
        return distance;
    }

    public float distanceFromThis(Point p){
        float distance = (float) Math.sqrt(Math.pow(x-p.x,2)+Math.pow(y-p.y,2));
        return distance;
    }


    @Override
    public String toString() {
        return x+","+y;
    }

}
