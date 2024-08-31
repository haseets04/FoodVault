package com.example.foodvault;

public class UserModel {
    private Integer user_id;
    private String user_firstname;
    private String user_lastname;
    private String user_email;
    private String user_password;
    //private int expiration_period; //need?

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUserFirstname() {
        return user_firstname;
    }

    public void setUserFirstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUserLastname() {
        return user_lastname;
    }

    public void setUserLastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getUserPassword() {
        return user_password;
    }

    public void setUserPassword(String user_password) {
        this.user_password = user_password;
    }

    /*public int getExpirationPeriod() {
        return expiration_period;
    }

    public void setExpirationPeriod(int expiration_period) {
        this.expiration_period = expiration_period;
    }*/
}
