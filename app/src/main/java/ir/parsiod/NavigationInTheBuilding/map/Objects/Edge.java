package ir.parsiod.NavigationInTheBuilding.map.Objects;

public class Edge{
    float[] vertex1  ;
    float[] vertex2 ;
    float m=0;
    float c =0;

    public Edge() {
        this.vertex1  =new float[2];
        this.vertex2 =new float[2];
    }

    public Edge setVertexs(String ver1, String ver2){
        Edge edge = new Edge();
        String [] verte1 = ver1.split(",");
        String [] verte2 = ver2.split(",");

        vertex1[0] =Float.valueOf(verte1[0]);
        vertex1[1]=Float.valueOf(verte1[1]);



        vertex2[0] =Float.valueOf(verte2[0]);
        vertex2[0]=Float.valueOf(verte2[1]);
        //line is y+mx+c =0
        if(vertex1[0]-vertex2[0]!=0){
            m = (vertex2[1]-vertex1[1])/(vertex1[0]-vertex2[0]);//line slope

            c = m*vertex1[0] - vertex2[1];
        }


        return this;
    }


    public float distanceFromTheLine (String point){
        String [] loc = point.split(",");

        float [] locOfPoint=new float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);


        float distance = (float) ((m*locOfPoint[0]+locOfPoint[1]+c)/Math.pow(Math.pow(m,2)+1,1/2));

        return distance;


    }

    public float [] pointOnLineImage(String point){

        String [] loc = point.split(",");
        Float [] locOfPoint=new Float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);

        float k = (float) ((m*locOfPoint[0] + locOfPoint[1] +c)/(Math.pow(m,2)+1));

        float [] pointOnLine= new float[2];

        pointOnLine[0] = locOfPoint[0]- m*k;
        pointOnLine[1] = locOfPoint[1]-k;

        return pointOnLine;
    }



    @Override
    public String toString() {
      return vertex1[0]+","+vertex1[1]+"|"+  vertex2[0]+","+vertex2[1];
    }
}
