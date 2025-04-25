package model;


public class Users {
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    private boolean notifyItemCreated;
    private boolean notifyItemDeleted;
    private boolean notifyOutOfStock;
    private boolean notifyRequestChange;

    public Users() {}

    public Users(String userId, String password, String firstName, String lastName, String email, String role) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;

        this.notifyItemCreated = true;
        this.notifyItemDeleted = true;
        this.notifyOutOfStock = true;
        this.notifyRequestChange = true;
    }


    public String  getName() {return firstName + " " + lastName;}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isNotifyItemCreated() {
        return notifyItemCreated;
    }

    public void setNotifyItemCreated(boolean notifyItemCreated) {
        this.notifyItemCreated = notifyItemCreated;
    }

    public boolean isNotifyItemDeleted() {
        return notifyItemDeleted;
    }

    public void setNotifyItemDeleted(boolean notifyItemDeleted) {
        this.notifyItemDeleted = notifyItemDeleted;
    }

    public boolean isNotifyOutOfStock() {
        return notifyOutOfStock;
    }

    public void setNotifyOutOfStock(boolean notifyOutOfStock) {
        this.notifyOutOfStock = notifyOutOfStock;
    }

    public boolean isNotifyRequestChange() {
        return notifyRequestChange;
    }

    public void setNotifyRequestChange(boolean notifyRequestChange) {
        this.notifyRequestChange = notifyRequestChange;
    }
}
