package ir.parsiot.pokdis.map.WallGraph;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class RectObstacleTest {
    RectObstacle rectObstacle;

    @Before
    public void setUp() throws Exception {
        Double[] leftTopDot = new Double[]{-100d,100d};
        Double[] rightBottomDot = new Double[]{100d,-100d};



        rectObstacle = new RectObstacle(leftTopDot , rightBottomDot);
    }

    @Test
    public void inArea() {
        Double[] dot1 = new Double[]{0d,0d};
        Double[] dot2 = new Double[]{-200d,50d};
        Double[] dot3 = new Double[]{-200d,-120d};

        assertEquals(true, rectObstacle.inArea(dot1));
        assertEquals(false, rectObstacle.inArea(dot2));
        assertEquals(false, rectObstacle.inArea(dot3));



        ArrayList<Double[][]> obstacleVertexes = new ArrayList<Double[][]>() {{
            add(new Double[][]{
                    new Double[]{265d, -250d},
                    new Double[]{165d, 205d},
            });
        }};

        ArrayList<RectObstacle> rectObstacles = new ArrayList<RectObstacle>();
        for (Double[][] obstacle : obstacleVertexes) {
            RectObstacle newRectObstacle = new RectObstacle(obstacle[0], obstacle[1]);
            rectObstacles.add(newRectObstacle);
        }

        Double[] xy = new Double[]{
                217d,-92d,
        };

        assertNotEquals(null, rectObstacle.inArea(xy));

    }
}