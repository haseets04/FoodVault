package com.example.foodvault;

public class UserModel {
    public int user_id;
    //public String body_type_my;
    public String user_firstname;
    public String user_lastname;
    public String user_email;
    public String user_password;

    public UserModel(String user_firstname, String user_lastname, String user_email, String user_password) {
        //this.user_id = user_id;
        this.user_firstname = user_firstname;
        this.user_lastname = user_lastname;
        this.user_email = user_email;
        this.user_password = user_password;
    }

    public UserModel() {

    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
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
}
