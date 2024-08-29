package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShoppingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
    }

    public void onAddListClicked(View view) {
        startActivity(new Intent(ShoppingListActivity.this, NewShoppingListActivity.class)); //Dashboard
    }

    public void onEditListClicked(View view) {
    }

    public void onRemoveListClicked(View view) {
    }
}