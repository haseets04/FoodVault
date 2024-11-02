package com.example.foodvault;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHelper {
    private static final String CHANNEL_ID = "FoodVaultNotifications";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;
    private final SupabaseAPI api;

    public NotificationHelper(Context context) {
        this.context = context;
        this.api = SupabaseClient.getClient().create(SupabaseAPI.class);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FoodVault Notifications";
            String description = "Notifications for products nearing expiration";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String productName, Date expirationDate, long daysUntilExpiration) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Product Expiring Soon")
                .setContentText(productName + " is expiring in " + daysUntilExpiration + " days")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
        } else {
            try {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            } catch (SecurityException e) {
                Log.e("Notification Error", "Failed to send notification", e);
            }
        }
    }

    public void checkExpiringProducts() {
        Integer userId = UserSession.getInstance().getUserSessionId();
        int expirationPeriod = UserSession.getInstance().getExpiration_period();

        Call<List<ProductModel>> productsCall = api.getProducts();
        productsCall.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> listProducts = response.body();
                    Date currentDate = new Date();

                    if (!listProducts.isEmpty()) {
                        for (ProductModel product : listProducts) {
                            if (product.getUserIdForProduct() == userId) {
                                Date expirationDate = product.getProductExpirationDate();
                                long diffInMillies = expirationDate.getTime() - currentDate.getTime();
                                long daysUntilExpiration = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                                if (daysUntilExpiration >= 0 && daysUntilExpiration <= expirationPeriod) {
                                    sendNotification(
                                            product.getProductName(),
                                            expirationDate,
                                            daysUntilExpiration
                                    );
                                }
                            }
                        }
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Log.e("Supabase Error", "Failed to fetch product details", t);
            }
        });
    }

}
