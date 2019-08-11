package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.map.MapConsts;

public class ParticleFilterRunner extends Thread {
    private static final String TAG = "ParticleFilterRunner";
    ParticleFilter filter;
    private int ShowParticleCounter = 0;
    private int SHOW_PARTICLE_COUNTER_THRESHOLD = 50;

    HashMap<String, Double[]> beaconCoordinates = BeaconLocations.beaconCoordinates;
    final int NUM_PARTICLES = 2;
    double Fnoise = 0.05d, Tnoise = 0.05d, Snoise = 5d;
    ArrayList<Double> initScatterFactor = new ArrayList<Double>() {
        {
            add(400d);
            add(400d);
            add(40d);
        }
    };

    private Object semaphore = new Object();
    private boolean mPaused = false;
    private volatile boolean isRunning = false;

    ArrayList<Double> curState = new ArrayList<Double>();
    ArrayList<Double> lastMotionState = new ArrayList<Double>();
    ArrayList<Double> newMotionState = new ArrayList<Double>();
    ParticleFilterCallback callback;

    public ParticleFilterRunner(ParticleFilterCallback callback) {
        this.callback = callback;
        lastMotionState = MapConsts.getInitLocationDouble();
        lastMotionState.add(MapConsts.initHeading);

        filter = new ParticleFilter(NUM_PARTICLES, initScatterFactor, beaconCoordinates, MapConsts.MAP_WIDTH, MapConsts.MAP_HEIGHT);
        filter.setNoise(Fnoise, Tnoise, Snoise);
        try {
            filter.createParticles(lastMotionState);
        } catch (Exception ex) {
            Log.e("ParticleFilter", ex.getMessage());
        }

        // Todo: Run motionDna and beaconscanner services
    }

    @Override
    public void run() {
        super.run();
        while (this.isRunning) {
//            Log.d(TAG, String.format("run: name %s", this.getName()));
            pauseRunner();
            synchronized (semaphore) {
                while (mPaused) {
//                    Log.d(TAG, "run: mPaused");
                    try {
                        semaphore.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

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

            if (ShowParticleCounter > SHOW_PARTICLE_COUNTER_THRESHOLD) {
                callback.onParticleLocationUpdate(filter.getParticles());
                ShowParticleCounter = -1;
            }
            ShowParticleCounter++;

        }
    }

    public void startRunner() {
        if (this.getState() == State.NEW) {
            this.start();
        } else {
            synchronized (semaphore) {
                mPaused = false;
                semaphore.notifyAll();
            }
        }
        this.isRunning = true;
    }

    private void pauseRunner() {
        synchronized (semaphore) {
            mPaused = true;
        }
    }

    private void resumeRunner() {
        synchronized (semaphore) {
            mPaused = false;
        }
    }


    public void stopRunner() {
        synchronized (semaphore) {
            mPaused = true;
            this.isRunning = false;
        }
    }


    public void onSensedMotionData(ArrayList<Double> newMotionState) {
        this.newMotionState = newMotionState;
        this.resumeRunner();
//        this.start();
    }

    public void onSensedLandmarkData(ArrayList<String> newMotionstate) {

    }


    public interface ParticleFilterCallback {
        public void onLocationUpdate(Double x, Double y, Double h);

        public void onParticleLocationUpdate(ArrayList<ArrayList<Double>> particles);
    }


}
