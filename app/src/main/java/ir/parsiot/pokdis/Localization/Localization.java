package ir.parsiot.pokdis.Localization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ir.parsiot.pokdis.Constants.Constants;
import ir.parsiot.pokdis.Localization.Beacon.BLEdevice;
import ir.parsiot.pokdis.Localization.MotionDna.MotionDnaForegroundService;
import ir.parsiot.pokdis.Localization.MotionDna.NaviSettings;
import ir.parsiot.pokdis.Localization.Beacon.BeaconCallback;
import ir.parsiot.pokdis.Localization.Beacon.BeaconDiscovered;
import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.Localization.ParticleFilter.ParticleFilterRunner;
import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.Objects.Graph;
import ir.parsiot.pokdis.map.Objects.Point;
import ir.parsiot.pokdis.map.WallGraph.RectObstacle;
import ir.parsiot.pokdis.map.WebViewManager;

import static android.os.SystemClock.elapsedRealtime;
import static ir.parsiot.pokdis.Constants.Constants.MAX_PROXIMITY_TO_ROUTE_THRESHOLD;
import static ir.parsiot.pokdis.Constants.Constants.MIN_VALID_PROXIMITY_RSS;
import static ir.parsiot.pokdis.Localization.MotionDna.NaviSettings.MAX_PROXIMITY_TO_ROUTE_THRESHOLD_IMU;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.Convert2zeroto360;

public class Localization implements MotionDnaInterface, ParticleFilterRunner.ParticleFilterCallback {

    WebViewManager.LocationUpdateCallback locationUpdateCallback;
    PackageManager packageManager;
    Context context;


    ParticleFilterRunner pfFilter;

    ArrayList<ArrayList<Point>> routePath = new ArrayList<ArrayList<Point>>();

    // Beacon:
    private BeaconDiscovered beaconDiscovered;
    Hashtable<String, ArrayList<Double>> beaconHistories = new Hashtable<String, ArrayList<Double>>();

    private int farFromRouteCnt = 0;


    // MotionDna:
    MotionDnaApplication motionDnaApplication;
    Hashtable<String, MotionDna> networkUsers = new Hashtable<String, MotionDna>();
    Hashtable<String, Double> networkUsersTimestamps = new Hashtable<String, Double>();
    Intent motionDnaServiceIntent;
    // Custom MotionDna :
    NaviSettings naviSettings = new NaviSettings();

    public double x;
    public double y;
    public double h;

//    public double deltaH;
//    ArrayList<Double> deltaHs = new ArrayList<Double>();
//
//    double LastCornerX = MapConsts.getInitLocationFloat().get(1), LastCornerY = MapConsts.getInitLocationFloat().get(0);
//    double LastCornerCandidateX = MapConsts.getInitLocationFloat().get(1), LastCornerCandidateY = MapConsts.getInitLocationFloat().get(0);
//
//    public double localOffsetX = 0;
//    public double localOffsetY = 0;
//    public double localOffsetH = 0;


//    // Relocation Algorithm Parameters:
//    HashMap<String, ArrayList<Long>> beaconTimestamps = new HashMap<String, ArrayList<Long>>();
//    HashMap<String, HashMap<Long, Integer>> beaconHistories = new HashMap<String, HashMap<Long, Integer>>();
//    HashMap<String, Integer> beaconCount = new HashMap<String, Integer>();
//    HashMap<String, Integer> beaconCountSideBySide = new HashMap<String, Integer>();
//    HashMap<String, Integer> beaconCountFar = new HashMap<String, Integer>();
//    HashMap<String, Long> sideBySideLastTime = new HashMap<String, Long>();

    private String lastLocationXY = null;
    private String lastHeading = null;


    @Override
    public PackageManager getPkgManager() {
        return packageManager;
    }

    @Override
    public Context getAppContext() {
        return context;
    }


