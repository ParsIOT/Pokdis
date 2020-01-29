package ir.parsiot.pokdis.Localization.ParticleFilter;

public class ParticleFilterMath {
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static double Gaussian(double mu, double sigma, double x) {
        return Math.exp(-(Math.pow(mu - x, 2)) / Math.pow(sigma, 2) / 2.0) / Math.sqrt(360.0 * Math.pow(sigma, 2));
    }

    public static double round(final double number, final int precision){
        return Math.round(number * Math.pow(10, precision)) / Math.pow(10, precision);
    }


    public static double degreeDiff(double d1, double d2) {
        double diff = 0;
        diff = Math.abs(d1 - d2);
        if (diff > 180) {
            diff = 360 - diff;
        }
        return diff;
    }
}
