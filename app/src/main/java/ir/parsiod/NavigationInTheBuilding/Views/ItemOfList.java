package ir.parsiod.NavigationInTheBuilding.Views;

import android.widget.TextView;

public class ItemOfList {
    private int itemImage;
    private String name;
    private String description;
    private String price;
    private String location;
    private String id;



    public ItemOfList(String id,String name, String description, String price, int itemImage, String location) {
        this.itemImage = itemImage;
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.id = id;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "name: "+name +" location:"+location + " itemImage:"+itemImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