    public Localization(WebViewManager.LocationUpdateCallback locationUpdateCallback, PackageManager packageManager, Context context, Activity activity) {
        this.locationUpdateCallback = locationUpdateCallback;
        this.packageManager = packageManager;
        this.context = context;

        pfFilter = new ParticleFilterRunner(this, activity);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                pfFilter.startRunner();
            }
        }, 2000);

        drawObstacleWalls();
        // Get location from beacon manager

    }

    public void InitAndStart() {
        motionDnaServiceIntent = new Intent(getAppContext(), MotionDnaForegroundService.class);
        getAppContext().startService(motionDnaServiceIntent);



        try {
            beaconDiscovered = new BeaconDiscovered(this.context);

            //Todo: enable it when use beacon
            beaconDiscovered.startMonitoring();
//            beaconDiscovered.startMonitoring();
//            beaconDiscovered.startMonitoringNearMotionDna();
            beaconDiscovered.setCallback(new MyBeaconCallback());
//            updateLocationJustBeacon();
        } catch (RuntimeException e) {
            Log.e("Error:", e.getMessage());
        }

        // Start the MotionDna Core
        startMotionDna();
    }

    public void onDestory() {
        //for particle filter runner
        if (pfFilter != null){
            pfFilter.stopRunner();
        }
        //for unbind beaconDiscovered
        if (beaconDiscovered != null) {
            try {
                beaconDiscovered.stopMonitoring();
                beaconDiscovered.unbind();
            } catch (RuntimeException e) {
                Log.e("error", e.toString());
            }
        }

        // MotionDna:
        // Shuts downs the MotionDna Core
        motionDnaApplication.stop();

        // Handle destruction of the foreground service if
        // it is enabled
        if (motionDnaServiceIntent != null) {
            getAppContext().stopService(motionDnaServiceIntent);
        }
        //
    }


    // MotionDna:
    public void startMotionDna() {
        String devKey = "mpIR1Zf2euR74NJT7Pc9jl7kTJpI96pGma2BWimBF3kJugDRdWsw76fkU90dMD7O";

        motionDnaApplication = new MotionDnaApplication(this);
//        motionDnaApplication.setLocalHeading(90.0);

        //    This functions starts up the SDK. You must pass in a valid developer's key in order for
        //    the SDK to function. IF the key has expired or there are other errors, you may receive
        //    those errors through the reportError() callback route.

        motionDnaApplication.runMotionDna(devKey);

        //    Use our internal algorithm to automatically compute your location and heading by fusing
        //    inertial estimation with global location information. This is designed for outdoor use and
        //    will not compute a position when indoors. Solving location requires the user to be walking
        //    outdoors. Depending on the quality of the global location, this may only require as little
        //    as 10 meters of walking outdoors.

//        motionDnaApplication.setLocationNavisens();

        //   Set accuracy for GPS positioning, states :HIGH/LOW_ACCURACY/OFF, OFF consumes
        //   the least battery.

//        motionDnaApplication.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);

        //    Manually sets the global latitude, longitude, and heading. This enables receiving a
        //    latitude and longitude instead of cartesian coordinates. Use this if you have other
        //    sources of information (for example, user-defined address), and need readings more
        //    accurate than GPS can provide.
//        motionDnaApplication.setLocationLatitudeLongitudeAndHeadingInDegrees(37.787582, -122.396627, 0);

        //    Set the power consumption mode to trade off accuracy of predictions for power saving.

        motionDnaApplication.setPowerMode(MotionDna.PowerConsumptionMode.PERFORMANCE);

        //    Connect to your own server and specify a room. Any other device connected to the same room
        //    and also under the same developer will receive any udp packets this device sends.

//        motionDnaApplication.startUDP();

        //    Allow our SDK to record data and use it to enhance our estimation system.
        //    Send this file to support@navisens.com if you have any issues with the estimation
        //    that you would like to have us analyze.

        motionDnaApplication.setBinaryFileLoggingEnabled(true);

        //    Tell our SDK how often to provide estimation results. Note that there is a limit on how
        //    fast our SDK can provide results, but usually setting a slower update rate improves results.
        //    Setting the rate to 0ms will output estimation results at our maximum rate.

        motionDnaApplication.setCallbackUpdateRateInMs(1);

        //    When setLocationNavisens is enabled and setBackpropagationEnabled is called, once Navisens
        //    has initialized you will not only get the current position, but also a set of latitude
        //    longitude coordinates which lead back to the start position (where the SDK/App was started).
        //    This is useful to determine which building and even where inside a building the
        //    person started, or where the person exited a vehicle (e.g. the vehicle parking spot or the
        //    location of a drop-off).
//        motionDnaApplication.setBackpropagationEnabled(true);

        //    If the user wants to see everything that happened before Navisens found an initial
        //    position, he can adjust the amount of the trajectory to see before the initial
        //    position was set automatically.
        motionDnaApplication.setBackpropagationBufferSize(200000); //Todo : It was 2000 by default
        motionDnaApplication.setLocalHeadingOffsetInDegrees(MapConsts.initHeading);
        //    Enables AR mode. AR mode publishes h quaternion at a higher rate.
//        motionDnaApplication.setARModeEnabled(true);
        motionDnaServiceIntent = new Intent(getAppContext(), MotionDnaForegroundService.class);
        getAppContext().startService(motionDnaServiceIntent);

    }

    //    This event receives the estimation results using a MotionDna object.
    //    Check out the Getters section to learn how to read data out of this object.

    @Override
    public void receiveNetworkData(MotionDna.NetworkCode networkCode, Map<String, ?> map) {

    }

