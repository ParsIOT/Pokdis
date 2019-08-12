package ir.parsiot.pokdis.Localization.ParticleFilter;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ParticleFilterTest {

    @Test
    public void resample() {
        Random random = new Random();

        float num = (float) (random.nextFloat() / 100);
        System.out.println(num);
    }
}