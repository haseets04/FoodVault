package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private String productName;
    //Barcode
    private int quantityValue;
    private Date expireDate;
    private String selectedCategory;
    private boolean isProductExpired = false;
    EditText productNameInput, expirationDateInput;
    NumberPicker quantityPicker;
    Spinner category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameInput = findViewById(R.id.product_name_input);

        //goes in inventory table
        quantityPicker = findViewById(R.id.number_picker_quantity);
        quantityPicker.setMinValue(0);
        quantityPicker.setMaxValue(100);

        expirationDateInput = findViewById(R.id.expiration_date_input);
        expirationDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddProductActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String dateString = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        expirationDateInput.setText(dateString);
                    }, year, month, day);
            datePickerDialog.show();

        });

        category = findViewById(R.id.spinner_category);
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

    public void onAddProductClicked(View view) { //doesnt add to product table
        // Retrieve values from UI
        productName = productNameInput.getText().toString();
        quantityValue = quantityPicker.getValue();

        String dateString = expirationDateInput.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            expireDate = sdf.parse(dateString); //use this to add to DB
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(AddProductActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
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

        Log.i("Product Info", "Name: " + productName + ", Quantity: " + quantityValue +
                ", Expiration Date: " + expireDate + ", Category: " + selectedCategory);

        // Insert into Inventory table
        InventoryModel newInventory = new InventoryModel();
        newInventory.setQuantity(quantityValue);

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<InventoryModel> inventoryCall = api.insertInventory(newInventory);
        inventoryCall.enqueue(new Callback<InventoryModel>() {
            @Override
            public void onResponse(Call<InventoryModel> call, Response<InventoryModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer generatedProductId = response.body().getProductId();

                    // Insert into Product table
                    ProductModel newProduct = new ProductModel();
                    newProduct.setProductId(generatedProductId);
                    newProduct.setProductName(productName);
                    newProduct.setProductExpirationDate(expireDate);
                    newProduct.setProductCategory(selectedCategory);
                    newProduct.setProductExpired(isProductExpired);

                    Call<Void> insertProductCall = api.insertProduct(newProduct);
                    insertProductCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                // Clear input fields or redirect if needed
                            } else {
                                handleApiError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AddProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<InventoryModel> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("Supabase Error", "Failed: " + response.message() + " - " + errorBody);
            Toast.makeText(AddProductActivity.this, "Failed: " + response.message(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Supabase Error", "Error reading response body", e);
        }
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