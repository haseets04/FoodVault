package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {
    private ListView notificationListView;
    private final List<String> notificationsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private final Integer userId = UserSession.getInstance().getUserSessionId();
    private final int expirationPeriod = UserSession.getInstance().getExpiration_period();
    private final SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationListView = findViewById(R.id.notificationListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        //adapter.notifyDataSetChanged();
        notificationListView.setAdapter(adapter);

        // Fetch the products reaching expiration and display them
        getProductsReachingExpirationFromDB();

        Toast.makeText(this, "Period: " + expirationPeriod, Toast.LENGTH_SHORT).show();

    }

    private void getProductsReachingExpirationFromDB(){
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentDateMinusExpirationPeriod = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (long) expirationPeriod * 24 * 60 * 60 * 1000));

        Call<List<ProductModel>> productsCall = api.getProducts();
        productsCall.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> listProducts = response.body(); //gets all the products from the database

                    //only return products for the current user and where the expiration date is between the current date and currentDateMinusEP

                    if (!listProducts.isEmpty()) {
                        for (ProductModel product : listProducts) {
                            if(product.getUserIdForProduct() == userId){
                                String productDetails = "Product: " + product.getProductName() + "\n" +
                                        "Expiration Date: " + new SimpleDateFormat("yyyy-MM-dd").format(product.getProductExpirationDate()) + "\n" +
                                        "Quantity: " + product.getProductQuantity();
                                notificationsList.add(productDetails);
                            }



                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Error loading product details", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch product details", t);
            }
        });



        /*String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
        //change to using the expiration period

        String expirationDateCondition = "gte." + thirtyDaysAgo + ",lt." + currentDate;
        //gte (greater than or equal to) and lt (less than) operators to filter products where the expiration date is between 30 days ago and today.

        Call<List<ProductModel>> call = api.getProductsReachingExpiration(expirationDateCondition, "*");

        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful()) {
                    List<ProductModel> expiringProducts = response.body();

                    // Handle the list of products
                    if (expiringProducts != null && !expiringProducts.isEmpty()) {
                        for (ProductModel product : expiringProducts) {
                            String productDetails = "Product: " + product.getProductName() + "\n" +
                                    "Expiration Date: " + new SimpleDateFormat("yyyy-MM-dd").format(product.getProductExpirationDate()) + "\n" +
                                    "Quantity: " + product.getProductQuantity();
                            notificationsList.add(productDetails);
                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NotificationsActivity.this, "No expiring products found", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.e("Supabase Error", "Failed to fetch products");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Log.e("Supabase Error", "Error: " + t.getMessage());
            }
        });*/



    }





}