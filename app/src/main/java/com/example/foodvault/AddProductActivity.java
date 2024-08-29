package com.example.foodvault;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    String productName;
    //Barcode
    int quantityValue;
    Date expireDate;
    String selectedCategory;
    boolean isProductExpired = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        EditText productNameInput = findViewById(R.id.product_name_input);
        productName = productNameInput.getText().toString();

        //goes in inventory table
        NumberPicker quantity = findViewById(R.id.number_picker_quantity);
        quantity.setMinValue(0);
        quantity.setMaxValue(100);
        quantityValue = quantity.getValue();

        EditText expirationDate = findViewById(R.id.expiration_date_input);
        expirationDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddProductActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String dateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        expirationDate.setText(dateString);
                    }, year, month, day);
            datePickerDialog.show();

        });
        String dateString = expirationDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            expireDate = sdf.parse(dateString);
            // Use expireDate to insert into the database
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the error
        }

        //expireDate = (Date) expirationDate.getText();
        //String stringExpireDate = expirationDate.getText().toString();

        Spinner category = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected (if necessary)
            }
        });

    }

    public void onAddProductClicked(View view) {
        //insert new product record to DB (product and inventory table)
        InventoryModel newInventory = new InventoryModel();
        newInventory.setQuantity(quantityValue);

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<InventoryModel> inventoryCall = api.insertInventory(newInventory);
        inventoryCall.enqueue(new Callback<InventoryModel>() {
            @Override
            public void onResponse(Call<InventoryModel> call, Response<InventoryModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the generated product_id from the Inventory insertion
                    int generatedProductId = response.body().getProductId();

                    // Now insert into Product table using the generated product_id
                    ProductModel newProduct = new ProductModel();
                    newProduct.setProductId(generatedProductId); // Set the product_id from Inventory
                    newProduct.setProductName(productName);
                    newProduct.setProductExpirationDate(expireDate);
                    newProduct.setProductCategory(selectedCategory);
                    newProduct.setProduct_expired(isProductExpired);

                    Call<Void> insertProductCall = api.insertProduct(newProduct);
                    insertProductCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddProductActivity.this, "Product added to Inventory", Toast.LENGTH_SHORT).show();
                                // Clear the input fields or redirect to another activity if needed
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                    Log.e("Supabase Error", "Product Insertion Failed: " + response.message() + " - " + errorBody);
                                } catch (Exception e) {
                                    Log.e("Supabase Error", "Error reading response body", e);
                                }
                                Toast.makeText(AddProductActivity.this, "Product Insertion Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AddProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(AddProductActivity.this, "Inventory Insertion Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InventoryModel> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*Log.i("Test Credentials", "Product Name: " + productName + ", Quantity: " + stringQuantity +
                    ", Expiration Date: " + stringExpireDate + ", Category: " + stringCategory);*/ //get info into their datatypes
    }



    public void onCancelProductClicked(View view) {
        //don't add to DB
        Toast.makeText(AddProductActivity.this, "Product entry cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCameraClicked(View view) {
        try{
            Intent intentCameraApp = new Intent();
            intentCameraApp.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intentCameraApp);
        } catch (Exception e){
            e.printStackTrace();
        }
        //make it scan a barcode on camera app
    }
}