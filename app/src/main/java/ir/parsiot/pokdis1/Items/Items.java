package ir.parsiot.pokdis1.Items;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis1.R;
import ir.parsiot.pokdis1.Views.ItemOfList;
// THIS CLASS strong information of sales list
public class Items {
   public List<ItemOfList> items;

    public Items() {
        items = new ArrayList<>();

        items.add(new ItemOfList("1","مایع ظرفشویی تاژ مدل Lemon"
                ,"حجم 3750 گرم",
                " تمیز کنندگی , چربی زدایی",
                "-","3750 گرم","تاژ",200000, "item_images/"+"1.jpg",
                "51.9444465637207,-315.9444465637207"));

        items.add(new ItemOfList("2","چیپس ساده نمکی چی توز"
                ,"مقدار 450 گرم",
                "Cheetoz Original Salty Potato Chips 450gr \n\n چیپس ساده نمکی ورقه\u200Cای",
                "42 × 31 سانتی\u200Cمتر","450 گرم","چی توز", 40000 ,"item_images/"+"2.jpg",
                "43.9444465637207,4.055553436279297"));

        items.add(new ItemOfList("3","کرم ضد چروک قوی دور چشم مای "
                ,"حجم 15 میلی لیتر",
                "MY Eye Contour Anti Wrinkle Cream \n\n  نرم کننده و سفت کننده و تقویت کننده پوست",
                "-","15 میلی لیتر","مای",
                700000, "item_images/"+"3.jpg",
                "52,356"));

        items.add(new ItemOfList("4","پوشک مای بیبی مدل Chamomile"
                ,"سایز 3 بسته 38 عددی",
                " مناسب برای وزن: 5 تا 9 کیلوگرم",
                "-","-","مای بیبی",
                670000,"item_images/"+"4.jpg",
                "-212.0555534362793,-459.9444465637207"));

    }
    public ItemOfList get_item(String itemId){
        for(ItemOfList item : this.items) {
            if(item.getId().equals(itemId)){
                return item;
            }
        }
        return null;
    }
}
