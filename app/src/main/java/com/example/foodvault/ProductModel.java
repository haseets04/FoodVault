package com.example.foodvault;

import java.util.Date;

public class ProductModel {
    private Integer product_id;
    private Integer user_id;
    private Integer location_id; //can be null
    private String product_name;
    private Integer product_barcode; //add //can be null //String rather?
    private Date product_expiration_date;
    private String product_category;
    private boolean product_expired;
    private int quantity;

    public ProductModel() {
    }

    public Integer getProductId() {
        return product_id;
    }

    public void setProductId(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getUserIdForProduct() {
        return user_id;
    }

    public void setUserIdForProduct(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getLocationId() {
        return location_id;
    }

    public void setLocationId(Integer location_id) {
        this.location_id = location_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public Integer getProductBarcode() {
        return product_barcode;
    }

    public void setProductBarcode(Integer product_barcode) {
        this.product_barcode = product_barcode;
    }

    public Date getProductExpirationDate() {
        return product_expiration_date;
    }

    public void setProductExpirationDate(Date product_expiration_date) {
        this.product_expiration_date = product_expiration_date;
    }

    public String getProductCategory() {
        return product_category;
    }

    public void setProductCategory(String product_category) {
        this.product_category = product_category;
    }

    public boolean getProductExpired() {
        return product_expired;
    }

    public void setProductExpired(boolean product_expired) {
        this.product_expired = product_expired;
    }

    public int getProductQuantity() { //change to getQuantity later
        return quantity;
    }

    public void setProductQuantity(int quantity) {
        this.quantity = quantity;
    }
}