//    public double getMainHeading() {
//        return Convert2zeroto360(h + localOffsetH);
//    }

    @Override
    public void receiveNetworkData(MotionDna motionDna) {

        networkUsers.put(motionDna.getID(), motionDna);
        double timeSinceBootSeconds = elapsedRealtime() / 1000.0;
        networkUsersTimestamps.put(motionDna.getID(), timeSinceBootSeconds);
        StringBuilder activeNetworkUsersStringBuilder = new StringBuilder();
        List<String> toRemove = new ArrayList();

        activeNetworkUsersStringBuilder.append("Network Shared Devices:\n");
        for (MotionDna user : networkUsers.values()) {
            if (timeSinceBootSeconds - networkUsersTimestamps.get(user.getID()) > 2.0) {
                toRemove.add(user.getID());
            } else {
                activeNetworkUsersStringBuilder.append(user.getDeviceName());
                MotionDna.XYZ location = user.getLocation().localLocation;
                activeNetworkUsersStringBuilder.append(String.format(" (%.2f, %.2f, %.2f)", location.x, location.y, location.z));
                activeNetworkUsersStringBuilder.append("\n");
            }

        }
        for (String key : toRemove) {
            networkUsers.remove(key);
            networkUsersTimestamps.remove(key);
        }

//        networkTextView.setText(activeNetworkUsersStringBuilder.toString());
    }

    @Override
    public void receiveMotionDna(MotionDna motionDna) {

        if (this.locationUpdateCallback != null && pfFilter != null) {
            MotionDna.Location location = motionDna.getLocation();
            ArrayList<Double> newMotionstate = new ArrayList<Double>();
            newMotionstate.add(location.localLocation.y * naviSettings.scale + MapConsts.getInitLocationFloat().get(0));
            newMotionstate.add(location.localLocation.x * naviSettings.scale + MapConsts.getInitLocationFloat().get(1));
//            newMotionstate.add(Convert2zeroto360(-1 * (Convert2zeroto360(-1 * location.heading) + MapConsts.initHeading)));
            newMotionstate.add(
                    Convert2zeroto360(-1 * (Convert2zeroto360(Convert2zeroto360(-1 * location.heading) + 90.0D)) + 180 - MapConsts.initHeading)
            );
            String locationXY = String.format("%.2f,%.2f", newMotionstate.get(0), newMotionstate.get(1));
            String heading = String.format("%.2f", newMotionstate.get(2));

            if (!(locationXY.equals(lastLocationXY) && heading.equals(lastHeading))) {
//                Log.e("heading ", heading);
                pfFilter.onSensedMotionData(newMotionstate);
//
//                locationUpdateCallback.onLocationUpdate(locationXY, heading);
            }
        }
    }

    @Override
    public void reportError(MotionDna.ErrorCode errorCode, String s) {
        switch (errorCode) {
            case ERROR_AUTHENTICATION_FAILED:
                System.out.println("Error: authentication failed " + s);
                break;
            case ERROR_SDK_EXPIRED:
                System.out.println("Error: SDK expired " + s);
                break;
            case ERROR_PERMISSIONS:
                System.out.println("Error: permissions not granted " + s);
                break;
            case ERROR_SENSOR_MISSING:
                System.out.println("Error: sensor missing " + s);
                break;
            case ERROR_SENSOR_TIMING:
                System.out.println("Error: sensor timing " + s);
                break;
        }
    }

    // MotionDna beacon :
    public class MyBeaconCallback implements BeaconCallback {


        public MyBeaconCallback() {
        }

        private final Lock _mutex = new ReentrantLock(true);

        @Override
        public void onBeaconDetection(final ArrayList<BLEdevice> sortedDiscoveredDevices) {
            try {
                _mutex.lock();

//                HashMap<String, Double> measurements = new HashMap<>();
                ArrayList<BLEdevice> importantNearBeacons = new ArrayList<>();
                if (sortedDiscoveredDevices.size()>0) {
                    if (sortedDiscoveredDevices.get(0).getRssiAvg() >= MIN_VALID_PROXIMITY_RSS) {
//                    measurements.put(sortedDiscoveredDevices.get(0).getMac(), PROXIMITY_DISTANCE * MapConsts.scale);
                        importantNearBeacons.add(sortedDiscoveredDevices.get(0));
                    }
                    if (sortedDiscoveredDevices.size() > 1) {
                        if (sortedDiscoveredDevices.get(1).getRssiAvg() >= MIN_VALID_PROXIMITY_RSS) {
//                    measurements.put(sortedDiscoveredDevices.get(0).getMac(), PROXIMITY_DISTANCE * MapConsts.scale);
                            importantNearBeacons.add(sortedDiscoveredDevices.get(1));
                        }
                    }
                }


//                for(BLEdevice bleDevice: sortedDiscoveredDevices){
////                    beaconHistories.get(beacon).
//                    measurements.put(bleDevice.getMac(), convertRssToDist1(beacon.getRssi())*MapConsts.scale);
////                }
                if (pfFilter != null && importantNearBeacons.size()>0){
//                    pfFilter.onSensedLandmarkData(measurements);
                    pfFilter.onSensedLandmarkProxmity(importantNearBeacons);
                }

            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            } finally {
                _mutex.unlock();
            }


        }
    }



