package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.map.MapConsts;

public class ParticleFilterRunner extends Thread {
    private static final String TAG = "ParticleFilterRunner";
    ParticleFilter filter;
    private int ShowParticleCounter = 0;
    private int SHOW_PARTICLE_COUNTER_THRESHOLD = 10;

    HashMap<String, Double[]> beaconCoordinates = BeaconLocations.beaconCoordinates;
    public final int NUM_PARTICLES = 400;
    public final int divisionResampleParticleNumThreshold = 3;
    double Fnoise = 0.05d, Tnoise = 0.05d, Snoise = 0.05d;
    ArrayList<Double> initScatterFactor = new ArrayList<Double>() {
        {
            add(4000d);
            add(4000d);
            add(40d);
        }
    };

    ArrayList<Double> resampleScatterFactor = new ArrayList<Double>() {
        {
            add(60d);
            add(60d);
            add(10d);
        }
    };

    private Object semaphore = new Object();
    private boolean mPaused = false;
    private volatile boolean isRunning = false;

    ArrayList<Double> curState = new ArrayList<Double>();
    ArrayList<Double> lastMotionState = new ArrayList<Double>();
    ArrayList<Double> newMotionState = new ArrayList<Double>();
    ParticleFilterCallback callback;
    Activity appContext;

    public ParticleFilterRunner(ParticleFilterCallback callback, Activity appContext) {
        this.callback = callback;
        this.appContext = appContext;
        lastMotionState = MapConsts.getInitLocationDouble();
        lastMotionState.add(MapConsts.initHeading);

        filter = new ParticleFilter(NUM_PARTICLES, divisionResampleParticleNumThreshold, initScatterFactor, resampleScatterFactor, beaconCoordinates);
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

//            Log.e("particleFilter", curState.toString());

            if (appContext != null) {
                ((Activity) appContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLocationUpdate(curState.get(0), curState.get(1), curState.get(2));
                    }
                });
            }
//            callback.onLocationUpdate(curState.get(0), curState.get(1), curState.get(2));

            if (ShowParticleCounter > SHOW_PARTICLE_COUNTER_THRESHOLD) {

//                callback.onParticleLocationUpdate(filter.getParticles());

                if (appContext != null) {
                    ((Activity) appContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onParticleLocationUpdate(filter.getParticles());
                        }
                    });
                }


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
            semaphore.notifyAll();
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
//        Log.d("Pf thread:","OK");
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
