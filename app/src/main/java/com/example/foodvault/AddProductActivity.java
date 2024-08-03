package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class AddProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        String productName = ((EditText) findViewById(R.id.product_name_input)).getText().toString();

        NumberPicker quantity = findViewById(R.id.number_picker_quantity);
        quantity.setMinValue(0);
        quantity.setMaxValue(100);
        //String stringQuantity = quantity.toString();

        EditText expirationDate = findViewById(R.id.expiration_date_input);
        expirationDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddProductActivity.this,
                    (view, year1, month1, dayOfMonth) -> expirationDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            datePickerDialog.show();
        });
        //String stringExpireDate = expirationDate.getText().toString();

        Spinner category = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        //String stringCategory = category.toString();

        ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intentCameraApp = new Intent();
                    intentCameraApp.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intentCameraApp);
                } catch (Exception e){
                    e.printStackTrace();
                }
                //make it scan a barcode on camera app
            }
        });

        Button addProduct = findViewById(R.id.btn_add_product);
        addProduct.setOnClickListener(view -> {
            //add to DB (product table)
            /*Log.i("Test Credentials", "Product Name: " + productName + ", Quantity: " + stringQuantity +
                    ", Expiration Date: " + stringExpireDate + ", Category: " + stringCategory);*/ //get info into their datatypes
            Toast.makeText(AddProductActivity.this, "Product added to Inventory", Toast.LENGTH_SHORT).show();
        });

        Button cancelProduct = findViewById(R.id.btn_cancel_product);
        cancelProduct.setOnClickListener(view -> {
            //don't add to DB
            Toast.makeText(AddProductActivity.this, "Product entry cancelled", Toast.LENGTH_SHORT).show();
        });



    }

}