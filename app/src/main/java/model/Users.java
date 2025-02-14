package model;

public class Users {
    private int id;
    private String name;
    private String password;
    private String username;
    private String role;

    public Users(int id, String password){
        this.id = id;
        this.password = password;
    }

    public Users(int id, String password, String name, String username, String role){
        this.id = id;
        this.password = password;
        this.name = name;
        this.username = username;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}