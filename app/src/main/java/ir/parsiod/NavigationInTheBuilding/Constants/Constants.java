package ir.parsiod.NavigationInTheBuilding.Constants;


/**
 * Created by seyedalian on 11/6/2019.
 */

public class Constants {
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION =1;
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE =2;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH =3;


    public static final int MAX_RSSI_LIST_SIZE =6;

    public  static  final String ALTBEACON_LAYOUT="m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static  final  long PERIOD_TIME_BETWEEN_SCAN = 300l;

    public static  final String COMMON_UUID_BEACON ="23a01af0-232a-4518-9c0e-323fb773f5ef";
    public static  final String COMMON_MAJOR_BEACON ="1";


    public static final long PERIOD_OF_GET_TOP_BEACON = 1000l;




}
