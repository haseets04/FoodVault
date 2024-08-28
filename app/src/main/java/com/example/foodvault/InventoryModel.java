package com.example.foodvault;

public class InventoryModel {
    private Integer product_id;
    private int qty;

    public InventoryModel() {
    }

    public Integer getProductId() {
        return product_id;
    }

    public void setProductId(Integer product_id) {
        this.product_id = product_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
