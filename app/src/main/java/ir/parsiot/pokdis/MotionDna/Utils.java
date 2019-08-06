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

    public static Double[] findCloseDot(Double[] refDot, Double[] currentLoc, Double[] nearLoc) {
        Double[] resultXY = new Double[]{Double.NaN, Double.NaN};

        Double x0, y0;
        Double x1, y1;
        x0 = refDot[0];
        y0 = refDot[1];

        x1 = nearLoc[0];
        y1 = nearLoc[1];

        Double d2 = Math.pow(x0 - currentLoc[0], 2) + Math.pow(y0 - currentLoc[1], 2);

        if (x1 - x0 == 0) {
            resultXY[0] = x0;
            resultXY[1] = Math.sqrt(d2);
            return resultXY;
        }

//        if (Math.sqrt(d2) <= MinValidRelocation) {
//            // it handles some errors in the below algorithm too
//            return resultXY; //return NaN,NaN
//        }
        Double alpha = (y1 - y0) / (x1 - x0);

        Double a = 1 + Math.pow(alpha, 2);
        Double b = -2 * x0 * a;
        Double c = Math.pow(x0, 2) * a - d2;

        Double delta = b * b - 4 * a * c;
        if (delta < 0) {
            return resultXY;
        }
        Double temp1 = Math.sqrt(delta);

        Double resX1 = (-b + temp1) / (2 * a);
        Double resY1 = alpha * (resX1 - x0) + y0;
        Double resX2 = (-b - temp1) / (2 * a);
        Double resY2 = alpha * (resX2 - x0) + y0;


        Double dist1 = Math.pow(x1 - resX1, 2) + Math.pow(y1 - resY1, 2);
        Double dist2 = Math.pow(x1 - resX2, 2) + Math.pow(y1 - resY2, 2);

        if (dist1 > dist2) {
            resultXY[0] = resX2;
            resultXY[1] = resY2;
        } else {
            resultXY[0] = resX1;
            resultXY[1] = resY1;
        }
        return resultXY;
    }

}
