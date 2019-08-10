package ir.parsiot.pokdis.Localization;

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
import ir.parsiot.pokdis.Localization.MotionDna.MotionDnaForegroundService;
import ir.parsiot.pokdis.Localization.MotionDna.NaviSettings;
import ir.parsiot.pokdis.Localization.Beacon.BeaconCallback;
import ir.parsiot.pokdis.Localization.Beacon.BeaconDiscovered;
import ir.parsiot.pokdis.Localization.Beacon.BeaconLocations;
import ir.parsiot.pokdis.Localization.ParticleFilter.ParticleFilterRunner;
import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.Objects.Graph;
import ir.parsiot.pokdis.map.Objects.Point;
import ir.parsiot.pokdis.map.WebViewManager;

import static android.os.SystemClock.elapsedRealtime;
import static ir.parsiot.pokdis.Constants.Constants.MAX_PROXIMITY_TO_ROUTE_THRESHOLD;
import static ir.parsiot.pokdis.Localization.MotionDna.NaviSettings.MAX_PROXIMITY_TO_ROUTE_THRESHOLD_IMU;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.Convert2zeroto360;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.DegreeDiff;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.findCloseDot;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.round;

public class Localization implements MotionDnaInterface, ParticleFilterRunner.ParticleFilterCallback {

    WebViewManager.LocationUpdateCallback locationUpdateCallback;
    PackageManager packageManager;
    Context context;


    ParticleFilterRunner pfFilter;

    ArrayList<ArrayList<Point>> routePath = new ArrayList<ArrayList<Point>>();

    // Beacon:
    private BeaconDiscovered beaconDiscovered;
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

    public double deltaH;
    ArrayList<Double> deltaHs = new ArrayList<Double>();

    double LastCornerX = MapConsts.getInitLocationFloat().get(1), LastCornerY = MapConsts.getInitLocationFloat().get(0);
    double LastCornerCandidateX = MapConsts.getInitLocationFloat().get(1), LastCornerCandidateY = MapConsts.getInitLocationFloat().get(0);

    public double localOffsetX = 0;
    public double localOffsetY = 0;
    public double localOffsetH = 0;


    // Relocation Algorithm Parameters:
    HashMap<String, ArrayList<Long>> beaconTimestamps = new HashMap<String, ArrayList<Long>>();
    HashMap<String, HashMap<Long, Integer>> beaconHistories = new HashMap<String, HashMap<Long, Integer>>();
    HashMap<String, Integer> beaconCount = new HashMap<String, Integer>();
    HashMap<String, Integer> beaconCountSideBySide = new HashMap<String, Integer>();
    HashMap<String, Integer> beaconCountFar = new HashMap<String, Integer>();
    HashMap<String, Long> sideBySideLastTime = new HashMap<String, Long>();

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


    public Localization(WebViewManager.LocationUpdateCallback locationUpdateCallback, PackageManager packageManager, Context context) {
        this.locationUpdateCallback = locationUpdateCallback;
        this.packageManager = packageManager;
        this.context = context;

        pfFilter = new ParticleFilterRunner(this);

        // Get location from beacon manager
        try {
            beaconDiscovered = new BeaconDiscovered(this.context);

            //Todo: enable it when use beacon
//            beaconDiscovered.startMonitoring();
//            beaconDiscovered.startMonitoringNearMotionDna();
            beaconDiscovered.setCallback(new MyBeaconCallback());
//            updateLocationJustBeacon();
        } catch (RuntimeException e) {
            Log.e("Error:", e.getMessage());
        }
    }

    public void InitAndStart() {
        motionDnaServiceIntent = new Intent(getAppContext(), MotionDnaForegroundService.class);
        getAppContext().startService(motionDnaServiceIntent);

        // Start the MotionDna Core
        startMotionDna();
    }

