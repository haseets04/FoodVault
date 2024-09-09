package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        //do show/hide for passwords
    }

    public void onRegisterClicked(View view) {
        EditText firstNameInput = findViewById(R.id.first_name_input);
        EditText lastNameInput = findViewById(R.id.last_name_input);
        EditText emailInput = findViewById(R.id.email_address_input);
        EditText passwordInput = findViewById(R.id.password_input);
        EditText confirmPasswordInput = findViewById(R.id.confirm_password_input);

        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String emailAddress = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        //validate input fields
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (lastName.isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (emailAddress.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.equals(confirmPassword)){  //can register
            //insert new user record to DB (user table)
            UserModel newUser = new UserModel();
            newUser.setUserFirstname(firstName);
            newUser.setUserLastname(lastName);
            newUser.setUserEmail(emailAddress);
            newUser.setUserPassword(password);
            newUser.setExpirationPeriod(30); //default expiration period

            SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

            //check if user already exists
            Call<List<UserModel>> checkUserCall = api.getUserByEmail("eq." + emailAddress, "*");
            checkUserCall.enqueue(new Callback<List<UserModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        //user already exists
                        Toast.makeText(RegisterProfileActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        //proceed with user registration
                        Call<Void> insertCall = api.insertUser(newUser);
                        insertCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RegisterProfileActivity.this, "Profile Registered", Toast.LENGTH_SHORT).show();

                                    // Clear the input fields
                                    firstNameInput.setText("");
                                    lastNameInput.setText("");
                                    emailInput.setText("");
                                    passwordInput.setText("");
                                    confirmPasswordInput.setText("");

                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                        Log.e("Supabase Error", "Registration Failed: " + response.message() + " - " + errorBody);
                                    } catch (Exception e) {
                                        Log.e("Supabase Error", "Error reading response body", e);
                                    }
                                    Toast.makeText(RegisterProfileActivity.this, "Registration Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(RegisterProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                    Toast.makeText(RegisterProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Log.i("Test Credentials", "First Name: " + firstName + ", Last Name: " + lastName + ", Email Address: " + emailAddress + ", Password: " + password);
        } else { // cannot register
            Toast.makeText(RegisterProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }

    }
}