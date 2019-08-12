package ir.parsiot.pokdis.Localization.ParticleFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ir.parsiot.pokdis.map.MapConsts;

import static ir.parsiot.pokdis.Localization.MotionDna.Utils.Convert2zeroto360;

public class Particle {

    public Double Fnoise, Tnoise, Snoise;
    public Double x, y, h;
    public float probability = 0;
    public HashMap<String, Double[]> landmarks;

    private static MapConsts mapConsts = new MapConsts();
    Random random = new Random();

    public Particle(ArrayList<Double> initState, float probability, HashMap<String, Double[]> landmarks, Double Fnoise, Double Tnoise, Double Snoise) {
        this.landmarks = landmarks;
        this.probability = probability;
        this.Fnoise = Fnoise;
        this.Tnoise = Tnoise;
        this.Snoise = Snoise;
//        x = random.nextDouble() * width;
//        y = random.nextDouble() * height;
//        h = random.nextDouble() * 360d;
        x = initState.get(0);
        y = initState.get(1);
        h = initState.get(2);
    }

    public Double get_carteian_heading(){
        return Convert2zeroto360(Convert2zeroto360(-1 * 180));
    }

    public Particle(){
        this.landmarks = new HashMap<String, Double[]>();
        this.Fnoise = 0d;
        this.Tnoise = 0d;
        this.Snoise = 0d;
        x = 0d;
        y = 0d;
        h = 0d;
    }

    public void setNoise(Double Fnoise, Double Tnoise, Double Snoise) {
        this.Fnoise = Fnoise;
        this.Tnoise = Tnoise;
        this.Snoise = Snoise;
    }

    public void set(Double x, Double y, Double h, float prob) throws Exception {
        this.x = x;
        this.y = y;
        this.h = h;
        this.probability = prob;
    }

//    public Double[] sense() {
//        Double[] ret = new Double[landmarks.length];
//
//        for(int i=0;i<landmarks.length;i++){
//            Double dist = (Double) ParticleFilterMath.distance(x, y,
//                    landmarks[i].x,
//                    landmarks[i].y);
//            ret[i] = dist + (Double)random.nextGaussian() * Snoise;
//        }
//        return ret;
//    }

//    public void move(Double turn, Double forward) throws Exception {
//        if(forward < 0) {
//            throw new Exception("Robot cannot move backwards");
//        }
//        h = h + turn + (Double)random.nextGaussian() * Tnoise;
//        h = Convert2zeroto360(h);
//
//        double dist = forward + random.nextGaussian() * Fnoise;
//
//        x += Math.cos(h) * dist;
//        y += Math.sin(h) * dist;
//        x = circle(x, (double)worldWidth);
//        y = circle(y, (double)worldHeight);
//    }

    public Double[][] move(Double dx, Double dy, Double dh) {
        h += dh + (Double) random.nextGaussian() * Tnoise;
        h = Convert2zeroto360(h);

        Double lastx = x;
        Double lasty = y;
        x += dx + random.nextGaussian() * Fnoise;
        y += dy + random.nextGaussian() * Fnoise;
        Double[][] collidedWall = mapConsts.wallGraph.getCollision(new Double[]{lasty, lastx}, new Double[]{y, x});
        return collidedWall;
    }

    public double measurementProb(Double[] measurement) {
        double prob = 1.0;
//        for(int i=0;i<landmarks.length;i++) {
//            Double dist = (Double) ParticleFilterMath.distance(x, y,
//                    landmarks[i].x,
//                    landmarks[i].y);
//            prob *= ParticleFilterMath.Gaussian(dist, Snoise, measurement[i]);
//        }
//
//        probability = prob;

        return prob;
    }

    @Override
    public String toString() {
        return "[x=" + x + " y=" + y + " h=" + Math.toDegrees(h) + " prob=" + probability + "]";
    }
}
