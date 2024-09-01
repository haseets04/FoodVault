package com.example.foodvault;

public class ShopListModel {
    private Integer shoplist_id;
    private String shoplist_name;
    private String created_at;  // Ensure this field exists and is populated

    public Integer getShoplistId() {
        return shoplist_id;
    }

    public void setShoplistId(Integer shoplist_id) {
        this.shoplist_id = shoplist_id;
    }

    public String getShoplistName() {
        return shoplist_name;
    }

    public void setShoplistName(String shoplist_name) {
        this.shoplist_name = shoplist_name;
    }

    public String getCreatedAt() { return created_at; }

    public void setCreatedAt(String created_at) { this.created_at = created_at; }
}
