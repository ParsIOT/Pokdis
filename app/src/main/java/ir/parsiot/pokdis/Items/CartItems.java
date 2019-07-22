package ir.parsiot.pokdis.Items;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.Constants.Constants;
import ir.parsiot.pokdis.Views.ItemOfList;


public class CartItems {
    private ArrayList<ItemOfList> items;
    public CartItems(){
        items = new ArrayList<ItemOfList>();
        if (Hawk.contains(Constants.CART_ITEMS_KEY)){
            items = Hawk.get(Constants.CART_ITEMS_KEY);
        }else{
            Hawk.put(Constants.CART_ITEMS_KEY, items);
        }
    }

    public void flush(){
        Hawk.put(Constants.CART_ITEMS_KEY, items);
    }

    public boolean put_item(ItemOfList item){

        boolean notFound = true;
        for (ItemOfList tempItem: items ){
            if (item.getId().equals(tempItem.getId())){
                notFound = false;
            }
        }
        if(notFound){
            items.add(item);
            flush();
        }

        return notFound;
    }
    public List<ItemOfList> get_items(){
        return items;
    }

    public ArrayList<ItemOfList> delete_item(ItemOfList item){
       for(int i=0; i<items.size(); i++){
           if (items.get(i).getId().equals(item.getId())){
               items.remove(i);
               flush();
               return items;
           }
       }
       return items;
    }

    public void clean(){
        items = new ArrayList<ItemOfList>();
        flush();
    }

}
