package ir.parsiot.pokdis.map.WallGraph;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class WallGraphTest {
    public ArrayList<Double[][]> allWalls = new ArrayList<Double[][]>();
    public static WallGraph wallGraph;

    @Before
    public void setUp() throws Exception {

        allWalls = new ArrayList<Double[][]>() {{

            // Map borders
            add(new Double[][]{
                    new Double[]{-380d,400d},
                    new Double[]{-380d,-450d},
            });
            add(new Double[][]{
                    new Double[]{-380d,-450d},
                    new Double[]{340d,-450d},
            });
            add(new Double[][]{
                    new Double[]{-380d,400d},
                    new Double[]{380d,400d},
            });
            add(new Double[][]{
                    new Double[]{380d,400d},
                    new Double[]{340d,-450d},
            });

        }};
        Double[][][] allWallsDouble = allWalls.toArray(new Double[allWalls.size()][][]);
        wallGraph = new WallGraph(allWallsDouble);
    }

    @Test
    public void getCollision() {
        Double[] srcDot1 = new Double[]{260d,-260d};
        Double[] dstDot1 = new Double[]{588d,-310d};
        Double[][] collidedWall1 = wallGraph.getCollision(srcDot1, dstDot1);
        Double[][] expectedCollideWall1 = new Double[][]{
          new Double[]{380d,400d},
          new Double[]{340d,-450d},
        };
        assertArrayEquals(expectedCollideWall1, collidedWall1);


        Double[] srcDot2 = new Double[]{260d,-260d};
        Double[] dstDot2 = new Double[]{270d,-250d};
        Double[][] collidedWall2 = wallGraph.getCollision(srcDot2, dstDot2);
        Double[][] expectedCollideWall2 = null;
        assertArrayEquals(expectedCollideWall2, collidedWall2);
    }
}