package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.map.MapConsts;

public class ParticleFilterRunner {
    ParticleFilter filter;
    private int ShowParticleCounter = 0;
    private int SHOW_PARTICLE_COUNTER_THRESHOLD= 50;

    HashMap<String, Double[]> beaconCoordinates = BeaconLocations.beaconCoordinates;
    final int NUM_PARTICLES = 2;
    double Fnoise=0.05d, Tnoise=0.05d, Snoise=5d;
    ArrayList<Double> initScatterFactor = new ArrayList<Double>(){{
        add(400d);
        add(400d);
        add(40d);
    }
    };

    ArrayList<Double> curState = new ArrayList<Double>();
    ArrayList<Double> lastMotionState = new ArrayList<Double>();
    ParticleFilterCallback callback;

    public ParticleFilterRunner(ParticleFilterCallback callback) {
        this.callback = callback;
        lastMotionState = MapConsts.getInitLocationDouble();
        lastMotionState.add(MapConsts.initHeading);

        filter = new ParticleFilter(NUM_PARTICLES, initScatterFactor, beaconCoordinates, MapConsts.MAP_WIDTH, MapConsts.MAP_HEIGHT);
        filter.setNoise(Fnoise, Tnoise, Snoise);
        try {
            filter.createParticles(lastMotionState);
        }catch (Exception ex){
            Log.e("ParticleFilter",ex.getMessage());
        }

        // Todo: Run motionDna and beaconscanner services
    }

    public void onSensedMotionData(ArrayList<Double> newMotionState){
//        Log.e("ParticleFilter", newMotionState.toString());
        Double dx = newMotionState.get(0) - lastMotionState.get(0);
        Double dy = newMotionState.get(1) - lastMotionState.get(1);
        Double dh = newMotionState.get(2) - lastMotionState.get(2);
        lastMotionState = newMotionState;

        filter.move(dx, dy, dh); // Todo : This function may takFe times, So we should run particlefilterRunner on a thread

        Particle resParticle = filter.getAverageParticle();

        curState.clear();
        curState.add(resParticle.x);
        curState.add(resParticle.y);
        curState.add(resParticle.h);

        Log.e("particleFilter", curState.toString());
        callback.onLocationUpdate(curState.get(0), curState.get(1), curState.get(2));

        if (ShowParticleCounter > SHOW_PARTICLE_COUNTER_THRESHOLD){
            callback.onParticleLocationUpdate(filter.getParticles());
            ShowParticleCounter = -1;
        }
        ShowParticleCounter++;
    }

    public void onSensedLandmarkData(ArrayList<String> newMotionstate){

    }


    public interface ParticleFilterCallback {
        public void onLocationUpdate(Double x, Double y, Double h);
        public void onParticleLocationUpdate(ArrayList<ArrayList<Double>> particles);
    }


}
