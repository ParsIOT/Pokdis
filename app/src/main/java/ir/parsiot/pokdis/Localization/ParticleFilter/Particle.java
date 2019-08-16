package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ir.parsiot.pokdis.Localization.Beacon.BLEdevice;
import ir.parsiot.pokdis.map.MapConsts;

import static ir.parsiot.pokdis.Constants.Constants.PROXIMITY_DISTANCE;
import static ir.parsiot.pokdis.Constants.Constants.PROXIMITY_PARTICLE_FACTOR;
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

    public Particle(){ // Note: Don't use it for particle creation, It's just for presenting average of particles
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

//    public void updateProbs(HashMap<String, Double> measurements) {
//        for(String landmarkName: measurements.keySet()){
//            Double[] landmarkLoc = this.landmarks.get(landmarkName);
//            Double dist = (Double) ParticleFilterMath.distance(x, y,
//                    landmarkLoc[0],
//                    landmarkLoc[1]);
//            this.probability *= (float)ParticleFilterMath.Gaussian(dist, Snoise, measurements.get(landmarkName));
//            Log.e("","");
//        }
//    }

//    public void updateProbs(ArrayList<BLEdevice> importantNearBeacons) {
//
//        BLEdevice firstNearBeacon = importantNearBeacons.get(0);
//
//
//        Double[] landmarkLoc = this.landmarks.get(firstNearBeacon.getMac());
//        Double dist = (Double) ParticleFilterMath.distance(x, y,
//                landmarkLoc[0],
//                landmarkLoc[1]);
//        if (dist < PROXIMITY_DISTANCE * MapConsts.scale) {
//            this.probability *= PROXIMITY_PARTICLE_FACTOR;
//        }
//    }

    public void updateProbs(ArrayList<BLEdevice> importantNearBeacons) {

        BLEdevice firstNearBeacon = importantNearBeacons.get(0);


        Double[] landmarkLoc = this.landmarks.get(firstNearBeacon.getMac());
        Double dist = (Double) ParticleFilterMath.distance(x, y,
                landmarkLoc[0],
                landmarkLoc[1]);
        try {
            this.probability *= 1.0 / dist;
        }catch (ArithmeticException ae){
            if(dist == 0d){
                this.probability *= 1.0;
            }else{
                Log.e("UpdateProbs :",ae.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "[x=" + x + " y=" + y + " h=" + Math.toDegrees(h) + " prob=" + probability + "]";
    }
}
