package com.example.foodvault;

import java.util.Date;

public class ProductModel {
    private Integer product_id;
    private String product_name;
    private int product_barcode; //add
    private Date product_expiration_date;
    private String product_category;
    private boolean product_expired;

    public ProductModel() {
    }

    public Integer getProductId() {
        return product_id;
    }

    public void setProductId(Integer product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public int getProductBarcode() {
        return product_barcode;
    }

    public void setProductBarcode(int product_barcode) {
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

    public boolean isProduct_expired() {
        return product_expired;
    }

    public void setProduct_expired(boolean product_expired) {
        this.product_expired = product_expired;
    }
}