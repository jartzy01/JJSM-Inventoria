package model;

import java.io.Serializable;

public class Products implements Serializable {
    private String id;
    private String desc;
    private String img;
    private String name;
    private int qty;
    private double weight;

    public Products(String name, String desc, int qty, double weight) {
        this.desc = desc;
        this.name = name;
        this.qty = qty;
        this.weight = weight;
    }

    public Products() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
