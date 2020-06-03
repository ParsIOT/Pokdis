package ir.parsiot.pokdis.Localization.ParticleFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ir.parsiot.pokdis.Localization.Beacon.BLEdevice;
import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.WallGraph.RectObstacle;

import static ir.parsiot.pokdis.Constants.Constants.MIN_CLUSTER_CENTER_DIST;
import static ir.parsiot.pokdis.Localization.MotionDna.Utils.Convert2zeroto360;

public class ParticleFilter {
    private static final String TAG = "ParticleFilter";

    ArrayList<Particle> particles;
    ArrayList<Particle> particlesHistory;


    public Double[][] clusterCenters = new Double[][]{};
    private boolean instantRelocateByBeacon = false;


    int numParticles = 0;
    int numInitParticles = 0;
    int numParticlesThreshold;
    boolean firstAggregation = false;
    boolean use2ClusterAlgorithm = false;

    public int relocationByProximityCnt = 0;

    Random gen = new Random();
    private ArrayList<Double> initScatterFactor;
    private ArrayList<Double> resampleScatterFactor;
    public HashMap<String, Double[]> landmarks;
    private Double Fnoise;
    private Double Tnoise;
    private Double Snoise;

    private static MapConsts mapConsts = new MapConsts();


    public ParticleFilter(int numParticles, float resampleThresholdDevision, ArrayList<Double> initScatterFactor, ArrayList<Double> resampleScatterFactor, HashMap<String, Double[]> landmarks) {
        this.numInitParticles = numParticles;
        this.numParticles = numParticles;
        this.numParticlesThreshold = (int) ((double) this.numInitParticles * (double) resampleThresholdDevision);
        this.initScatterFactor = initScatterFactor;
        this.resampleScatterFactor = resampleScatterFactor;
        this.landmarks = landmarks;
    }

    public void setNoise(Double Fnoise, Double Tnoise, Double Snoise) {
        this.Fnoise = Fnoise;
        this.Tnoise = Tnoise;
        this.Snoise = Snoise;
    }

    public boolean particleInitLocationValidation(Double x, Double y) {
//                particleInitState.add(initState.get(1) + initScatterFactor.get(1) * (gen.nextFloat()-0.5));
        Double[] xy = new Double[]{y, x};

        if (!mapConsts.mapBorderRect.inArea(xy)) {
//                    Log.d("ParticleFilter", mapConsts.mapBorderRect.toString()+" Particle is in denied areas1 "+xy.toString());
//                    mapConsts.mapBorderRect.inArea(xy);
            return false;
        }

        Boolean inRectObstacle = false;
        for (RectObstacle rectObstacle : mapConsts.rectObstacles) {
            if (rectObstacle.inArea(xy)) {
//                        Log.d("ParticleFilter", "Particle is in denied areas2");
                inRectObstacle = true;
                break;
            }
        }
        if (inRectObstacle) {
            return false;
        }
        return true;
    }

