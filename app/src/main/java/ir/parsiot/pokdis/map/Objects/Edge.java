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
