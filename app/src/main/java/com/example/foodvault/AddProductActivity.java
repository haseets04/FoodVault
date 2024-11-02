package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    Integer userId;
    private String productName, selectedCategory, selectedLocation;
    private Date expireDate;
    private boolean isProductExpired;
    private int quantityValue;
    private EditText productNameInput, expirationDateInput;
    private NumberPicker quantityPicker;
    private AutoCompleteTextView locationAutoView, categoryAutoView;
    private final sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
    private final List<String> locationNames = new ArrayList<>();
    private final HashMap<String, Integer> locationNameToIdMap = new HashMap<>();
    private Integer locationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameInput = findViewById(R.id.product_name_input);
        quantityPicker = findViewById(R.id.number_picker_quantity);
        expirationDateInput = findViewById(R.id.expiration_date_input);
        locationAutoView = findViewById(R.id.location_input);
        categoryAutoView = findViewById(R.id.category_input);

        quantityPicker.setMinValue(0);
        quantityPicker.setMaxValue(100);

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

        fetchLocations();
        fetchProducts(categoryAutoView);
    }

    private void fetchLocations() {
        Call<List<LocationModel>> locations = sbAPI.getLocations();
        locations.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationModel>> call, @NonNull Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //listLocations = response.body();
                    //locationNameToIdMap.clear();

                    for (LocationModel location : response.body()) {
                        locationNames.add(location.getLocation_name());
                        locationNameToIdMap.put(location.getLocation_name(), location.getLocation_id());
                    }
                    
                    ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(AddProductActivity.this,
                            android.R.layout.simple_dropdown_item_1line, locationNames);
                    locationAutoView.setAdapter(locationAdapter);
                    locationAutoView.setThreshold(0);
                } else {
                    Log.e("Add Product", "Locations response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationModel>> call, @NonNull Throwable t) {
                Log.e("Add Product", "Failed to fetch locations", t);
            }
        });
    }

    private void fetchProducts(AutoCompleteTextView categoryAutoView) {
        Call<List<ProductModel>> products = sbAPI.getProducts();
        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //listProducts = response.body();

                    List<String> categories = new ArrayList<>();
                    for (ProductModel product : response.body()) {
                        if (product.getProductCategory() != null && !categories.contains(product.getProductCategory())) {
                            categories.add(product.getProductCategory());
                        }                    }
                    
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(AddProductActivity.this,
                            android.R.layout.simple_dropdown_item_1line, categories);
                    categoryAutoView.setAdapter(categoryAdapter);
                    categoryAutoView.setThreshold(0);
                } else {
                    Log.e("Add Product", "Products response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Log.e("Add Product", "Failed to fetch products", t);
            }
        });
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(AddProductActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    private boolean validateInputs() {
        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter the product name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (quantityValue == 0) {
            Toast.makeText(this, "Please enter a quantity greater than 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (expirationDateInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter the expiration date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please enter the category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedLocation.isEmpty()) {
            Toast.makeText(this, "Please enter the location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onAddProductClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Save");
        builder2.setMessage("Are you sure you want to save the entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //retrieve values from UI
                productName = productNameInput.getText().toString();
                quantityValue = quantityPicker.getValue();
                selectedCategory = categoryAutoView.getText().toString();
                selectedLocation = locationAutoView.getText().toString();

                String dateString = expirationDateInput.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    expireDate = sdf.parse(dateString); //use this to add to DB
                } catch (ParseException e) {
                    e.printStackTrace();
                    //Toast.makeText(AddProductActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if product expired
                Date currentDate = new Date(); //get the current date
                Log.d("CurrentDate", "Date: " + currentDate);

                //compare the dates
                if (expireDate != null) {
                    if (expireDate.before(currentDate) || expireDate.equals(currentDate)) {
                        isProductExpired = true; // The chosen date is today or in the past
                    } else {
                        isProductExpired = false; // The chosen date is in the future
                    }
                } else {
                    // Handle the case where the date couldn't be parsed
                    isProductExpired = true; // Default to expired if date parsing fails
                    Toast.makeText(AddProductActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*Log.d("DateCheck", "isExpired: " + isProductExpired);

                Log.i("Product Info", "Name: " + productName + ", Quantity: " + quantityValue +
                        ", Expiration Date: " + expireDate + ", Category: " + selectedCategory);*/

                //validate inputs
                if(!validateInputs()){
                    return;
                }

                if (!locationNames.contains(selectedLocation)) {
                    Toast.makeText(AddProductActivity.this, "This is not an existing location. First add the location.", Toast.LENGTH_SHORT).show();
                    /*addNewLocation(() -> {
                        locationID = locationNameToIdMap.get(selectedLocation);
                    });
                    fetchLocations();

                    if(locationID == null){
                        Toast.makeText(this, "Location ID null", Toast.LENGTH_SHORT).show();
                    } else {
                        insertProduct();
                    }*/
                } else {
                    locationID = locationNameToIdMap.get(selectedLocation);
                    insertProduct();
                }

                Toast.makeText(AddProductActivity.this, "Product entry saved", Toast.LENGTH_SHORT).show();
                finish();
            }
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

    /*private void addNewLocation(Runnable onSuccess){
        Call<LocationModel> insertLocation= sbAPI.insertlocation(new LocationModel(selectedLocation));
        insertLocation.enqueue(new Callback<LocationModel>() {
            @Override
            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LocationModel addedLocation = response.body();
                    locationNames.add(addedLocation.getLocation_name());
                    locationNameToIdMap.put(addedLocation.getLocation_name(), addedLocation.getLocation_id());

                    Toast.makeText(AddProductActivity.this, "Location added successfully", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<LocationModel> call, Throwable t) {
                Log.e("Location Insert Error", t.getMessage());
                Toast.makeText(AddProductActivity.this, "Error adding location: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Toast.makeText(AddProductActivity.this, "New Location Added: " + selectedLocation, Toast.LENGTH_SHORT).show();
    }*/

    private void insertProduct(){
        //insert into Product table
        ProductModel newProduct = new ProductModel();
        newProduct.setUserIdForProduct(getCurrentUserIDFromSession());
        newProduct.setLocationId(locationID);
        newProduct.setProductName(productName);
        newProduct.setProductExpirationDate(expireDate);
        newProduct.setProductCategory(selectedCategory);
        newProduct.setProductExpired(isProductExpired);
        newProduct.setProductQuantity(quantityValue);

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

        Call<Void> insertProductCall = api.insertProduct(newProduct);
        insertProductCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    // Clear the input fields
                    productNameInput.setText("");
                    quantityPicker.setValue(0);
                    expirationDateInput.setText("");
                    categoryAutoView.setText("");
                    locationAutoView.setText("");
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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

    public void onCancelProductClicked(View view) {  //don't add to DB
        //confirmation dialog
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AddProductActivity.this, "Product entry cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
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