    public void createParticles(ArrayList<Double> initState) throws Exception {
        if (Fnoise == null || Tnoise == null || Snoise == null) {
            throw new Exception("Should setNoise before createParticles");
        }

        particles = new ArrayList<Particle>();
        gen = new Random();

        float initParticleProbability = (float) (1.0 / numInitParticles);

        for (int i = 0; i < numInitParticles; i++) {
            ArrayList<Double> particleInitState = new ArrayList<Double>();
            Boolean isParticleOk = false;
            while (!isParticleOk) {
//                Double x = initState.get(0) + initScatterFactor.get(0) * 2 * (gen.nextFloat() - 0.5);
//                Double y = initState.get(1) + initScatterFactor.get(1) * 2 * (gen.nextFloat() - 0.5);

                Double x = initState.get(0) + initScatterFactor.get(0) * 2 * (gen.nextFloat() - 0.5);
                Double y = initState.get(1) + initScatterFactor.get(1) * 2 * (gen.nextFloat() - 0.5);


//                particleInitState.add(initState.get(1) + initScatterFactor.get(1) * (gen.nextFloat()-0.5));
//                Double[] xy = new Double[]{y, x};
//
//                if (!mapConsts.mapBorderRect.inArea(xy)) {
////                    Log.d("ParticleFilter", mapConsts.mapBorderRect.toString()+" Particle is in denied areas1 "+xy.toString());
////                    mapConsts.mapBorderRect.inArea(xy);
//                    continue;
//                }
//
//                Boolean inRectObstacle = false;
//                for (RectObstacle rectObstacle : mapConsts.rectObstacles) {
//                    if (rectObstacle.inArea(xy)) {
////                        Log.d("ParticleFilter", "Particle is in denied areas2");
//                        inRectObstacle = true;
//                        break;
//                    }
//                }
//                if (inRectObstacle) {
//                    continue;
//                }
                if (!particleInitLocationValidation(x, y)) {
                    continue;
                }

                particleInitState.add(x);
                particleInitState.add(y);
                isParticleOk = true;
            }
            particleInitState.add(Convert2zeroto360(initState.get(2) + initScatterFactor.get(2) * 2 * (gen.nextFloat() - 0.5)));
            particles.add(new Particle(particleInitState, initParticleProbability, this.landmarks, Fnoise, Tnoise, Snoise));
        }

        particlesHistory.clear();
        particlesHistory.addAll(particles);
    }


    public void move(Double dx, Double dy, Double dh) {
        ArrayList<Particle> tempParticles = new ArrayList<Particle>();
        this.numParticles = 0;

        for (Particle particle : this.particles) {
            Double[][] collideWall = particle.move(dx, dy, dh);
            if (collideWall == null) {
                tempParticles.add(particle);
            }
        }
        this.particles = tempParticles;
        this.numParticles = this.particles.size();

//        Log.e(TAG, "Threshold: " + numParticlesThreshold);
//        Log.e(TAG, "Number of particles : " + numParticles);

        if (this.numParticles == 0) {
            particles.clear();
            particles.addAll(particlesHistory);
            numParticles = particles.size();
        }

        if (this.numParticles < numParticlesThreshold) {
            resample();
        }
//        Log.e(TAG, "After resample Number of particles : " + numParticles);

        particlesHistory = new ArrayList<Particle>();
        particlesHistory.addAll(particles);
    }

//    public void resample() {
//        ArrayList<Particle> tempParticles = new ArrayList<Particle>();
//        Double B = 0d;
//        Particle best = getBestParticle();
//        int index = (int) gen.nextDouble() * numParticles;
//        for (int i = 0; i < numInitParticles; i++) {
//            B += gen.nextDouble() * 2f * best.probability;
//            while (B > this.particles.get(index).probability) {
//                B -= this.particles.get(index).probability;
//                index = circle(index + 1, numParticles);
//            }
//
//            Particle chosenParticle = this.particles.get(index);
//            ArrayList<Double> particleInitState = new ArrayList<Double>();
//
//            Boolean isParticleOk = false;
//            while (!isParticleOk) {
//                Double x = chosenParticle.x + resampleScatterFactor.get(0) * 2 * (gen.nextFloat() - 0.5);
//                Double y = chosenParticle.y + resampleScatterFactor.get(1) * 2 * (gen.nextFloat() - 0.5);
//                if (!particleInitLocationValidation(x, y)){
//                    continue;
//                }
//
//                particleInitState.add(x);
//                particleInitState.add(y);
//                isParticleOk = true;
//            }
//            particleInitState.add(chosenParticle.h + resampleScatterFactor.get(2) * 2 * (gen.nextFloat() - 0.5));
//
//            tempParticles.add(new Particle(particleInitState, this.landmarks, Fnoise, Tnoise, Snoise));
//        }
//        this.particles = tempParticles;
//        this.numParticles = particles.size();
//    }


