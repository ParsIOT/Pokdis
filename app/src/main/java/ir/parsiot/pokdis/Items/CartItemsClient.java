package ir.parsiot.pokdis.Items;

import java.util.ArrayList;

import ir.parsiot.pokdis.Views.ItemOfList;

public interface CartItemsClient{
    public ArrayList<ItemOfList> deleteItem(ItemOfList item);
}