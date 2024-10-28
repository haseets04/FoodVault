package com.example.foodvault;

public class UsersInGroupModel {
    private Integer users_group_id;
    private Integer group_id;
    private Integer user_id;
    private boolean is_admin;



    // Getters and setters
    public Integer getUsers_group_id() {
        return users_group_id;
    }

    public void setUsers_group_id(Integer users_group_id) {
        this.users_group_id = users_group_id;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    // Override equals to compare based on users_group_id
}
