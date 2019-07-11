package ir.parsiod.NavigationInTheBuilding.beacon;


public class RssiList {
    private  int MAX_RSSI_LIST_SIZE =6;
    private int[] rssi;
    private int count =0;
    private int countOfZero =0;



    public RssiList(int MAX_RSSI_LIST_SIZE){
        this.MAX_RSSI_LIST_SIZE = MAX_RSSI_LIST_SIZE;
        rssi = new int[MAX_RSSI_LIST_SIZE];
        count =0;

        for(int i = 0; i< MAX_RSSI_LIST_SIZE; i++){
            rssi[i]=0;
        }
        countOfZero = MAX_RSSI_LIST_SIZE;
    }
    public void insert (int value){

          rssi[count]=value;
          countOfZero =0;
          for(int i = 0; i< MAX_RSSI_LIST_SIZE; i++){
              if(rssi[i]==0){
                  countOfZero++;
              }
          }

        count++;
        if(count == MAX_RSSI_LIST_SIZE){
            count =0;
        }
    }
    public int getCountOfZero() {
        return countOfZero;
    }
    public int average(){
        if(countOfZero == MAX_RSSI_LIST_SIZE){
            return 0;
        }
        int sum=0;
        int n = MAX_RSSI_LIST_SIZE - countOfZero;
        for(int i = 0; i< MAX_RSSI_LIST_SIZE; i++){
            sum+=rssi[i];
        }
        return sum/n;
    }


    @Override
    public String toString() {
        String str="";
        for(int i = 0; i< MAX_RSSI_LIST_SIZE; i++){
            str+= String.valueOf(rssi[i])+" ";
        }

        return str;
    }
}
