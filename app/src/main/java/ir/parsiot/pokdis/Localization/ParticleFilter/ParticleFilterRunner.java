package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ir.parsiot.pokdis.Localization.Beacon.BLEdevice;
import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.map.MapConsts;

public class ParticleFilterRunner extends Thread {
    private static final String TAG = "ParticleFilterRunner";
    ParticleFilter filter;
    private int ShowParticleCounter = 0;
    private int SHOW_PARTICLE_COUNTER_THRESHOLD = 10;

    public int getRelocationByProximityCnt() {
        return filter.relocationByProximityCnt;
    }

    public void setRelocationByProximityCnt(int relocationByProximityCnt) {
        filter.relocationByProximityCnt = relocationByProximityCnt;
    }

    BeaconLocations beaconLocations = new BeaconLocations();
    HashMap<String, Double[]> beaconCoordinates = beaconLocations.beaconCoordinates;
    public final int NUM_PARTICLES = 400;
//    public final int divisionResampleParticleNumThreshold = 3;
    public final float resampleThresholdDevision = 2.0f / 3.0f;
    double Fnoise = 0.05d, Tnoise = 0.05d, Snoise = 100d;
    ArrayList<Double> initScatterFactor = new ArrayList<Double>() {
        {
            add(4000d);
            add(4000d);
//            add(100d);
//            add(100d);
            add(40d);
        }
    };

    ArrayList<Double> resampleScatterFactor = new ArrayList<Double>() {
        {
            add(30d);
            add(30d);
            add(10d);
        }
    };

    private Object semaphore = new Object();
    private boolean mPaused = false;
    private volatile boolean isRunning = false;

    ArrayList<Double> curState = new ArrayList<Double>();
    ArrayList<Double> lastMotionState = new ArrayList<Double>();
    ArrayList<Double> motionState = new ArrayList<Double>();
    Boolean motionStateUpdated = false;
    ParticleFilterCallback callback;
    Activity appContext;

//    HashMap<String, Double> landmarkMeasurements = new HashMap<>();
    ArrayList<BLEdevice> importantNearBeacons = new ArrayList<>();
//    Boolean landmarkMeasurementsUpdated = false;
    Boolean importantNearBeaconsUpdated = false;

    public ParticleFilterRunner(ParticleFilterCallback callback, Activity appContext) {
        this.callback = callback;
        this.appContext = appContext;
        lastMotionState = MapConsts.getInitLocationDouble();
        lastMotionState.add(MapConsts.initHeading);

        filter = new ParticleFilter(NUM_PARTICLES, resampleThresholdDevision, initScatterFactor, resampleScatterFactor, beaconCoordinates);
        filter.setNoise(Fnoise, Tnoise, Snoise);
        try {
//            filter.createParticles(lastMotionState);
            filter.createParticles(MapConsts.constInitState);
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

            if (motionStateUpdated) {
                Double dx = motionState.get(0) - lastMotionState.get(0);
                Double dy = motionState.get(1) - lastMotionState.get(1);
                Double dh = motionState.get(2) - lastMotionState.get(2);

                lastMotionState = motionState;

                filter.move(dx, dy, dh);

                motionStateUpdated = false;
            }
//            if (landmarkMeasurementsUpdated){
//
//                filter.applyLandmarkMeasurements(landmarkMeasurements);
//
//                landmarkMeasurementsUpdated = false;
//            }
            if (importantNearBeaconsUpdated){

//                filter.applyLandmarkMeasurements(proximityMeasurements);
                filter.applyLandmarkProximityMeasurements(importantNearBeacons);

                importantNearBeaconsUpdated = false;
            }

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
                        try {
                            callback.onLocationUpdate(curState.get(0), curState.get(1), curState.get(2));
                        }catch(Exception ex){
                            Log.e("ParticleFilter", ex.getMessage());
                        }
                    }
                });
            }
            callback.onLocationUpdate(curState.get(0), curState.get(1), curState.get(2));


            // Show particles

            if (ShowParticleCounter > SHOW_PARTICLE_COUNTER_THRESHOLD) {

//                callback.onParticleLocationUpdate(filter.getParticles());

                if (appContext != null) {
                    ((Activity) appContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onParticleLocationUpdate(filter.getParticles(1));
                            callback.onShowParticleClusterCenters(filter.clusterCenters);
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


    public void onSensedMotionData(ArrayList<Double> motionState) {
        this.motionState = motionState;
        this.motionStateUpdated = true;
//        Log.d("Pf thread:","OK");
        this.resumeRunner();
//        this.start();
    }



//    public void onSensedLandmarkData(HashMap<String, Double> landmarkMeasurements) {
//        this.landmarkMeasurements = landmarkMeasurements;
//        this.landmarkMeasurementsUpdated = true;
//        this.resumeRunner();
//    }


    public void onSensedLandmarkProxmity(ArrayList<BLEdevice> importantNearBeacons) {
        this.importantNearBeacons = importantNearBeacons;
        this.importantNearBeaconsUpdated = true;
        this.resumeRunner();
    }


    public interface ParticleFilterCallback {
        public void onLocationUpdate(Double x, Double y, Double h);

        public void onParticleLocationUpdate(ArrayList<ArrayList<Double>> particles);
        public void onShowParticleClusterCenters(Double[][] clusterCenters);
    }


}
