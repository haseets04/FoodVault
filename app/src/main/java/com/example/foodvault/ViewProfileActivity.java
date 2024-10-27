package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView nameSurname, numShoppingLists, numGroups;
    private Integer userId;
    private final SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        nameSurname = findViewById(R.id.profile_name_surname);
        numShoppingLists = findViewById(R.id.num_shopping_lists);
        numGroups = findViewById(R.id.num_groups);

        fetchAndDisplayUserNameSurname();
        fetchAndDisplayUserNumShopLists();
        fetchAndDisplayUserNumGroups();
    }

    public void fetchAndDisplayUserNumGroups(){ //just gets groups created, not if its linked with a shoplist
        getCurrentUserIDFromSession();
        Call<List<UsersInGroupModel>> call = api.getGroupCountByUserId("*", "eq." + userId);
        call.enqueue(new Callback<List<UsersInGroupModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsersInGroupModel>> call, @NonNull Response<List<UsersInGroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size(); //count of groups for the user
                    numGroups.setText(String.valueOf(count));

                    //Toast.makeText(ViewProfileActivity.this, "Number of Groups: " + count, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewProfileActivity.this, "Failed to fetch count", Toast.LENGTH_SHORT).show();
                    Log.e("Supabase Error", "Failed to fetch count: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UsersInGroupModel>> call, @NonNull Throwable t) {
                Toast.makeText(ViewProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchAndDisplayUserNumShopLists(){
        getCurrentUserIDFromSession();
        Call<List<ShopListModel>> call = api.getShopListCountByUserId("*", "eq." + userId);
        call.enqueue(new Callback<List<ShopListModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopListModel>> call, @NonNull Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size(); //count of shopping lists for the user
                    numShoppingLists.setText(String.valueOf(count));

                    //Toast.makeText(ViewProfileActivity.this, "Number of Shopping Lists: " + count, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewProfileActivity.this, "Failed to fetch count", Toast.LENGTH_SHORT).show();
                    Log.e("Supabase Error", "Failed to fetch count: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopListModel>> call, @NonNull Throwable t) {
                Toast.makeText(ViewProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchAndDisplayUserNameSurname(){
        getCurrentUserIDFromSession();
        Call<List<UserModel>> getUserCall = api.getUserDetails("eq." + userId);
        getUserCall.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserModel currentUser = response.body().get(0);
                    nameSurname.setText(currentUser.getUserFirstname() + " " + currentUser.getUserLastname());

                } else {
                    Toast.makeText(ViewProfileActivity.this, "Failed to load user name and surname", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(ViewProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(ViewProfileActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    public void onUpdateProfileClicked(View view) {
        startActivity(new Intent(ViewProfileActivity.this, UpdateProfileActivity.class));
    }

    public void onNotificationIconClicked(View view) {
        startActivity(new Intent(ViewProfileActivity.this, NotificationsActivity.class));
    }
}