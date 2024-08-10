package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    private Button viewInventoryBtn;
    private Button viewShopListBtn;
    private Button viewGroupsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewInventoryBtn = findViewById(R.id.view_inventory_btn);
        viewShopListBtn = findViewById(R.id.view_shoplist_btn);
        viewGroupsBtn = findViewById(R.id.view_groups_btn);

        //viewInventoryBtn.setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, InventoryActivity.class)));

        viewShopListBtn.setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, ShoppingListActivity.class)));

        //viewGroupsBtn.setOnClickListener(view -> startActivity(new Intent(DashboardActivity.this, GroupsActivity.class)));
    }
}