package com.example.foodvault;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
public interface sbAPI_ViewInventory {

    @GET("/rest/v1/location")
    Call<List<LocationModel>> getLocations();

    @GET("/rest/v1/inventory")
    Call<List<InventoryModel>> getInventory();

    @GET("/rest/v1/product")
    Call<List<ProductModel>> getProducts();
}
