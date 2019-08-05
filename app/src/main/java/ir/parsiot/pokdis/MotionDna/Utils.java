package ir.parsiot.pokdis.MotionDna;

public class Utils {
    public static double Convert2zeroto360(double h) {
        while (true) {
            if (h < 0) {
                h += 360;
            } else if (h > 360) {
                h -= 360;
            } else {
                return h;
            }
        }
    }

    public static double DegreeDiff(double d1, double d2) {
        double diff = 0;
        diff = Math.abs(d1 - d2);
        if (diff > 180) {
            diff = 365 - diff;
        }
        return diff;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}
