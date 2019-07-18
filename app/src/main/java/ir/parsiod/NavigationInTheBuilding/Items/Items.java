package ir.parsiod.NavigationInTheBuilding.Items;

import java.util.ArrayList;
import java.util.List;

import ir.parsiod.NavigationInTheBuilding.R;
import ir.parsiod.NavigationInTheBuilding.Views.ItemOfList;

public class Items {
   public List<ItemOfList> items;

    public Items() {
        items = new ArrayList<>();

        items.add(new ItemOfList("1","shoyande"
                ,"mishore","10000", R.drawable.buy,
                "51.9444465637207,-315.9444465637207"));

        items.add(new ItemOfList("2","chips"
                ,"khordani","500", R.drawable.buy,
                "43.9444465637207,4.055553436279297"));

        items.add(new ItemOfList("3","kerem"
                ,"mimaly","10000", R.drawable.buy,
                "52,356"));

        items.add(new ItemOfList("4","pophak"
                ,"khordani","10000", R.drawable.buy,
                "-212.0555534362793,-459.9444465637207"));



    }
}
