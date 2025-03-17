package model;

public class Company {
    private int id;
    private String name;
    private String address;
    private String companyLogo;
    public Users users;
    public Products products;

    public Company(int id, String name, String address, String companyLogo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.companyLogo = companyLogo;
    }

    public Company(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }
}