    private void resample() {
        ArrayList<Particle> tempParticles = new ArrayList<Particle>();

        int[] indexes = systematic_resample();

        float initParticleProbability = (float) (1.0 / numInitParticles);

        for (int index : indexes) {
            Particle chosenParticle = this.particles.get(index);
            ArrayList<Double> particleInitState = new ArrayList<Double>();

            Boolean isParticleOk = false;
            while (!isParticleOk) {
                Double x = chosenParticle.x + resampleScatterFactor.get(0) * 2 * (gen.nextFloat() - 0.5);
                Double y = chosenParticle.y + resampleScatterFactor.get(1) * 2 * (gen.nextFloat() - 0.5);
                if (!particleInitLocationValidation(x, y)) {
                    continue;
                }

                particleInitState.add(x);
                particleInitState.add(y);
                isParticleOk = true;
            }
            particleInitState.add(chosenParticle.h + resampleScatterFactor.get(2) * 2 * (gen.nextFloat() - 0.5));

            tempParticles.add(new Particle(particleInitState, initParticleProbability, this.landmarks, Fnoise, Tnoise, Snoise));
        }
        this.particles = tempParticles;
        this.numParticles = particles.size();
    }

    private int[] systematic_resample() {

        Random random = new Random();
//        float rand = (float) (random.nextFloat()/ numInitParticles);
        float splitLine = (float) (1.0 / numInitParticles);

        float[] positions = new float[numInitParticles];
        for (int i = 0; i < numInitParticles; i++) {
//            positions[i] = (float) (i * splitLine) + rand;
            positions[i] = (float) (i * splitLine) + (float) (random.nextFloat() / numInitParticles);
        }

        float[] cumsumWeight = new float[numParticles];

        float convertFactor = ((float) numInitParticles / (float) numParticles); // Many times numParticles is lower than numInitParticles. So it causes that sum of particle probabilities not to be 1
        cumsumWeight[0] = particles.get(0).probability * convertFactor;
        for (int i = 1; i < numParticles; i++) {
            cumsumWeight[i] = cumsumWeight[i - 1] + particles.get(i).probability * convertFactor;
        }
        cumsumWeight[numParticles - 1] = 1.0f; // Ensures sum is exactly one

        int i = 0;
        int j = 0;

        int[] indexes = new int[numInitParticles];
        while (i < numInitParticles) {
//            Log.e(TAG, "i:"+i+", j:"+j);
//            Log.e(TAG, "positions[i]:"+positions[i]+", cumsumWeight[j]:"+cumsumWeight[j]);
            if (positions[i] < cumsumWeight[j]) {
                indexes[i] = j;
                i++;
            } else {
                j++;
            }
        }

        return indexes;
    }

    private int circle(int num, int length) {
        while (num > length - 1) {
            num -= length;
        }
        while (num < 0) {
            num += length;
        }
        return num;
    }


//    public void resample(Double[] measurement) throws Exception {
//        Particle[] new_particles
//                = new Particle[numParticles];
//
//        for (int i = 0; i < numParticles; i++) {
//            particles[i].measurementProb(measurement);
//        }
//        Double B = 0d;
//        Particle best = getBestParticle();
//        int index = (int) gen.nextDouble() * numParticles;
//        for (int i = 0; i < numParticles; i++) {
//            B += gen.nextDouble() * 2f * best.probability;
//            while (B > particles[index].probability) {
//                B -= particles[index].probability;
//                index = circle(index + 1, numParticles);
//            }
//            new_particles[i] = new Particle(particles[index].landmarks, particles[index].worldWidth, particles[index].worldHeight);
//            new_particles[i].set(particles[index].x, particles[index].y, particles[index].h, particles[index].probability);
//            new_particles[i].setNoise(particles[index].Fnoise, particles[index].Tnoise, particles[index].Snoise);
//        }
//
//        particles = new_particles;
//    }


