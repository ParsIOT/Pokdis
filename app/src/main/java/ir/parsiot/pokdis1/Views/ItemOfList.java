package ir.parsiot.pokdis1.Views;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ItemOfList {
    private String imageName;
    private String name;
    private String description;
    private String longDescription;
    private String dimens;
    private String weigth;
    private String brand;
    private double price;
    private String location;
    private String id;



    public ItemOfList(String id, String name,
                      String description, String longDescription, String dimens, String weight, String brand,
                      double price, String imageName, String location) {
        this.imageName = imageName;
        this.name = name;
        this.description = description;
        this.longDescription = longDescription;
        this.price = price;
        this.location = location;
        this.id = id;
        this.dimens = dimens;
        this.weigth = weight;
        this.brand = brand;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String itemImage) {
        this.imageName = itemImage;
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

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getDimens() {
        return dimens;
    }

    public void setDimens(String dimens) {
        this.dimens = dimens;
    }


    public String getWeigth() {
        return weigth;
    }

    public void setWeigth(String weigth) {
        this.weigth = weigth;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }



    public String getPrice() {
//        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###", symbols);
        String formattedPrice = decimalFormat.format(price);
//        String formattedPrice = decimalFormat.format(price);
        return formattedPrice + " ریال ";
    }

    public void setPrice(double price) {
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
        return "name: "+name +" location:"+location + " itemImage:"+imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
