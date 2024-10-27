package com.example.foodvault;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.HTTP;

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

    @GET("/rest/v1/user")
    Call<List<UserModel>> getUserByEmail(@Query("user_email") String email, @Query("select") String select);

    @POST("/rest/v1/user")
    Call<Void> insertUser(@Body UserModel user); //insert new user record

    @POST("/rest/v1/product")
    Call<Void> insertProduct(@Body ProductModel product); //insert new product record

    @POST("shoppinglist")
    Call<Void> insertShoppingList2(@Body ShopListModel shoppingList); //insert new shoppingList record

    @POST("/rest/v1/shoppinglist")
    @Headers("Prefer: return=representation")
    Call<List<ShopListModel>> insertShoppingList(@Body ShopListModel shopList); //insert new shopping list record

    @PATCH("/rest/v1/shoppinglist")
    Call<Void> updateShopListDetails(
            @Query("shoplist_id") String shoppingList, @Body ShopListModel shopList);

    @DELETE("/rest/v1/shoppinglist")
    Call<Void> deleteShoppingList(@Query("shoplist_id") String shoppingList);

    @GET("/rest/v1/shoppinglist")
    Call<List<ShopListModel>> getShoppingLists(@Query("select") String select);

    @GET("/rest/v1/product")
    Call<List<ProductModel>> getProducts();

    @GET("/rest/v1/products_on_shoppinglist")
    Call<List<ProductsOnShopListModel>> getProductOnListByShoplistID();

    @GET("/rest/v1/user")
    Call<List<UserModel>> getUserDetails(@Query("user_id") String userId);

    @PATCH("/rest/v1/user")     //("/rest/v1/user?id=eq:{user_id}")
    Call<Void> updateUserDetails(
            @Query("user_id") String userId,  // Dynamic query parameter
            @Body UserModel user      // Pass the updated fields as the body
    );

    @POST("/rest/v1/group")
    @Headers("Prefer: return=representation")
    Call<List<GroupModel>> insertGroup(@Body GroupModel group); //insert new group record

    @PATCH("/rest/v1/group")
    Call<Void> updateGroupDetails(
            @Query("group_id") String group, @Body GroupModel groupModel);

    @DELETE("/rest/v1/group")
    Call<Void> deleteGroup(@Query("group_id") String group);

    @POST("/rest/v1/users_in_group")
    Call<Void> insertUserInGroup(@Body UsersInGroupModel userInGroup); //insert new user in group record

    @GET("/rest/v1/shoppinglist")
    Call<List<ShopListModel>> getShopListCountByUserId(@Query("select") String select, @Query("user_id") String userId);

    @GET("/rest/v1/users_in_group")
    Call<List<UsersInGroupModel>> getGroupCountByUserId(@Query("select") String select, @Query("user_id") String userId);

    /*@GET("/rest/v1/product")
    Call<List<ProductModel>> getProductsReachingExpiration(
            @Query("product_expiration_date") String expirationDateCondition,
            @Query("select") String select);*/


    @GET("/rest/v1/group")
    Call<List<GroupModel>> getGroups(@Query("select") String select);

    @GET("/rest/v1/users_in_group")
    Call<List<UsersInGroupModel>> getUsersInGroups(@Query("select") String select);

    @GET("/rest/v1/users_in_group")
    Call<List<UsersInGroupModel>> getMembersByGroupID(@Query("group_id") String groupId);

    @GET("/rest/v1/shoppinglist")
    Call<List<ShopListModel>> getShopListByGroupID(@Query("group_id") String groupId);

    // Delete multiple members in a group
    @HTTP(method = "DELETE", path = "users_in_group", hasBody = true)
    Call<Void> deleteMembersInGroup(@Query("user_id") String userIdFilter, @Query("group_id") String groupIdFilter);

    @DELETE("users_in_group")
    Call<Void> deleteGroupMembers(@Query("user_id") String userID);

}
