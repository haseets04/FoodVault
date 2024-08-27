package com.example.foodvault;

import java.util.Date;

public class ProductModel {
    private int product_id;
    private String product_name;
    private int product_barcode;
    private Date product_expiration_date;
    private String product_category;
    private boolean product_expired;

    public ProductModel() {
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getProduct_barcode() {
        return product_barcode;
    }

    public void setProduct_barcode(int product_barcode) {
        this.product_barcode = product_barcode;
    }

    public Date getProduct_expiration_date() {
        return product_expiration_date;
    }

    public void setProduct_expiration_date(Date product_expiration_date) {
        this.product_expiration_date = product_expiration_date;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public boolean isProduct_expired() {
        return product_expired;
    }

    public void setProduct_expired(boolean product_expired) {
        this.product_expired = product_expired;
    }
}
