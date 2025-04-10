package model;

public class Category {
    //private String id;
    private String name;

    public Category() {
    }

    public Category(String name) {

        this.name = name;
    }

    // Getters
    //public String getId() {return id;}
    public String getName() {
        return name;
    }

    // Setter
    //public void setId(String id) {this.id = id;}
    public void setName(String name) {
        this.name = name;
    }
}
