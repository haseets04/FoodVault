package com.example.foodvault;

public class ProductsOnShopListModel {
    private Integer products_on_list_id;
    private Integer shoplist_id;
    private Integer product_id;
    private String product_category;
    private String grocery_store;
    private boolean ticked_or_not;
    private int shoplistproducts_quantity;

    public int getShoplistproducts_quantity() {
        return shoplistproducts_quantity;
    }

    public void setShoplistproducts_quantity(int shoplistproducts_quantity) {
        this.shoplistproducts_quantity = shoplistproducts_quantity;
    }


    public ProductsOnShopListModel( String grocery_store, boolean ticked_or_not, Integer shoplist_id, Integer product_id,Integer shoplistproducts_quantity) {
        this.grocery_store = grocery_store;
        this.ticked_or_not = ticked_or_not;
        this.shoplist_id = shoplist_id;
        this.product_id = product_id;
        this.shoplistproducts_quantity=shoplistproducts_quantity;
    }

    public Integer getProducts_on_list_id() {
        return products_on_list_id;
    }

    public void setProducts_on_list_id(Integer products_on_list_id) {
        this.products_on_list_id = products_on_list_id;
    }

    public Integer getShoplist_id() {
        return shoplist_id;
    }

    public void setShoplist_id(Integer shoplist_id) {
        this.shoplist_id = shoplist_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getGrocery_store() {
        return grocery_store;
    }

    public void setGrocery_store(String grocery_store) {
        this.grocery_store = grocery_store;
    }

    public boolean isTicked_or_not() {
        return ticked_or_not;
    }

    public void setTicked_or_not(boolean ticked_or_not) {
        this.ticked_or_not = ticked_or_not;
    }
}
