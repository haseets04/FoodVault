package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        Button registerProfileButton = findViewById(R.id.register_profile_btn);
        registerProfileButton.setOnClickListener(view -> {
            String firstName = ((EditText) findViewById(R.id.first_name_input)).getText().toString();
            String lastName = ((EditText) findViewById(R.id.last_name_input)).getText().toString();
            String emailAddress = ((EditText) findViewById(R.id.email_address_input)).getText().toString();
            String password = ((EditText) findViewById(R.id.password_input)).getText().toString();
            String confirmPassword = ((EditText) findViewById(R.id.confirm_password_input)).getText().toString();

            if(password.equals(confirmPassword)){
                //can register
                //add to DB (user table)
                Log.i("Test Credentials", "First Name: " + firstName + ", Last Name: " + lastName +
                ", Email Address: " + emailAddress + ", Password: " + password); //get info into their data types
                Toast.makeText(RegisterProfileActivity.this, "Profile Registered", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(RegisterProfileActivity.this, Activity.class)); //specify next activity
            }
            else {
                //cannot register
                Toast.makeText(RegisterProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });



    }
}