    public Particle getBestParticle() {
        Particle particle = particles.get(0);
        for (int i = 0; i < numParticles; i++) {
            if (particles.get(i).probability > particle.probability) {
                particle = particles.get(i);
            }
        }
        return particle;
    }

    public Particle getAverageParticle() {
        Particle p = new Particle();
        Double x = 0d, y = 0d, h = 0d;
        double probSum = 0d;
        for (int i = 0; i < numParticles; i++) { //Todo: probability must be multiply to x,y and h

            Particle particle = particles.get(i);
            x += particle.x * particle.probability;
            y += particle.y * particle.probability;
            h += particle.h * particle.probability;
            probSum += particle.probability;
        }
        x /= probSum;
        y /= probSum;
        h /= probSum;


        if (use2ClusterAlgorithm) {

            Double[] xy = new Double[]{y, x};
            Boolean inRectObstacle = false;
            for (RectObstacle rectObstacle : mapConsts.rectObstacles) {
                if (rectObstacle.inArea(xy)) {
//                        Log.d("ParticleFilter", "Particle is in denied areas2");
                    inRectObstacle = true;
                    break;
                }
            }


            clusterCenters = new Double[][]{};
            Double[] bestKmeans2Cluster = selectBestKmeans2Cluster();

            if (inRectObstacle) {
//            GraphBuilder location = new GraphBuilder();
//            String dot = String.format("%.2f,%.2f", x, y);
//            String resDot = location.graph.getNearestDot(dot);
//
//            String[] srcPointStrList = resDot.split(",");
//            x += Double.valueOf(srcPointStrList[0]) ;
//            x /= 2;
//            y += Double.valueOf(srcPointStrList[1]) ;
//            y /= 2;

////            relocationByProximityCnt = 3;
                if (instantRelocateByBeacon) {
                    relocationByProximityCnt -= 1;
                    instantRelocateByBeacon = false;
                }

                if (firstAggregation) {
                    x = bestKmeans2Cluster[0];
                    y = bestKmeans2Cluster[1];
                }
            }
        }


        try {
            p.set(x, y, h, 1);
        } catch (Exception ex) {
//            Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
        }

        p.setNoise(particles.get(0).Fnoise, particles.get(0).Tnoise, particles.get(0).Snoise);


        return p;
    }

//    public void applyLandmarkMeasurements(HashMap<String, Double> measurements){
//        for(Particle particle: this.particles){
//            Log.e(TAG, "Prob before update:"+particle.probability);
//            particle.updateProbs(measurements);
//            Log.e(TAG, "Prob after update:"+particle.probability);
//        }
//        // Todo: normalize the weights after weight update and resamapling
//        // Todo: Check probabilities and resample according to the Neff, if it's needed.
//    }

