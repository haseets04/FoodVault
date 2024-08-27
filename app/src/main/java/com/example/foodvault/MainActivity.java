package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity { //Login page

    /*private static final String ConnectionString = "jdbc:mysql://localhost:3306/foodvault"; //check jdbc:mysql://localhost:3306/FoodVault //192.168.101.118
    private static final String DeviceDriver = "com.mysql.cj.jdbc.Driver"; //check
    private Connection connection = null;
    private Statement stmt;*/
    private Button loginBtn;
    private Button signUpButton;

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
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    List<UserModel> items = response.body();
                    Log.d("Supabase Response", response.toString());
                    if (items != null && !items.isEmpty()) {
                        //textView.setText("Connected. Items: " + items.get(0).user_id);
                        textView.setText("Successfully connected to the Supabase API. Items: \n" +
                                + items.get(1).getUserId() + items.get(1).getUserFirstname() + items.get(1).getUserLastname() + items.get(1).getUserEmail() + items.get(1).getUserPassword());
                    } else {
                        textView.setText("No items found.");
                    }
                } else {
                    textView.setText("Response unsuccessful: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                textView.setText("Error connecting to the Supabase API: " + t.getMessage());
            }
        });

    }

    public void onLoginClicked(View view) {
        String emailAddress = ((EditText) findViewById(R.id.email_address_input)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_input)).getText().toString();

        Log.i("Test Credentials", "Email Address: " + emailAddress + " and Password: " + password);

        startActivity(new Intent(MainActivity.this, AddProductActivity.class)); //Dashboard
    }


    public void onSignUpClicked(View view) {
        startActivity(new Intent(MainActivity.this, RegisterProfileActivity.class));
    }
}