package ir.parsiot.pokdis.Localization.Beacon;

import ir.parsiot.pokdis.Constants.Constants;

public class RssiList {
    private int MAX_RSSI_LIST_SIZE = 6;
    private int[] rssi;
    private int count = 0;
    private int countOfZero = 0;


    public RssiList(int MAX_RSSI_LIST_SIZE) {
        this.MAX_RSSI_LIST_SIZE = MAX_RSSI_LIST_SIZE;
        rssi = new int[MAX_RSSI_LIST_SIZE];
        count = 0;

        for (int i = 0; i < MAX_RSSI_LIST_SIZE; i++) {
            rssi[i] = 0;
        }
        countOfZero = MAX_RSSI_LIST_SIZE;
    }

    public void insert(int value) {

        rssi[count] = value;
        countOfZero = 0;
        for (int i = 0; i < MAX_RSSI_LIST_SIZE; i++) {
            if (rssi[i] == 0) {
                countOfZero++;
            }
        }

        count++;
        if (count == MAX_RSSI_LIST_SIZE) {
            count = 0;
        }
    }

    public int getCountOfZero() {
        return countOfZero;
    }

    public int average() {
        if (countOfZero == MAX_RSSI_LIST_SIZE) {
            return 0;
        }
        int sum = 0;
        int n = MAX_RSSI_LIST_SIZE - countOfZero;
        for (int i = 0; i < MAX_RSSI_LIST_SIZE; i++) {
            sum += rssi[i];
        }
        return sum / n;
    }
 /*   public int average(){
        if(countOfZero == MAX_RSSI_LIST_SIZE){
            return 0;
        }
        int sum=0;
        sortRssi();
        int n = MAX_RSSI_LIST_SIZE - countOfZero;
        if(n%2==0){
            int m = n/2;
            int avg = (rssi[m-1]+rssi[m])/2;
            return avg;

        }else {
            int m = n/2;
            return rssi[m];

        }

    }*/

    void sortRssi() {
        for (int i = 0; i < MAX_RSSI_LIST_SIZE; i++) {
            for (int j = 0; j < MAX_RSSI_LIST_SIZE; j++) {
                if (rssi[i] > rssi[j]) {
                    int temp = rssi[i];
                    rssi[i] = rssi[j];
                    rssi[j] = temp;
                }
            }
        }

    }

    public static Double convertRssToDist1(int rss) {
        Double ratio_linear = Math.pow(10, (Constants.BEACON_TX_POWER - rss) / 10);
        Double dist = Math.sqrt(ratio_linear);
        return dist;
    }


    public static Double convertRssToDist2(int rss) {
        Double ratio = (double)(rss)/(double)Constants.BEACON_TX_POWER ;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            Double dist =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return dist;
        }
    }


    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < MAX_RSSI_LIST_SIZE; i++) {
            str += String.valueOf(rssi[i]) + " ";
        }

        return str;
    }
}
