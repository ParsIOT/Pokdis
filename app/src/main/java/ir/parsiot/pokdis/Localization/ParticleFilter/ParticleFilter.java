package ir.parsiot.pokdis.Localization.ParticleFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static ir.parsiot.pokdis.Localization.ParticleFilter.ParticleFilterMath.circle;

public class ParticleFilter {
    Particle[] particles;
    int numParticles = 0;
    Random gen = new Random();
    private  ArrayList<Double> initScatterFactor;
    public int worldWidth;
    public int worldHeight;
    public HashMap<String, Double[]> landmarks;
    private Double Fnoise;
    private Double Tnoise;
    private Double Snoise;


    public ParticleFilter(int numParticles, ArrayList<Double> initScatterFactor, HashMap<String, Double[]> landmarks, int width, int height) {
        this.numParticles = numParticles;
        this.initScatterFactor = initScatterFactor;
        this.worldWidth = width;
        this.worldHeight = height;
        this.landmarks = landmarks;
    }

    public void setNoise(Double Fnoise, Double Tnoise, Double Snoise) {
        this.Fnoise = Fnoise;
        this.Tnoise = Tnoise;
        this.Snoise = Snoise;
    }

    public void createParticles(ArrayList<Double> initState) throws Exception {
        if (Fnoise == null || Tnoise == null || Snoise == null){
            throw new Exception("Should setNoise before createParticles");
        }

        particles = new Particle[numParticles];
        gen = new Random();

        for (int i = 0; i < numParticles; i++) {
            ArrayList<Double> particleInitState = new ArrayList<Double>();
            particleInitState.add(initState.get(0) + initScatterFactor.get(0) * gen.nextFloat());
            particleInitState.add(initState.get(1) + initScatterFactor.get(1) * gen.nextFloat());
            particleInitState.add(circle(initState.get(2) + initScatterFactor.get(2) * gen.nextFloat()));
            particles[i] = new Particle(particleInitState, this.landmarks, this.worldWidth, this.worldHeight, Fnoise, Tnoise, Snoise);
        }
    }


    public void move(Double dx, Double dy, Double dh) {
        for (int i = 0; i < numParticles; i++) {
            particles[i].move(dx, dy, dh);
        }
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
        Particle particle = particles[0];
        for (int i = 0; i < numParticles; i++) {
            if (particles[i].probability > particle.probability) {
                particle = particles[i];
            }
        }
        return particle;
    }

    public Particle getAverageParticle() {
        Particle p = new Particle();
        Double x = 0d, y = 0d, h = 0d, prob = 0d;
        for(int i=0;i<numParticles;i++) {
            x += particles[i].x;
            y += particles[i].y;
            h += particles[i].h;
            prob += particles[i].probability;
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

        p.setNoise(particles[0].Fnoise, particles[0].Tnoise, particles[0].Snoise);

        return p;
    }

    public ArrayList<ArrayList<Double>> getParticles(){
        ArrayList<ArrayList<Double>> particlesData = new ArrayList<ArrayList<Double>>();
        for(Particle particle:particles){
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
            res += particles[i].toString() + "\n";
        }
        return res;
    }
}
