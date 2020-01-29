package ir.parsiot.pokdis.Models;

public class EmkanatRefahi {
    private int id;
    private int imageRes;
    private String title;
    private String xy;
    private String imgSrc;



    public EmkanatRefahi(int id, int imageRes, String title, String xy, String imgSrc) {
        this.id = id;
        this.imageRes = imageRes;
        this.title = title;
        this.xy = xy;
        this.imgSrc = imgSrc;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getXy() {
        return xy;
    }
    public String getImgSrc() {
        return imgSrc;
    }
}
