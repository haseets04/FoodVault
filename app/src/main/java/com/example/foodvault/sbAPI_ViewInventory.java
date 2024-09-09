package com.example.foodvault;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface sbAPI_ViewInventory {

    @GET("/rest/v1/location")
    Call<List<LocationModel>> getLocations();

    /*@GET("/rest/v1/inventory")
    Call<List<InventoryModel>> getInventory();*/

    @GET("/rest/v1/product")
    Call<List<ProductModel>> getProducts();

    @DELETE("/rest/v1/product")
    Call<Void> deleteproduct(@Query("product_id") String product_id);

   /* @DELETE("/rest/v1/inventory")
    Call<Void> deleteinvrecord(@Query("product_id") String product_id);*/

    @DELETE("/rest/v1/user")
    Call<Void> deleteshoppinglistrecord(@Query("user_id") String product_id);
}
