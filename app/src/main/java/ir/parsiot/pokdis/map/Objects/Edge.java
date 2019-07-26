package ir.parsiot.pokdis.map.Objects;

public class Edge {
    Vertex v1;
    Vertex v2;

    float m = 0;

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }


    float c = 0;

    public Edge() {
        v1 = new Vertex();
        v2 = new Vertex();
    }

    public Edge setVertexs(String ver1, String tag1, String ver2, String tag2) {

        String[] verte1 = ver1.split(",");
        String[] verte2 = ver2.split(",");

        v1.x = Float.valueOf(verte1[0]);
        v1.y = Float.valueOf(verte1[1]);
        v1.tag = tag1;


        v2.x = Float.valueOf(verte2[0]);
        v2.y = Float.valueOf(verte2[1]);
        v2.tag = tag2;
        //line is y+mx+c =0
        if (v1.x - v2.x != 0) {
            m = (v2.y - v1.y) / (v2.x - v1.x);//line slope

            c = (m * v1.x) - v1.y;
        }


        return this;
    }

////for find near EDGE
//    public float distanceFromTheLine (String point){
//     /*   String [] loc = point.split(",");
//
//        float [] locOfPoint=new float[2];
//        locOfPoint[0] = Float.valueOf(loc[0]);
//        locOfPoint[1] = Float.valueOf(loc[1]);
//
//
//        float distance = (float) ( Math.abs(m*locOfPoint[0]+locOfPoint[1]+c)/Math.sqrt(Math.pow(m,2)+1));
//*/      Vertex  center = new Vertex();
//        center.x = (v1.x+v2.x)/2;
//        center.y=   (v1.y+v2.y)/2;
//
//
//
//
//        abs((xPoint-v2.x)*(v2.y-v1.y) - (v2.x-v1.x)*(yPoint-v2.y)) / np.sqrt(np.square(xPoint-v2.x) + np.square(yPoint-v2.y))
//
//        float distance = center.distanceFromThis(point);
//        return distance;
//
//
//    }
//

//    public static float distBetween(float x, float y, float x1, float y1) {
//        float xx = x1 - x;
//        float yy = y1 - y;
//
//        return (float) Math.sqrt(xx * xx + yy * yy);
//    }
//
//    public float distanceFromTheLine(String point) {
//        String[] loc = point.split(",");
//        Float[] locOfPoint = new Float[2];
//        float x = Float.valueOf(loc[0]);
//        float y = Float.valueOf(loc[1]);
//        float x1,x2,y1,y2;
//        x1 = v1.x;
//        y1 = v1.y;
//        x2 = v2.x;
//        y2 = v2.y;
//
//
//
//        float AB = distBetween(x, y, x1, y1);
//        float BC = distBetween(x1, y1, x2, y2);
//        float AC = distBetween(x, y, x2, y2);
//
//        // Heron's formula
//        float s = (AB + BC + AC) / 2;
//        float area = (float) Math.sqrt(s * (s - AB) * (s - BC) * (s - AC));
//
//        // but also area == (BC * AD) / 2
//        // BC * AD == 2 * area
//        // AD == (2 * area) / BC
//        // TODO: check if BC == 0
//        float AD = (2 * area) / BC;
//        return AD;
//    }

    public float distanceFromTheLine(String point) {
        String[] loc = point.split(",");
        Float[] locOfPoint = new Float[2];
        float x3 = Float.valueOf(loc[0]);
        float y3 = Float.valueOf(loc[1]);
        float x1, x2, y1, y2;
        x1 = v1.x;
        y1 = v1.y;
        x2 = v2.x;
        y2 = v2.y;
        float px = x2 - x1;
        float py = y2 - y1;
        float temp = (px * px) + (py * py);
        float u = ((x3 - x1) * px + (y3 - y1) * py) / (temp);
        if (u > 1) {
            u = 1;
        } else if (u < 0) {
            u = 0;
        }
        float x = x1 + u * px;
        float y = y1 + u * py;

        float dx = x - x3;
        float dy = y - y3;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        return dist;

    }

//    public float distanceFromTheLine(String point) {
//        String[] loc = point.split(",");
//        Float[] locOfPoint = new Float[2];
//        float xPoint = Float.valueOf(loc[0]);
//        float yPoint = Float.valueOf(loc[1]);
//
//        float A = xPoint - v1.x; // position of point rel one end of line
//        float B = yPoint - v1.y;
//        float C = v2.x - v1.x; // vector along line
//        float D = v2.y - v1.y;
//        float E = -D; // orthogonal vector
//        float F = C;
//
//        double dot = A * E + B * F;
//        double len_sq = E * E + F * F;
//
//        return (float) (Math.abs(dot) / Math.sqrt(len_sq));
//    }


//    //TODO: This function doesn't work correctly
//    public String pointOnLineImage(String point) {
//        float a = -m;
//
//        String[] loc = point.split(",");
//        Float[] locOfPoint = new Float[2];
//        locOfPoint[0] = Float.valueOf(loc[0]);
//        locOfPoint[1] = Float.valueOf(loc[1]);
//
//        float k = (float) ((a * locOfPoint[0] + locOfPoint[1] + c) / (Math.pow(a, 2) + 1));
//
//        float[] pointOnLine = new float[2];
//
//        pointOnLine[0] = locOfPoint[0] - a * k;
//        pointOnLine[1] = locOfPoint[1] - k;
//
//        String tpoint = pointOnLine[0] + "," + pointOnLine[1];
//
//        return tpoint;
//    }

    public String pointOnLineImage(String point)//int sx1, int sy1, int sx2, int sy2, int px, int py)
    {
        String[] loc = point.split(",");
        Float[] locOfPoint = new Float[2];
        float xPoint = Float.valueOf(loc[0]);
        float yPoint = Float.valueOf(loc[1]);

        float resX, resY;

        double xDelta = v2.x - v1.x;
        double yDelta = v2.y - v1.y;

        if ((xDelta == 0) && (yDelta == 0)) {
            throw new IllegalArgumentException("Segment start equals segment end");
        }

        double u = ((xPoint - v1.x) * xDelta + (yPoint - v1.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        if (u < 0) {
            resX = v1.x;
            resY = v1.y;
        } else if (u > 1) {
            resX = v2.x;
            resY = v2.y;
        } else {
            resX = Math.round(v1.x + u * xDelta);
            resY = Math.round(v1.y + u * yDelta);
        }

        return resX + "," + resY;
    }

    //NEAR VERTEX ON A edge
    public String nearVertex(String point) {
        if (checkOnLine(point)) {
            if (v1.distanceFromThis(point) > v2.distanceFromThis(point)) {
                return v2.tag;
            } else {
                return v1.tag;
            }

        } else {
            point = pointOnLineImage(point);
            if (v1.distanceFromThis(point) > v2.distanceFromThis(point)) {
                return v2.tag;
            } else {
                return v1.tag;
            }


        }

    }

    //check a point is on line or not
    public Boolean checkOnLine(String point) { //Todo: It doesn't work correctly(e.g It doesn't work on the vertexes of the edge)
        String[] loc = point.split(",");
        Float[] locOfPoint = new Float[2];
        locOfPoint[0] = Float.valueOf(loc[0]);
        locOfPoint[1] = Float.valueOf(loc[1]);

        int a = (int) (locOfPoint[1] + m * locOfPoint[0] + c);

        if (a == 0) {
            return true;
        }

        return false;
    }


    @Override
    public String toString() {

        return v1.tag + "|" + v2.tag;
    }
}
