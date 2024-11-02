package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity { //Login page
    private Integer userIdOnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLoginClicked(View view) {
        EditText loginEmailInput = findViewById(R.id.email_address_input);
        EditText loginPasswordInput = findViewById(R.id.password_input);

        String loginEmail = loginEmailInput.getText().toString();
        String loginPassword = loginPasswordInput.getText().toString();

        //validate input fields
        if (loginEmail.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (loginPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if user email in DB
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

        Call<List<UserModel>> checkUserCall = api.getUserByEmail("eq." + loginEmail, "*");

        checkUserCall.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                //Log.d("API Response", response.toString());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    //user email in DB
                    UserModel currentUser = response.body().get(0); //get the first match

                    //check if password correctly entered
                    if(loginPassword.equals(currentUser.getUserPassword())){
                        //work with this user's data in the database till logout
                        userIdOnLogin = currentUser.getUserId();
                        UserSession.getInstance().setUserSessionId(userIdOnLogin); //store user ID in singleton class
                        UserSession.getInstance().setExpiration_period(currentUser.getExpirationPeriod()); //to use in NotificationActivity

                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class)); //DashboardActivity

                        NotificationHelper notificationHelper = new NotificationHelper(MainActivity.this);
                        notificationHelper.checkExpiringProducts();

                    } else{
                        Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //user email not in DB
                    Toast.makeText(MainActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onSignUpClicked(View view) {
        startActivity(new Intent(MainActivity.this, RegisterProfileActivity.class));
    }
}