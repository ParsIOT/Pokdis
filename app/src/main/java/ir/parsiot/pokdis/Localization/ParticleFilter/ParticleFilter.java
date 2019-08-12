package ir.parsiot.pokdis.Localization.ParticleFilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ir.parsiot.pokdis.map.MapConsts;
import ir.parsiot.pokdis.map.WallGraph.RectObstacle;

import static ir.parsiot.pokdis.Localization.MotionDna.Utils.Convert2zeroto360;

public class ParticleFilter {
    private static final String TAG = "ParticleFilter";

    ArrayList<Particle> particles;
    ArrayList<Particle> particlesHistory;
    int numParticles = 0;
    int numInitParticles = 0;
    int numParticlesThreshold;

    Random gen = new Random();
    private ArrayList<Double> initScatterFactor;
    private ArrayList<Double> resampleScatterFactor;
    public HashMap<String, Double[]> landmarks;
    private Double Fnoise;
    private Double Tnoise;
    private Double Snoise;

    private static MapConsts mapConsts = new MapConsts();


    public ParticleFilter(int numParticles, int divisionResampleParticleNumThreshold, ArrayList<Double> initScatterFactor, ArrayList<Double> resampleScatterFactor, HashMap<String, Double[]> landmarks) {
        this.numInitParticles = numParticles;
        this.numParticles = numParticles;
        this.numParticlesThreshold = this.numInitParticles / divisionResampleParticleNumThreshold;
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

        Log.e(TAG, "Threshold: " + numParticlesThreshold);
        Log.e(TAG, "Number of particles : " + numParticles);
        Log.e(TAG, "InitNumber of particles : " + numInitParticles);

        if (this.numParticles == 0) {
            particles.clear();
            particles.addAll(particlesHistory);
            numParticles = particles.size();
        }

        if (this.numParticles < numParticlesThreshold) {
            resample();
        }
        Log.e(TAG, "After resample Number of particles : " + numParticles);

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

    // Todo: normalize the weights after weight update and resamapling


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
        float rand = (float) (random.nextFloat()/ numInitParticles);
        float splitLine = (float) (1.0 / numInitParticles);

        float[] positions = new float[numInitParticles];
        for (int i = 0; i < numInitParticles; i++) {
            positions[i] = (float) (i * splitLine) + rand;
        }

        float[] cumsumWeight = new float[numParticles];

        float convertFactor = ((float) numInitParticles/(float) numParticles); // Manytimes numParticles is lower than numInitParticles. So it causes that sum of particle probabilities not to be 1
        cumsumWeight[0] = particles.get(0).probability * convertFactor;
        for (int i = 1; i < numParticles; i++) {
            cumsumWeight[i] = cumsumWeight[i - 1] + particles.get(i).probability * convertFactor;
        }
        cumsumWeight[numParticles-1] = 1.0f; // Ensures sum is exactly one

        int i = 0;
        int j = 0;

        int[] indexes = new int[numInitParticles];
        while (i < numInitParticles) {
            Log.e(TAG, "i:"+i+", j:"+j);
            Log.e(TAG, "positions[i]:"+positions[i]+", cumsumWeight[j]:"+cumsumWeight[j]);
            if (positions[i] < cumsumWeight[j]) {
                indexes[i] = j;
                i++;
            } else {
                j++;
            }
        }

        return indexes;
    }

    //    def systematic_resample(weights):
//    N = len(weights)
//
//            positions = (np.arange(N) + random()) / N
//
//    indexes = np.zeros(N, 'i')
//    cumulative_sum = np.cumsum(weights)
//    i, j = 0, 0
//            while i < N:
//            if positions[i] < cumulative_sum[j]:
//    indexes[i] = j
//    i += 1
//            else:
//    j += 1
//            return indexes
//
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
        float prob = 0f;
        for (int i = 0; i < numParticles; i++) { //Todo: probability must be multiply to x,y and h

            Particle particle = particles.get(i);
            x += particle.x;
            y += particle.y;
            h += particle.h;
            prob += particle.probability;
        }
        x /= numParticles;
        y /= numParticles;
        h /= numParticles;
        prob /= numParticles;
        try {
            p.set(x, y, h, prob);
        } catch (Exception ex) {
//            Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
        }

        p.setNoise(particles.get(0).Fnoise, particles.get(0).Tnoise, particles.get(0).Snoise);


        return p;
    }

    public ArrayList<ArrayList<Double>> getParticles() {
        ArrayList<ArrayList<Double>> particlesData = new ArrayList<ArrayList<Double>>();
        for (Particle particle : particles) {
            ArrayList<Double> tempParticleData = new ArrayList<Double>();
            tempParticleData.add(particle.x);
            tempParticleData.add(particle.y);
            tempParticleData.add(particle.h);
            particlesData.add(tempParticleData);
        }
        return particlesData;
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
