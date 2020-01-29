package ir.parsiot.pokdis.Items;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.Constants.Constants;


public class CartItems {
    private ArrayList<ItemClass> items;
    public CartItems(Context context){
        items = new ArrayList<ItemClass>();
        if (!Hawk.isBuilt()){
            Hawk.init(context).build();
        }
        if (Hawk.contains(Constants.CART_ITEMS_KEY)){
            items = Hawk.get(Constants.CART_ITEMS_KEY);
        }else{
            Hawk.put(Constants.CART_ITEMS_KEY, items);
        }
    }

    public void flush(){
        Hawk.put(Constants.CART_ITEMS_KEY, items);
    }

    public boolean put_item(ItemClass item){

        boolean notFound = true;
        for (ItemClass tempItem: items ){
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
    public List<ItemClass> get_items(){
        return items;
    }

    public ArrayList<ItemClass> delete_item(ItemClass item){
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
        items = new ArrayList<ItemClass>();
        flush();
    }

}
