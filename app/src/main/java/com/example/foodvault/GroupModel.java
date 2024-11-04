package com.example.foodvault;

import java.util.Objects;

public class GroupModel {
    private Integer group_id;
    //private Integer user_id;
    //private Integer shoplist_id;
    private String group_name;

    public Integer getGroupId() {
        return group_id;
    }

    public void setGroupId(Integer group_id) {
        this.group_id = group_id;
    }

    /*public Integer getUserIdForGroup() {
        return user_id;
    }

    public void setUserIdForGroup(Integer user_id) {
        this.user_id = user_id;
    }*/

    /*public Integer getShoplistIdForGroup() {
        return shoplist_id;
    }

    public void setShoplistIdForGroup(Integer shoplist_id) {
        this.shoplist_id = shoplist_id;
    }*/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check for reference equality
        if (obj == null || getClass() != obj.getClass()) return false; // Type check
        GroupModel that = (GroupModel) obj;
        return Objects.equals(group_id, that.group_id); // Compare group_id
    }

    @Override
    public int hashCode() {
        return Objects.hash(group_id); // Include group_id in hash code
    }


    public String getGroupName() {
        return group_name;
    }

    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }
}
