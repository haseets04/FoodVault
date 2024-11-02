package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onExpirationReminderClicked(View view) {
        startActivity(new Intent(SettingsActivity.this, ExpirationPeriodActivity.class));
    }

    public void onLogOutClicked(View view) {
        //blank email and password
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    public void onHomeClicked(View view) {
        startActivity(new Intent(SettingsActivity.this, DashboardActivity.class));
    }
}