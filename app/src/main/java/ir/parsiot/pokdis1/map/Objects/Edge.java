package ir.parsiot.pokdis1.map.Objects;

public class Edge{
    Vertex v1;
    Vertex v2;

    float m=0;

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }


    float c =0;

    public Edge() {
        v1 = new Vertex();
        v2 = new Vertex();
    }

    public Edge setVertexs(String ver1,String tag1, String ver2,String tag2){

        String [] verte1 = ver1.split(",");
        String [] verte2 = ver2.split(",");

        v1.x =Float.valueOf(verte1[0]);
        v1.y=Float.valueOf(verte1[1]);
        v1.tag =tag1;


        v2.x =Float.valueOf(verte2[0]);
        v2.y=Float.valueOf(verte2[1]);
        v2.tag =tag2;
        //line is y+mx+c =0
        if(v1.x-v2.x!=0){
            m = (v2.y-v1.y)/(v2.x-v1.x);//line slope

            c = (m*v1.x)-v1.y ;
        }


        return this;
    }

//for find near EDGE
    public float distanceFromTheLine (String point){
     /*   String [] loc = point.split(",");

        float [] locOfPoint=new float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);


        float distance = (float) ( Math.abs(m*locOfPoint[0]+locOfPoint[1]+c)/Math.sqrt(Math.pow(m,2)+1));
*/      Vertex  center = new Vertex();
        center.x = (v1.x+v2.x)/2;
        center.y=   (v1.y+v2.y)/2;
        float distance = center.distanceFromThis(point);
        return distance;


    }

    //TODO THIS FUNCTION NOT TRUE WORKING
    public String pointOnLineImage(String point){
        float a = -m;

        String [] loc = point.split(",");
        Float [] locOfPoint=new Float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);

        float k = (float) ((a*locOfPoint[0] + locOfPoint[1] +c)/(Math.pow(a,2)+1));

        float [] pointOnLine= new float[2];

        pointOnLine[0] = locOfPoint[0]- a*k;
        pointOnLine[1] = locOfPoint[1]-k;

        String tpoint =pointOnLine[0]+","+pointOnLine[1];

        return tpoint;
    }

    //NEAR VERTEX ON A edge
    public String nearVertex(String point){
       if(checkOnLine(point)){
          if(v1.distanceFromThis(point)>v2.distanceFromThis(point)){
            return v2.tag;
          }else {
              return v1.tag;
          }

       }else {
           point = pointOnLineImage(point);
           if(v1.distanceFromThis(point)>v2.distanceFromThis(point)){
               return v2.tag;
           }else {
               return v1.tag;
           }


       }

    }
//check a point is on line or not
    public Boolean checkOnLine(String point){
        String [] loc = point.split(",");
        Float [] locOfPoint=new Float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);

        int a = (int) (locOfPoint[1]+m*locOfPoint[0]+c);

        if(a==0){
            return true;
        }

        return false;
    }


    @Override
    public String toString() {

        return v1.tag+"|"+ v2.tag;
    }
}