//    private void updateLocationJustBeacon() {
//        // update location of marker on the map
//        try {
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
////                    String location = beaconDiscovered.getNearLocationToString();
//                    ArrayList<String> nearBeaconLocations = beaconDiscovered.getAllSortedDiscoveredBeaconLocations();
//
//                    if (nearBeaconLocations != null) {
//                        if (nearBeaconLocations.size() > 0) {
////                        Log.e("location:", location);
//                            String location;
//                            if (routePath.size() > 0) {
//                                location = findNearLocationByPath(nearBeaconLocations, routePath);
//                            } else {
//                                location = nearBeaconLocations.get(0);
//                            }
//                            if (location != null) {
//                                locationUpdateCallback.onLocationUpdate(location);
//                            } else {
//                                Log.d("MainActivity", "Estimated location is far from route path");
//                            }
//                        }
//                    }
//                }
//            }, Constants.DELAY_ON_SHOW_TOP_BEACON, Constants.PERIOD_OF_GET_TOP_BEACON);
//        } catch (RuntimeException e) {
//
//        }
//
//    }


    // Map related functions :

    // Todo: We should create other class and place routing related function in it
    public String findNearLocationByPath(ArrayList<String> nearBeaconLocations, ArrayList<ArrayList<Point>> RoutePath) {
        /*
        According to the location of the near beacon and the path between
            source and destination of the route we find best location on the route
         */
        String firstBeaconLocation = nearBeaconLocations.get(0);
        if (Graph.dist2Route(firstBeaconLocation, RoutePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD) {  // estLocation is near the route graph
            farFromRouteCnt = 0;
            return firstBeaconLocation;
        } else {
            farFromRouteCnt++;
            if (farFromRouteCnt < Constants.MIN_COUNT_TO_IGNORE_PATH) {
                if (nearBeaconLocations.size() > 1) {
                    String secondBeaconLocation = nearBeaconLocations.get(1);
                    if (Graph.dist2Route(secondBeaconLocation, RoutePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD) {  // estLocation is near the route graph
                        return secondBeaconLocation;
                    }
                }
            } else {
                return firstBeaconLocation;
            }
        }
        return null; // Don't update location!
    }

    /////////


    public String findNearLocationByPathIMUVersion(String location, String heading, ArrayList<ArrayList<Point>> RoutePath) {
        /*
        According to the location ...
         */
        if (Graph.dist2Route(location, RoutePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD_IMU) {  // estLocation is near the route graph
            farFromRouteCnt = 0;
            return location;
        }
//        } else {
//            farFromRouteCnt++;
//            if (farFromRouteCnt < Constants.MIN_COUNT_TO_IGNORE_PATH) {
//                if (nearBeaconLocations.size() > 1) {
//                    String secondBeaconLocation = nearBeaconLocations.get(1);
//                    if (Graph.dist2Route(secondBeaconLocation, routePath) < MAX_PROXIMITY_TO_ROUTE_THRESHOLD) {  // estLocation is near the route graph
//                        return secondBeaconLocation;
//                    }
//                }
//            } else {
//                return firstBeaconLocation;
//            }
//        }
        return null; // Don't update location!
    }

    public void updateRoutePath(ArrayList<ArrayList<Point>> routePath) {
        this.routePath = routePath;
    }

    public void clearBeaconDataHistory() {
        farFromRouteCnt = 0;

//        ArrayList<ArrayList<String>> particleExample = new ArrayList<ArrayList<String>>();
//        ArrayList<String> particle = new ArrayList<String>();
//        particle.add("100");
//        particle.add("100");
//        particle.add("100");
//        particleExample.add(particle);
//        locationUpdateCallback.onUpdateParticles(particleExample);
    }

    public void onLocationUpdate(Double x, Double y, Double h) {
        String locationXY = String.format("%.2f,%.2f", x, y);
        String heading = String.format("%.2f", h);
        locationUpdateCallback.onLocationUpdate(locationXY, heading);
    }

    public void onParticleLocationUpdate(ArrayList<ArrayList<Double>> particles) {
        ArrayList<ArrayList<String>> particlesData = new ArrayList<ArrayList<String>>();
        for (ArrayList<Double> particle : particles) {
            ArrayList<String> particleStrList = new ArrayList<String>();
            for (Double dim : particle) {
                particleStrList.add(dim.toString());
            }
            particlesData.add(particleStrList);
        }
        locationUpdateCallback.onUpdateParticles(particlesData);
    }

    public void drawObstacleWalls(){
        MapConsts mapConsts = new MapConsts();
        for(RectObstacle rectObstacle: mapConsts.rectObstacles){
            ArrayList<ArrayList<String>> walls = new ArrayList<ArrayList<String>>();
            for (Double[][] wall : rectObstacle.getWalls()){
                String dot1 = String.format("%.2f,%.2f", wall[0][1], wall[0][0]);
                String dot2 = String.format("%.2f,%.2f", wall[1][1], wall[1][0]);
                Log.d("Obstacle1:",dot1 +" "+dot2);
                locationUpdateCallback.drawConstLine(dot1, dot2);
            }
        }


        ArrayList<ArrayList<String>> walls = new ArrayList<ArrayList<String>>();
        for (Double[][] wall : mapConsts.mapBorderRect.getWalls()){
            String dot1 = String.format("%.2f,%.2f", wall[0][1], wall[0][0]);
            String dot2 = String.format("%.2f,%.2f", wall[1][1], wall[1][0]);
            Log.d("Obstacle1:",dot1 +" "+dot2);
            locationUpdateCallback.drawConstLine(dot1, dot2);
        }

    }

}
