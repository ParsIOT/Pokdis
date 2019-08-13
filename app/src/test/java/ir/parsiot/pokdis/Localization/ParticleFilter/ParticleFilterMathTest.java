package ir.parsiot.pokdis.Localization.ParticleFilter;

import org.junit.Test;

import static ir.parsiot.pokdis.Localization.ParticleFilter.ParticleFilterMath.Gaussian;
import static org.junit.Assert.*;

public class ParticleFilterMathTest {

    @Test
    public void gaussian() {

        double r = 12.5d * 1.1f;
        double res = Gaussian(243f,100f,360f);
        System.out.println(res);
    }
}