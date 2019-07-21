package ir.parsiot.pokdis1.map.Objects;

public class Vertex {
   public String tag;
   public float x;
   public float y;

    public Vertex(String tag, float x, float y) {
        this.tag = tag;
        this.x = x;
        this.y = y;
    }
    public Vertex() {
        String tag ="";
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


    @Override
    public String toString() {
        return x+","+y;
    }
}
