package com.example.foodvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GroceryStoreActivity extends AppCompatActivity {
    EditText groceryStoreName;
    TextView fallUnderGroceryStoreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_store);

        groceryStoreName = findViewById(R.id.grocery_store_name);
        fallUnderGroceryStoreName = findViewById(R.id.txt_fall_under_grocery_store_name);



    }

    public void onAddGroceryStoreClicked(View view) {
        //add below stuff in block



    }

    public void onSaveGrouping(View view) {
        //change the way the NewShoppingListActivity block looks
        //acd grocery store attribute to products_on_shopping_list table


    }

    public void onCancelGrouping(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the grouping?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(GroceryStoreActivity.this, "Grouping cancelled", Toast.LENGTH_SHORT).show();
                finish();            }
        });

        builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog2 = builder2.create();
        dialog2.show();
    }
}