    public void onDestory() {
        //for unbind beaconDiscovered
        if (beaconDiscovered != null) {
            try {
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

    public double getMainHeading() {
        return Convert2zeroto360(h + localOffsetH);
    }

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
                Log.e("updateLocationAndH", locationXY);
                pfFilter.onSensedMotionData(newMotionstate);
//
//                locationUpdateCallback.onLocationUpdate(locationXY, heading);
            }
        }
//        // mupdate
////        Log.e("receiveMotionDna:", "receiveMotionDna");
//
////        String str = "Navisens MotionDna Location Data:\n";
////        str += "Lat: " + motionDna.getLocation().globalLocation.latitude + " Lon: " + motionDna.getLocation().globalLocation.longitude + "\n";
////        MotionDna.XYZ location = motionDna.getLocation().localLocation;
////        str += String.format(" (%.2f, %.2f, %.2f)\n",location.x*scale, location.y*scale, location.z);
//
//        if (this.locationUpdateCallback != null) {
//            MotionDna.Location location = motionDna.getLocation();
//
////            final String str = String.format("%.2f,%.2f",
////                    location.y*scale + MapConsts.getInitLocationFloat().get(0),
////                    location.x*scale + MapConsts.getInitLocationFloat().get(1));
//
//
//            this.x = location.localLocation.x * naviSettings.scale;
//            this.y = location.localLocation.y * naviSettings.scale;
//            this.deltaH = DegreeDiff(this.h, Convert2zeroto360(Convert2zeroto360(-1 * location.heading) + 90.0D));
//
//            int deltaHsSize = deltaHs.size();
//            if (deltaHsSize <= naviSettings.maxDeltaHsLen2) {
//                deltaHs.add(this.deltaH);
//            } else {
//                deltaHs.remove(0); //delete older h
//                deltaHs.add(this.deltaH);
//            }
//
//            double cumulativeDelta1 = 0;
//            double cumulativeDelta2 = 0;
//            for (int i = deltaHsSize - 1; i >= 0; i--) {
//                cumulativeDelta2 += deltaHs.get(i);
//                if (i <= naviSettings.maxDeltaHsLen2 / naviSettings.maxDeltaHsLen1) {
//                    cumulativeDelta1 += deltaHs.get(i);
//                }
//            }
//
//
//            if (Math.sqrt(Math.pow(LastCornerX - this.x, 2) + Math.pow(LastCornerY - this.y, 2)) > naviSettings.cornerCandidateDiff) {
//                LastCornerCandidateX = this.x;
//                LastCornerCandidateY = this.y;
//            }
//            if (Math.sqrt(Math.pow(LastCornerX - this.x, 2) + Math.pow(LastCornerY - this.y, 2)) > naviSettings.cornerDistanceDiff) {
//                LastCornerX = LastCornerCandidateX;
//                LastCornerY = LastCornerCandidateY;
//            }
//            if (cumulativeDelta1 > naviSettings.cornerDetectionTreshold1 && cumulativeDelta2 > naviSettings.cornerDetectionTreshold2) {
//                // Find corner
//                LastCornerX = this.x;
//                LastCornerY = this.y;
//                LastCornerCandidateX = this.x;
//                LastCornerCandidateY = this.y;
//            }
////            Log.e("location.heading", String.valueOf(location.heading));
//            this.h = Convert2zeroto360(Convert2zeroto360(-1 * location.heading) + 90.0D + MapConsts.initHeading);
//
//            String locationXY = String.format("%d,%d", (int) (this.y + MapConsts.getInitLocationFloat().get(0)), (int) (this.x + MapConsts.getInitLocationFloat().get(1)));
//            String heading = String.format("%d", (int) Convert2zeroto360(-1 * getMainHeading() + 180));
//
//            if (!(locationXY.equals(lastLocationXY) && heading.equals(lastHeading))){
//                Log.e("updateLocationAndH",locationXY);
//                locationUpdateCallback.onLocationUpdate(locationXY, heading);
////                webViewManager.updateLocationAndHeading(locationXY, heading);
//            }
//
//            lastLocationXY = locationXY;
//            lastHeading = heading;
//
////            // Using findNearLocationByPathIMUVersion :
////            if (routePath.size() > 0) {
////                if (findNearLocationByPathIMUVersion(locationXY, heading, routePath) != null){
////                    webViewManager.updateLocationAndHeading(locationXY, heading);
////                }else{
////                    webViewManager.updateHeading(heading);
////                }
////            } else {
////                webViewManager.updateLocationAndHeading(locationXY, heading);
////            }
////            webViewManager.updateLocation(str);
//        }
//
//        str += "Hdg: " + motionDna.getLocation().heading +  " \n";
//        str += "motionType: " + motionDna.getMotion().motionType + "\n";
//        textView.setTextColor(Color.BLACK);
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                Log.e("Location:", str);
//            }
//        });
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
        public void onBeaconDetection(final Beacon beacon) {
            try {
//                Log.e("beacon", "didRangeBeaconsInRegion called with beacon count:  " + beacon.getBluetoothAddress());

                //log params to see the modification;;
                _mutex.lock();

                final String beaconName = beacon.getBluetoothAddress();
                BeaconLocations beaconLocations = new BeaconLocations();
                HashMap<String, Double[]> beaconCoordinates = beaconLocations.beaconCoordinates;

                Double[] coordinates = beaconCoordinates.get(beaconName);
                final double beaconY = coordinates[0] - MapConsts.getInitLocationFloat().get(0);
                final double beaconX = coordinates[1] - MapConsts.getInitLocationFloat().get(1);
//                final double beaconX = MapConsts.getInitLocationFloat().get(0);
//                final double beaconY = MapConsts.getInitLocationFloat().get(1);
                double currentX = x;
                double currentY = y;


                long currentTime = System.currentTimeMillis();
                int currentRss = 0;
                if (!beaconTimestamps.containsKey(beaconName)) { //initializations
                    beaconTimestamps.put(beaconName, new ArrayList<Long>());
                    beaconHistories.put(beaconName, new HashMap<Long, Integer>());
                    beaconCount.put(beaconName, 0);
                    beaconCountSideBySide.put(beaconName, 0);
                    beaconCountFar.put(beaconName, 0);
                    sideBySideLastTime.put(beaconName, 0l);
                }

                ArrayList<Long> timestamps = beaconTimestamps.get(beaconName);
                HashMap<Long, Integer> beaconHistory = beaconHistories.get(beaconName);

                int count = beaconCount.get(beaconName);
                int countSBS = beaconCountSideBySide.get(beaconName);

                boolean emptyTimestamps;
                long lastBeaconSeenTime = 0;
                if (timestamps.size() == 0) {
                    emptyTimestamps = true;
                } else {
                    emptyTimestamps = false;
                    lastBeaconSeenTime = timestamps.get(timestamps.size() - 1);
                }

                // Avoid some near detections to change the counter suddenly, so just change last rss according to them(calculate average of current rss and last rss)
                if (!emptyTimestamps && currentTime - lastBeaconSeenTime < naviSettings.minTimeToDetectNewRssi) {
                    int lastRss = beacon.getRssi();
                    currentRss = (beaconHistory.get(lastBeaconSeenTime) + lastRss) / 2;
                    beaconHistory.put(lastBeaconSeenTime, currentRss);
                    beaconTimestamps.put(beaconName, timestamps);
                    beaconHistories.put(beaconName, beaconHistory);

                } else {
                    // Add rss to beaconHistory
                    if (timestamps.size() < naviSettings.rssHistoryMaxLen) {
                        timestamps.add(currentTime);
                        beaconHistory.put(currentTime, beacon.getRssi());
                    } else { //Delete older timestamp and the related rss
                        beaconHistory.remove(timestamps.get(0));
                        timestamps.remove(0); //delete older timestamp

                        beaconHistory.put(currentTime, beacon.getRssi());
                        timestamps.add(currentTime);
                    }


                    // Delete very old beacon data & Change or reset count and countSBS according to the history
                    int timestampSize = timestamps.size(); // We'll replace it with `timestamps` variable
                    ArrayList<Long> tempTimestamps = new ArrayList<Long>();
                    Long lastTime = currentTime;
                    Long tempTime;
                    tempTimestamps.add(lastTime); // Add current time to tempTimestamp (because current time isn't old!)

                    boolean deleteRemains = false; // It acts as a `break` command, but we should clear beaconHistory, So we can't use `break`
                    // Check and delete old rssi
                    for (int i = timestampSize - 2; i >= 0; i--) {
                        tempTime = timestamps.get(i);

                        if (!deleteRemains) {
                            if (lastTime - tempTime > naviSettings.maxBeaconDelayms) {
                                if (i == timestampSize - 2) { //just current rss is new
                                    count = 0;
                                    countSBS = 0;
                                }
                                if (count > 0) {
                                    count--; // It's like a punishment!
                                }
                                if (countSBS > 0) {
                                    countSBS--; // It's like a punishment!
                                }
                                deleteRemains = true; // Ignore old rssi
                                continue;
                            }
                            tempTimestamps.add(0, tempTime);
                        } else {
                            beaconHistory.remove(tempTime);
                            if (count > 0) {
                                count--; // It's like a punishment!
                            }
                            if (countSBS > 0) {
                                countSBS--; // It's like a punishment!
                            }
                        }
                    }
                    timestamps = tempTimestamps;

                    // Renew beaconTimestamps and beaconHistories after delete the old data
                    beaconTimestamps.put(beaconName, timestamps);
                    beaconHistories.put(beaconName, beaconHistory);


                    int rssHistoryLen = tempTimestamps.size();

                    ArrayList<Integer> beaconHistoryDebug = new ArrayList<Integer>();
                    currentRss = 0; // Actually, It's average of the saved and current rssi
                    for (int i = 0; i <= rssHistoryLen - 1; i++) {
                        tempTime = timestamps.get(i);
                        Integer tempRss = beaconHistory.get(tempTime);
                        currentRss += tempRss;
                        beaconHistoryDebug.add(tempRss);
                    }
                    currentRss /= rssHistoryLen;

                    final int currentRss1 = currentRss;
                    final int count1 = count;
//                        Log.d(this.getClass().getSimpleName(), "Beacon: " + beaconName + " CurrentRssi: " + currentRss);


                    // on beacon direction detection:
                    double toBeaconVector = Math.sqrt(Math.pow(beaconX - currentX, 2) + Math.pow(beaconY - currentY, 2));
                    double toBeaconVectorHeading = 0;

                    // Calculate degree difference between the motion sensor heading and the line between current location and location of detected beacon
                    if (beaconY - currentY >= 0) {
                        toBeaconVectorHeading = Math.toDegrees(Math.acos(beaconX - currentX / toBeaconVector));
                    } else {
                        toBeaconVectorHeading = 360 - Math.toDegrees(Math.acos(beaconX - currentX / toBeaconVector));
                    }
                    double toBeaconVectorhDiff = DegreeDiff(getMainHeading(), toBeaconVectorHeading);

                    double tempRssThreshold = naviSettings.rssThreshold;
                    if (toBeaconVectorhDiff < naviSettings.maxtoBeaconVectorhDiff) {
                        tempRssThreshold += naviSettings.beaconDirectionRssThresholdDiff; // Mitigate rssThreshold when we turn to the beacon
                    }

                    if (currentRss >= tempRssThreshold) { // The main if that check current(average) rss to the RssThreshold

                        count++; // We increase `count` if we see that we see the beacon
                        beaconCountFar.put(beaconName, 0); // We don't know that the beacon is far or not, So we just consider it as far until now
//                            Log.d(this.getClass().getSimpleName(), "Count: " + count);

                        // We should reset count when we are in the adjacencyCircle for a long time
                        if (count > naviSettings.maxValidCountInAdjacency && Math.pow(currentX - beaconX, 2.0) + Math.pow(currentY - beaconY, 2.0) < naviSettings.adjacencyCircle) {
                            count = 0;
                        }

                        // Check that we aren't side by side a beacon but we see the beacon and we should react properly according to it
                        if (currentRss < naviSettings.bySideRssThreshold && count >= naviSettings.countThreshold) {
                            // If we are side by side a beacon but motion sensor shows we are out of adjacency circle
                            if (Math.pow(currentX - beaconX, 2.0) + Math.pow(currentY - beaconY, 2.0) > naviSettings.adjacencyCircle) {

                                Double[] dstDot = new Double[]{};
                                Double[] refDot = new Double[]{LastCornerX, LastCornerY};
                                Double[] curDot = new Double[]{currentX, currentY};
                                Double[] nearDot = new Double[]{(currentX + beaconX) / 2, (currentY + beaconY) / 2};

                                dstDot = findCloseDot(refDot, curDot, nearDot);

                                if (!dstDot[0].isNaN() && !dstDot[1].isNaN()) {
                                    double relocationVectorX, relocationVectorY;
                                    double dstx, dsty;
//                                        Log.d(this.getClass().getSimpleName(), "dstDot: " + dstDot[0] + "," + dstDot[1]);
                                    dstx = dstDot[0];
                                    dsty = dstDot[1];

                                    relocationVectorX = dstx - currentX;
                                    relocationVectorY = dsty - currentY;

                                    double relocationVectorLen = Math.sqrt(Math.pow(relocationVectorX, 2) + Math.pow(relocationVectorY, 2));
                                    double relocationHeading = 0;
                                    if (relocationVectorY >= 0) {
                                        relocationHeading = Math.toDegrees(Math.acos(relocationVectorX / relocationVectorLen));
                                    } else {
                                        relocationHeading = 360 - Math.toDegrees(Math.acos(relocationVectorX / relocationVectorLen));
                                    }
                                    double hDiff = DegreeDiff(getMainHeading(), relocationHeading);

                                    final double mainHeading = round(getMainHeading(), 0);
                                    final double relocationHeading1 = round(relocationHeading, 0);

                                    if (hDiff < naviSettings.maxValidRelocationHDiff) { // Avoid rollback replacings
                                        localOffsetX += relocationVectorX;
                                        localOffsetY += relocationVectorY;

                                        if (motionDnaApplication != null) {
                                            motionDnaApplication.setCartesianOffsetInMeters(localOffsetX / naviSettings.scale, localOffsetY / naviSettings.scale);
                                        } else {
                                            Log.d(this.getClass().getSimpleName(), "Core motion var is null!");

                                        }
                                        for (String otherBeaconName : beaconCount.keySet()) { // Reset other beacon counts
                                            if (!otherBeaconName.equals(beaconName)) {
                                                beaconCount.put(otherBeaconName, 0);
                                            }
                                        }
                                        for (String otherBeaconName : beaconCountSideBySide.keySet()) { // Reset other beacon SBS count
                                            if (!otherBeaconName.equals(beaconName)) {
                                                beaconCountSideBySide.put(otherBeaconName, 0);
                                            }
                                        }
//                                            Log.d(this.getClass().getSimpleName(), "relocation");

                                    }
                                } else {
                                    Log.e(this.getClass().getSimpleName(), "New Location x,y are NaN");
                                }
                                count = 0;
                            }


                        } else if (currentRss >= naviSettings.bySideRssThreshold) { // We are side by side a beacon

                            countSBS++;
                            if (countSBS >= naviSettings.bySideCountThreshold) {
                                Log.d(this.getClass().getSimpleName(), "side by side the beacon, dstDot: " + beaconX + "," + beaconY);

                                long lastBeaconSideBySideRelocation = sideBySideLastTime.get(beaconName);
                                // Check sidebyside relocation history and avoid relocation if we did it `minTimeBetweenSameSidebySide` [seconds] ago
                                if (currentTime - lastBeaconSideBySideRelocation > naviSettings.minTimeBetweenSameSidebySide) {

//                                    localOffsetX += (4 * beaconX + currentX) / 5 - currentX;
//                                    localOffsetY += (4 * beaconY + currentY) / 5 - currentY;
                                    localOffsetX += beaconX - currentX;
                                    localOffsetY += beaconY - currentY;

                                    Log.e("beacon", "SideBySide occurs on :  " + beacon.getBluetoothAddress() + " new Location: " + localOffsetX + "," + localOffsetY);

                                    if (motionDnaApplication != null) { // relocate the motionsonser xy to the beacon loaction
                                        motionDnaApplication.setCartesianOffsetInMeters(localOffsetX / naviSettings.scale, localOffsetY / naviSettings.scale);
                                    }

                                    Log.d(this.getClass().getSimpleName(), "sideBySide");

//                                    NavisensMaps.context.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
////                                    NavisensMaps.context.menuItem.setTitle(mainHeading + " : " + relocationHeading1);
//                                            NavisensMaps.context.menuItem.setTitle(String.format(Locale.getDefault(), "side:%s", Constants.beaconMacNameMap.get(beaconName)));
//
//                                        }
//                                    });


                                    sideBySideLastTime.put(beaconName, currentTime);
                                    for (String otherBeaconName : beaconCount.keySet()) { // Reset other beacon counts
                                        if (!otherBeaconName.equals(beaconName)) {
                                            beaconCount.put(otherBeaconName, 0);
                                        }
                                    }
                                    for (String otherBeaconName : beaconCountSideBySide.keySet()) { // Reset other beacon SBS count
                                        if (!otherBeaconName.equals(beaconName)) {
                                            beaconCountSideBySide.put(otherBeaconName, 0);
                                        }
                                    }

                                    countSBS = 0; // Reset the countSBS
                                    count = 0;  // Reset the count
                                }
                            }
//
                        }

                    } else if (currentRss < tempRssThreshold) { // If condition is ineffective, but I added it for readability

                        // Punish beacon adjacency counts( count & countSBS) when we receive weak rssi from the beacon.
                        Integer countFar = beaconCountFar.get(beaconName);
                        countFar++;
                        beaconCountFar.put(beaconName, countFar);
                        if (count > 0) {
                            count -= 2;
                        }
                        if (countSBS > 0) {
                            countSBS -= 2;
                        }
                        if (countFar >= naviSettings.maxCountFar) {
                            beaconCountFar.put(beaconName, 0);
                            countSBS = 0;
                            count = 0;
                        }
                    }

                    beaconCount.put(beaconName, count);
                    beaconCountSideBySide.put(beaconName, countSBS);
                }


            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            } finally {
                _mutex.unlock();
            }


        }
    }


    private void updateLocationJustBeacon() {
        // update location of marker on the map
        try {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
//                    String location = beaconDiscovered.getNearLocationToString();
                    ArrayList<String> nearBeaconLocations = beaconDiscovered.getAllSortedDiscoveredBeaconLocations();

                    if (nearBeaconLocations != null) {
                        if (nearBeaconLocations.size() > 0) {
//                        Log.e("location:", location);
                            String location;
                            if (routePath.size() > 0) {
                                location = findNearLocationByPath(nearBeaconLocations, routePath);
                            } else {
                                location = nearBeaconLocations.get(0);
                            }
                            if (location != null) {
                                locationUpdateCallback.onLocationUpdate(location);
                            } else {
                                Log.d("MainActivity", "Estimated location is far from route path");
                            }
                        }
                    }
                }
            }, Constants.DELAY_ON_SHOW_TOP_BEACON, Constants.PERIOD_OF_GET_TOP_BEACON);
        } catch (RuntimeException e) {

        }

    }


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

}
