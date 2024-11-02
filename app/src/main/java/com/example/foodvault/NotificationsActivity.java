package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private NotificationHelper notificationHelper;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationHelper = new NotificationHelper(this);
        checkNotificationPermission();

        notificationListView = findViewById(R.id.notificationListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        //adapter.notifyDataSetChanged();
        notificationListView.setAdapter(adapter);

        // Fetch the products reaching expiration and display them
        getProductsReachingExpirationFromDB();

        Toast.makeText(this, "Period: " + expirationPeriod, Toast.LENGTH_SHORT).show();

    }

    private void checkNotificationPermission() {
        // Only need to check for notification permission on Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                // Refresh notifications when permission is granted
                notificationHelper.checkExpiringProducts();
            } else {
                Toast.makeText(this, "Notification permission denied. You won't receive notifications.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getProductsReachingExpirationFromDB(){
        /*String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentDateMinusExpirationPeriod = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (long) expirationPeriod * 24 * 60 * 60 * 1000));
        */
        //maybe also add if item has expired?

        Call<List<ProductModel>> productsCall = api.getProducts();
        productsCall.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> listProducts = response.body(); //gets all the products from the database
                    //notificationsList.clear();

                    Date currentDate = new Date();
                    //only return products for the current user and where the expiration date is between the current date and currentDateMinusEP

                    if (!listProducts.isEmpty()) {
                        for (ProductModel product : listProducts) {
                            if(product.getUserIdForProduct() == userId){

                                Date expirationDate = product.getProductExpirationDate();

                                // Calculate days until expiration
                                long diffInMillies = expirationDate.getTime() - currentDate.getTime();
                                long daysUntilExpiration = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                                // Check if product is within the notification period
                                if (daysUntilExpiration >= 0 && daysUntilExpiration <= expirationPeriod) {
                                    String productDetails = "⚠️ EXPIRING SOON ⚠️\n" +
                                            "Product: " + product.getProductName() + "\n" +
                                            "Expiration Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expirationDate) + "\n" +
                                            "Days until expiration: " + daysUntilExpiration + "\n" +
                                            "Quantity: " + product.getProductQuantity();

                                    notificationsList.add(productDetails);

                                }

                            }

                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        if (notificationsList.isEmpty()) {
                            notificationsList.add("No products are nearing expiration.");
                            adapter.notifyDataSetChanged();
                        }

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

    }

}