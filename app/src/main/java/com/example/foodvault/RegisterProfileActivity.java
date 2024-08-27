package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);
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

        if(password.equals(confirmPassword)){  //can register
            //insert new user record to DB (user table)
            UserModel newUser = new UserModel();
            newUser.setUserFirstname(firstName);
            newUser.setUserLastname(lastName);
            newUser.setUserEmail(emailAddress);
            newUser.setUserPassword(password);

            SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
            Call<Void> call = api.insertUser(newUser);
            call.enqueue(new Callback<Void>() {
                @Override
                /*public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterProfileActivity.this, "Profile Registered", Toast.LENGTH_SHORT).show();
                        // Redirect to another activity if needed
                    } else {
                        Toast.makeText(RegisterProfileActivity.this, "Registration Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }*/
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterProfileActivity.this, "Profile Registered", Toast.LENGTH_SHORT).show();
                        // Clear the input fields or redirect to another activity if needed
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
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Log.i("Test Credentials", "First Name: " + firstName + ", Last Name: " + lastName +
                    ", Email Address: " + emailAddress + ", Password: " + password); //get info into their data types
            //Toast.makeText(RegisterProfileActivity.this, "Profile Registered", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(RegisterProfileActivity.this, Activity.class)); //specify next activity
        }
        else {  //cannot register
            Toast.makeText(RegisterProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }

    }
}