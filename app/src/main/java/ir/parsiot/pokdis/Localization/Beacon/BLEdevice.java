package ir.parsiot.pokdis.Localization.Beacon;

import ir.parsiot.pokdis.Constants.Constants;

public class BLEdevice {

    private String name;
    private String UUID;
    private String major;
    private String minor;
    private RssiList data = new RssiList(Constants.MAX_RSSI_LIST_SIZE);
    private String mac;



    private String distance ="far";
    private int rss;

    public boolean canThatCanRemoveThisDivice(){
        if(data.getCountOfZero() ==Constants.MAX_RSSI_LIST_SIZE){
            return true;
        }
        return false;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public int getRssiAvg() {
        return data.average();
    }

    public void InsertRssi(int rss) {
       data.insert(rss);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    @Override
    public String toString() {
        String a;
    if(getRssiAvg()>-60){
        a = "uuid: " +UUID +"\n major: "+major +"\n minor: "+minor +"\n avg of rssi: "+ getRssiAvg()+"\n"+"near";
    }else{
        a = "uuid: " +UUID +"\n major: "+major +"\n minor: "+minor +"\n avg of rssi: "+ getRssiAvg()+"\n"+"far";
    }



     return a;

    }

}
