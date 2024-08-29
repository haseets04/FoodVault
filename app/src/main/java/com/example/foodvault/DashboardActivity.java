package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onViewInventoryClicked(View view) {
        startActivity(new Intent(DashboardActivity.this, ViewInventory.class));
    }


    public void onViewShopListClicked(View view) {
        startActivity(new Intent(DashboardActivity.this, ShoppingListActivity.class));
    }


    public void onViewGroupsClicked(View view) {
        //startActivity(new Intent(DashboardActivity.this, GroupsActivity.class);
    }
}