    private Double[] selectBestKmeans2Cluster() {
        int KMEANS_PERIOD = 50;
        ArrayList<Particle> cluster1 = new ArrayList<Particle>();
        ArrayList<Particle> cluster2 = new ArrayList<Particle>();

        Double[] cluster1Center = new Double[]{(double) (MapConsts.MAP_HEIGHT / 2), 0d};
        Double[] cluster2Center = new Double[]{(double) (-1 * MapConsts.MAP_HEIGHT / 2), 0d};

        for (int i = 0; i < KMEANS_PERIOD; i++) {
            cluster1.clear();
            cluster2.clear();

            for (Particle particle : this.particles) {
                Double dist1 = (Double) ParticleFilterMath.distance(particle.x, particle.y,
                        cluster1Center[0],
                        cluster1Center[1]);

                Double dist2 = (Double) ParticleFilterMath.distance(particle.x, particle.y,
                        cluster2Center[0],
                        cluster2Center[1]);

                if (dist1 < dist2) {
                    cluster1.add(particle);
                } else {
                    cluster2.add(particle);
                }
            }

            // Calculate cluster1Center
            cluster1Center = new Double[]{0d, 0d};
            for (Particle particle : cluster1) {
                cluster1Center[0] += particle.x;
                cluster1Center[1] += particle.y;
            }
            cluster1Center[0] /= cluster1.size();
            cluster1Center[1] /= cluster1.size();

            // Calculate cluster2Center
            cluster2Center = new Double[]{0d, 0d};
            for (Particle particle : cluster2) {
                cluster2Center[0] += particle.x;
                cluster2Center[1] += particle.y;
            }
            cluster2Center[0] /= cluster2.size();
            cluster2Center[1] /= cluster2.size();
        }

        // Select best cluster according to the particle filter
        Double cluster1Weight = 0d;
        Double cluster2Weight = 0d;
        for (Particle particle : cluster1) {
            cluster1Weight += particle.probability;
        }
        for (Particle particle : cluster2) {
            cluster2Weight += particle.probability;
        }

        double clusterSumWeightRatio = cluster1Weight / cluster2Weight;
        double clusterCentersDist = (Double) ParticleFilterMath.distance(cluster1Center[0], cluster1Center[1],
                cluster2Center[0],
                cluster2Center[1]);

        if (clusterCentersDist < MIN_CLUSTER_CENTER_DIST && firstAggregation == false) {
            firstAggregation = true;
        }

        double diffWeightRation = 0.3;
        if ((clusterSumWeightRatio > (1 - diffWeightRation) || (1 + diffWeightRation) > clusterSumWeightRatio) && clusterCentersDist > MIN_CLUSTER_CENTER_DIST) {
            instantRelocateByBeacon = true;
        }
        clusterCenters = new Double[][]{
                cluster1Center,
                cluster2Center,
        };

        if (cluster1Weight > cluster2Weight) {
            return cluster1Center;
        } else {
            return cluster2Center;
        }
    }


    public void applyLandmarkProximityMeasurements(ArrayList<BLEdevice> importantNearBeacons) {
        for (Particle particle : this.particles) {
//            Log.e(TAG, "Prob before update:"+particle.probability);
            particle.updateProbs(importantNearBeacons);
//            Log.e(TAG, "Prob after update:"+particle.probability);
        }
        normalizeWeights();


        // Todo: I didn't research about influence of Neff and its related resampling effects
        double Neff = calc_Neff();

        if (this.numParticles < Neff) {
            resample();
            particlesHistory = new ArrayList<Particle>();
            particlesHistory.addAll(particles);
        }
    }

    public ArrayList<ArrayList<Double>> getParticles(int randomParticleFactor) {
        ArrayList<ArrayList<Double>> particlesData = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < this.numParticles; i++) {
            if (i % randomParticleFactor == 0) {
                Particle particle = particles.get(i);
                ArrayList<Double> tempParticleData = new ArrayList<Double>();
                tempParticleData.add(particle.x);
                tempParticleData.add(particle.y);
                tempParticleData.add(particle.h);
                particlesData.add(tempParticleData);
            }
        }
//
//        for (Particle particle : particles) {
//            ArrayList<Double> tempParticleData = new ArrayList<Double>();
//            tempParticleData.add(particle.x);
//            tempParticleData.add(particle.y);
//            tempParticleData.add(particle.h);
//            particlesData.add(tempParticleData);
//        }
        return particlesData;
    }

    public void normalizeWeights() {
        Double sumWeight = 0d;
        for (Particle particle : this.particles) {
            sumWeight += particle.probability;
        }
        for (Particle particle : this.particles) {
            particle.probability /= sumWeight;
        }
        particlesHistory = new ArrayList<Particle>();
        particlesHistory.addAll(particles);
    }

    public double calc_Neff() {
        double sumSqrProb = 0d;
        for (Particle particle : this.particles) {
            sumSqrProb += Math.sqrt(particle.probability);
        }
        return 1.0d / sumSqrProb;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < numParticles; i++) {
            res += particles.get(i).toString() + "\n";
        }
        return res;
    }
}
