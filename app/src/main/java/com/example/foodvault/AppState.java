package com.example.foodvault;

public class AppState {
    private static AppState instance;
    private int expirationPeriod = 30; //default value
    private int shopListNameID = 1; //default

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public int getExpirationPeriod() {
        return expirationPeriod;
    }

    public void setExpirationPeriod(int expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
    }

    public int getShopListNameID() {
        return shopListNameID;
    }

    public void setShopListNameID(int shopListNameID) {
        this.shopListNameID = shopListNameID;
    }
}
