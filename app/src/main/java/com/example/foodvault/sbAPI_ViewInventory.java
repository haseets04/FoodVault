package com.example.foodvault;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface sbAPI_ViewInventory {

    @GET("location")
    Call<List<LocationModel>> getLocations();


    @PUT("products_on_shoppinglist")
    Call<Void> updateSLproduct(@Query("products_on_list_id") String products_on_list_id,@Body ShoppingListProductsModel product);
    @GET("product")
    Call<List<ProductModel>> getProducts();
    @GET("products_on_shoppinglist")
    Call<List<ShoppingListProductsModel>> getshoppinglistproducts();


    @DELETE("product")
    Call<Void> deleteproduct(@Query("product_id") String product_id);

    @DELETE("products_on_shoppinglist")
    Call<Void> deleteSLproduct(@Query("products_on_list_id") String products_on_list_id);



    @PUT("product")
    Call<Void> updateProduct(@Query("product_id") String product_id, @Body ProductModel product);
    @PUT("shoppinglist")
    Call<Void> updateShoplist(@Query("shoplist_id") String shoplist_id, @Body ShopListModel product);

    @PUT("location")
    Call<Void> updateLocation(@Query("location_id") String location_id, @Body LocationModel location);
    @DELETE("user")
    Call<Void> deleteshoppinglistrecord(@Query("user_id") String product_id);
    @POST("products_on_shoppinglist")
    Call<com.example.foodvault.ShoppingListProductsModel> insertShoppingListItem(@Body com.example.foodvault.ShoppingListProductsModel inventory);
    @POST("location")
    Call<com.example.foodvault.LocationModel> insertlocation(@Body com.example.foodvault.LocationModel location);

    @POST("product")
    Call<com.example.foodvault.ProductModel> insertProduct(@Body com.example.foodvault.ProductModel product);

    @GET("rpc/getLastProduct")
    Call<List<ProductModel>> getLastProduct();

    @POST("rpc/addProduct")
    Call<Void> addProduct(@Body ProductModel product, @Query("shoplist_id") Integer shoplist_id);




}
