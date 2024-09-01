package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity { //Login page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //get first user record
        TextView textView = findViewById(R.id.text_view);

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<UserModel>> call = api.getItems("*"); //second half of your sql statement
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    List<UserModel> items = response.body();
                    Log.d("Supabase Response", response.toString());
                    if (items != null && !items.isEmpty()) {
                        //textView.setText("Connected. Items: " + items.get(0).user_id);
                        textView.setText("Successfully connected to the Supabase API. Items: \n" +
                                items.get(1).getUserId() + items.get(1).getUserFirstname() + items.get(1).getUserLastname() + items.get(1).getUserEmail() + items.get(1).getUserPassword());
                    } else {
                        textView.setText("No items found.");
                    }
                } else {
                    textView.setText("Response unsuccessful: " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                textView.setText("Error connecting to the Supabase API: " + t.getMessage());
            }
        });

    }

    public void onLoginClicked(View view) {
        String emailAddress = ((EditText) findViewById(R.id.email_address_input)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_input)).getText().toString();

        //make sure to check with DB if its there

        Log.i("Test Credentials", "Email Address: " + emailAddress + " and Password: " + password);

        startActivity(new Intent(MainActivity.this, DashboardActivity.class)); //DashboardActivity
    }


    public void onSignUpClicked(View view) {
        startActivity(new Intent(MainActivity.this, RegisterProfileActivity.class));
    }
}