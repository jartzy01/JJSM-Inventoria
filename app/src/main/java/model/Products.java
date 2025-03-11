package model;

public class Products {
    private int id;
    private String category;
    private String desc;
    private String img;
    private String name;
    private int qty;
    private double weight;

    public Products(int id, String category, String desc, String img, String name, int qty, double weight) {
        this.id = id;
        this.category = category;
        this.desc = desc;
        this.img = img;
        this.name = name;
        this.qty = qty;
        this.weight = weight;
    }

    public Products(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
