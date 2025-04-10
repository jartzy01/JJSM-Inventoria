package model;

public class Item {
    private String id;
    private String name;
    private String categoryId;

    public Item() {}  // Required for Firebase

    public Item(String id, String name, String categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
}
