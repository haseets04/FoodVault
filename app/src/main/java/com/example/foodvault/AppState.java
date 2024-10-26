package com.example.foodvault;

public class AppState {
    private static AppState instance;
    private int shopListNameID = 1; //default
    private int groupNameID = 1; //default

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public int getShopListNameID() {
        return shopListNameID;
    }

    public void setShopListNameID(int shopListNameID) {
        this.shopListNameID = shopListNameID;
    }

    public int getGroupNameID() {
        return groupNameID;
    }

    public void setGroupNameID(int groupNameID) {
        this.groupNameID = groupNameID;
    }
}
