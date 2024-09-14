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

    @GET("/rest/v1/location")
    Call<List<LocationModel>> getLocations();



    @GET("/rest/v1/product")
    Call<List<ProductModel>> getProducts();

    @DELETE("/rest/v1/product")

    Call<Void> deleteproduct(@Query("product_id") String product_id);

    @PUT("/rest/v1/product")
    Call<Void> updateProduct(@Query("product_id") String product_id, @Body ProductModel product);

    @PUT("/rest/v1/location")
    Call<Void> updateLocation(@Query("location_id") String location_id, @Body LocationModel location);
    @DELETE("/rest/v1/user")
    Call<Void> deleteshoppinglistrecord(@Query("user_id") String product_id);
    @POST("products_on_shoppinglist")
    Call<com.example.foodvault.ShoppingListProductsModel> insertShoppingListItem(@Body com.example.foodvault.ShoppingListProductsModel inventory);
    @POST("/rest/v1/location")
    Call<com.example.foodvault.LocationModel> insertlocation(@Body com.example.foodvault.LocationModel location);

    @POST("/rest/v1/product")
    Call<com.example.foodvault.ProductModel> insertProduct(@Body com.example.foodvault.ProductModel product);

    @GET("rpc/getLastProduct")
    Call<List<ProductModel>> getLastProduct();




}
