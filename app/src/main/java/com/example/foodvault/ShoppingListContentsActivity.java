package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ShoppingListContentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_contents);

        TextView txtListName = findViewById(R.id.txt_list_name);
        //txtListName.setText(); //pass button text

    }
}