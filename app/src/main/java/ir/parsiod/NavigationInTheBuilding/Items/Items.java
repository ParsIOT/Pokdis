package ir.parsiod.NavigationInTheBuilding.Items;

import java.util.ArrayList;
import java.util.List;

import ir.parsiod.NavigationInTheBuilding.R;
import ir.parsiod.NavigationInTheBuilding.Views.ItemOfList;
// THIS CLASS strong information of sales list
public class Items {
   public List<ItemOfList> items;

    public Items() {
        items = new ArrayList<>();

        items.add(new ItemOfList("1","مایع ظرف شویی"
                ,"شوینده","10000 تومان", R.drawable.buy,
                "51.9444465637207,-315.9444465637207"));

        items.add(new ItemOfList("2","چیپس"
                ,"خوراکی","500 تومان", R.drawable.buy,
                "43.9444465637207,4.055553436279297"));

        items.add(new ItemOfList("3","کرم "
                ,"زیبایی بهداشتی","10000 تومان", R.drawable.buy,
                "52,356"));

        items.add(new ItemOfList("4","پوفک"
                ,"خوراکی","10000 تومان", R.drawable.buy,
                "-212.0555534362793,-459.9444465637207"));



    }
}
