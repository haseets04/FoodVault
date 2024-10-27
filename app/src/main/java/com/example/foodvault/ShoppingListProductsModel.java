package com.example.foodvault;

public class ShoppingListProductsModel {
    Integer products_on_list_id;
    String grocery_store;
   boolean  ticked_or_not;
    Integer shoplist_id;

    public Integer getShoplistprocuts_quantity() {
        return shoplistproducts_quantity;
    }

    public void setShoplistprocuts_quantity(Integer shoplistproducts_quantity) {
        this.shoplistproducts_quantity = shoplistproducts_quantity;
    }

    Integer shoplistproducts_quantity;

    public Integer getProducts_on_list_id() {
        return products_on_list_id;
    }

    public void setProducts_on_list_id(Integer products_on_list_id) {
        this.products_on_list_id = products_on_list_id;
    }

    public String getGrocery_store() {
        return grocery_store;
    }

    public void setGrocery_store(String grocery_store) {
        this.grocery_store = grocery_store;
    }

    public boolean getTicked_or_not() {
        return ticked_or_not;
    }

    public void setTicked_or_not(boolean ticked_or_not) {
        this.ticked_or_not = ticked_or_not;
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

    public ShoppingListProductsModel( String grocery_store, boolean ticked_or_not, Integer shoplist_id, Integer product_id,Integer shoplistproducts_quantity) {
        this.grocery_store = grocery_store;
        this.ticked_or_not = ticked_or_not;
        this.shoplist_id = shoplist_id;
        this.product_id = product_id;
        this.shoplistproducts_quantity=shoplistproducts_quantity;
    }

    Integer product_id;
}
