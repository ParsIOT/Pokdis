package ir.parsiot.pokdis.map.WallGraph;

public class RectObstacle {
    Double[] leftTopDot; // We use the leftTop and rightBottom dots to describe the rectangle
    Double[] rightBottomDot;

    Double[] leftBottomDot;
    Double[] rightTopDot;

    public RectObstacle(Double[] leftTopDot, Double[] rightBottomDot) {
        this.leftTopDot = leftTopDot;
        this.rightBottomDot = rightBottomDot;

        leftBottomDot = new Double[]{leftTopDot[0], rightBottomDot[1]};
        rightTopDot = new Double[]{rightBottomDot[0], leftTopDot[1]};
    }

    public Double[][][] getWalls() {
        /*
        Return walls, that each wall contains 2 dot and each dot contains 2 dim(x , y)
         */
        Double[][][] walls = new Double[][][]{
                // Lines or walls
                new Double[][]{
                        // Dots or vertexes
                        leftTopDot,
                        rightTopDot
                },
                new Double[][]{
                        leftTopDot,
                        leftBottomDot
                },
                new Double[][]{
                        rightBottomDot,
                        rightTopDot
                },
                new Double[][]{
                        rightBottomDot,
                        leftBottomDot
                },
        };

        return walls;
    }

//    public boolean inArea(Double[] dot){
//        /*
//        Check that the dot is in the rectObstacle or not
//         */
//        return (leftTopDot[0] <= dot[0] && dot[0] <= rightBottomDot[0]) && (rightBottomDot[1] <= dot[1] && dot[1] <= leftTopDot[1]);
//    }

    public boolean inArea(Double[] dot){
        /*
        Check that the dot is in the rectObstacle or not
         */

        Double maxX,minX,maxY,minY;
        if (leftTopDot[0] < rightBottomDot[0] ){
            maxX = rightBottomDot[0];
            minX = leftTopDot[0];
        }else{
            maxX = leftTopDot[0];
            minX = rightBottomDot[0];
        }

        if (leftTopDot[1] < rightBottomDot[1] ){
            maxY = rightBottomDot[1];
            minY = leftTopDot[1];
        }else{
            maxY = leftTopDot[1];
            minY = rightBottomDot[1];
        }

        return (minX <= dot[0] && dot[0] <= maxX) && (minY <= dot[1] && dot[1] <= maxY);
    }
}
