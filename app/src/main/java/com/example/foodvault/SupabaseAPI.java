package com.example.foodvault;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * SupabaseApi interface to define the API endpoints
 * This interface defines the API endpoints for the Supabase API
 * It uses Retrofit annotations to define the HTTP methods and parameters
 * The methods defined in this interface will be used to make API calls
 * Typically,  GET annotations are used to fetch data from the API
 * The PUT annotation is used to insert data into the database
 * I would advise you to put all your api calls here, so login, add user, etc.
 *
 */
public interface SupabaseAPI {
    @GET("user") // Your table name is "Example"
    Call<List<UserModel>> getItems(@Query("select") String select); //first part of your query (select, update, insert, etc.)

    @GET("user")
    Call<List<UserModel>> getUserByEmail(@Query("user_email") String email, @Query("select") String select);

    @POST("user")
    Call<Void> insertUser(@Body UserModel user); //insert new user record

    @POST("product")
    Call<Void> insertProduct(@Body ProductModel product); //insert new product record

    @POST("shoppinglist")
    Call<Void> insertShoppingList(@Body ShopListModel shoppingList); //insert new shoppingList record

    @GET("shoppinglist")
    Call<List<ShopListModel>> getShoppingLists(@Query("select") String select);

    @GET("product")
    Call<List<ProductModel>> getProducts();

    @GET("products_on_shoppinglist")
    Call<List<ProductsOnShopListModel>> getProductOnListByShoplistID();

    /*@GET("shopping_lists")
    Call<List<ShopListModel>> getShoppingListRecentID(@Query("order") String order);*/

    /*@PATCH("/rest/v1/shoppinglist/{shoplist_id}")
    Call<Void> updateShoppingList(@Path("shoplist_id") Integer id, @Body ShopListModel updatedShoppingList);*/

    @GET("user")
    Call<List<UserModel>> getUserDetails(@Query("user_id") String userId);

    @PATCH("user")     //("/rest/v1/user?id=eq:{user_id}")
    Call<Void> updateUserExpirationPeriod(
            @Query("user_id") String userId,  // Dynamic query parameter
            @Body UserModel user      // Pass the updated fields as the body
    );



    //Call<Void> updateUserExpirationPeriod(@Path("user_id") Integer userId, @Body UserModel updatedUser);


}
