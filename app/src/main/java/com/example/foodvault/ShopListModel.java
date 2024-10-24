package com.example.foodvault;

public class ShopListModel {
    private Integer shoplist_id;
    private Integer user_id;
    private String shoplist_name;

    public Integer getShoplistId() {
        return shoplist_id;
    }

    public void setShoplistId(Integer shoplist_id) {
        this.shoplist_id = shoplist_id;
    }

    public Integer getUserIdForShopList() {
        return user_id;
    }

    public void setUserIdForShopList(Integer user_id) {
        this.user_id = user_id;
    }

    public String getShoplistName() {
        return shoplist_name;
    }

    public void setShoplistName(String shoplist_name) {
        this.shoplist_name = shoplist_name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopListModel that = (ShopListModel) o;
        return shoplist_id != null && shoplist_id.equals(that.shoplist_id);
    }
}
