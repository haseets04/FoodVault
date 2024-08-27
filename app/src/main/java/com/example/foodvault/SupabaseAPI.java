package com.example.foodvault;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    @GET("/rest/v1/user") // Your table name is "Example"
    Call<List<UserModel>> getItems(@Query("select") String select); //first part of your query (select, update, insert, etc.)

    @POST("/rest/v1/user")
    Call<Void> insertUser(@Body UserModel user); //insert new user record

    @POST("/rest/v1/product")
    Call<Void> insertProduct(@Body ProductModel product); //insert new product record

}
