package ir.parsiot.pokdis.Constants;


public class Constants {
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 3;


    public static final int MAX_RSSI_LIST_SIZE = 6;
    public static final int MIN_RSS_TO_RELOCATE = -56;
//    public static final int MIN_RSS_TO_RELOCATE = -69;

    public static final int MAX_PROXIMITY_TO_ROUTE_THRESHOLD = 200;
    public static final int MIN_COUNT_TO_IGNORE_PATH = 20;

    public  static  final String ALT_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static  final long PERIOD_TIME_BETWEEN_SCAN = 150l;
//    private static final long PERIOD_TIME_BETWEEN = 150l;


    public static  final String COMMON_UUID_BEACON = "23a01af0-232a-4518-9c0e-323fb773f5ef";
    public static  final String COMMON_MAJOR_BEACON = "1";

    public static final String CART_ITEMS_KEY = "cart_items_key";

    public static final long PERIOD_OF_GET_TOP_BEACON = 1000l;
    public static final long DELAY_ON_SHOW_TOP_BEACON = 0l;

    public static final Integer BEACON_TX_POWER = -75; // Usually ranges between -59 to -65
    // Note: Map constants are in map folder files


}
