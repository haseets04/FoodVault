package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {

    private sbAPI_ViewInventory sbAPI;
    private List<ProductModel> listproducts = new ArrayList<>();
    private List<InventoryModel> inventory = new ArrayList<>();
    private List<LocationModel> listlocations = new ArrayList<>();
    Intent intent;
    SeekBar seekBar;
    TextView seekBarValueTextView ;
    AutoCompleteTextView locationAutoView ;
    AutoCompleteTextView categoryAutoView ;
    TextView tvDate ;
    TextView Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        intent= getIntent();
        seekBar = findViewById(R.id.sk_quantity);
        seekBarValueTextView = findViewById(R.id.tvquantity);
        locationAutoView = findViewById(R.id.LocationTextView);
        categoryAutoView = findViewById(R.id.CategoryTextView);

        Product=findViewById(R.id.product_name_input);

        seekBar.setProgress(intent.getIntExtra("quantity",0));
        locationAutoView.setText(intent.getStringExtra("location"));
        categoryAutoView.setText(intent.getStringExtra("category"));

        Product.setText(intent.getStringExtra("name"));


        sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        // Set initial value of TextView
        seekBarValueTextView.setText("Quantity: " + seekBar.getProgress());

        fetchLocations(locationAutoView);
        fetchInventory();
        fetchProducts(categoryAutoView);
        tvDate = findViewById(R.id.expiration_date_input);
        // Set an OnClickListener on the TextView for DatePicker
        Calendar calendar = Calendar.getInstance();
        tvDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProductActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                tvDate.setText(dateFormat.format(calendar.getTime()));
            }, year, month, day);

            datePickerDialog.show();
        });

        tvDate.setText(intent.getStringExtra("expiration"));

        // Set up a listener for SeekBar changes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValueTextView.setText("Quantity: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle the event when the user starts touching the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Handle the event when the user stops touching the SeekBar
            }
        });
        Button btnsavechanges=findViewById(R.id.btn_edit_product);
        btnsavechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(EditProductActivity.this, "Record Successfully edited", Toast.LENGTH_SHORT).show();
                InventoryModel invupdate=null;
                ProductModel produpdate=null;
                LocationModel locupdate=null;
                for (ProductModel product :
                        listproducts) {
                    if (product.getProductId() == intent.getIntExtra("product_id", 0)) {
                        product.setProductCategory(categoryAutoView.getText().toString());
                        product.setProductName(Product.getText().toString());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        try {
                            product.setProductExpirationDate(dateFormat.parse(tvDate.getText().toString()));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        produpdate=product;
                    }
                }
                for (LocationModel location:
                     listlocations) {
                    if(location.getLocation_id()==intent.getIntExtra("location_id",0))
                    {
                        location.setLocation_name(locationAutoView.getText().toString());
                        locupdate=location;
                    }

                }
                for (InventoryModel inv:
                     inventory) {
                    if(inv.getProductId()==intent.getIntExtra("product_id",0))
                    {
                        inv.setQuantity(seekBar.getProgress());
                        invupdate=inv;
                    }

                }
                Call<Void> updateinv=sbAPI.updateInventory("eq." + invupdate.getProductId(),invupdate);
                Call<Void> updateloc=sbAPI.updateLocation("eq." + locupdate.getLocation_id(),locupdate);
                Call<Void> updateprod=sbAPI.updateProduct("eq." +produpdate.getProductId(),produpdate);

                updateinv.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        boolean test= response.isSuccessful();
                        if(!response.isSuccessful())
                            return;
                        updateloc.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                boolean test2= response.isSuccessful();
                                if(!response.isSuccessful())
                                    return;
                                updateprod.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        boolean test3= response.isSuccessful();
                                        if(!response.isSuccessful())
                                            return;
                                        Toast.makeText(EditProductActivity.this, "Record Successfully edited", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditProductActivity.this, ViewInventory.class));
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("Edit Product","Couldnt edit location");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Edit Product","Couldnt edit inventory");
                    }
                });

            }
        });
    }

    public void onCanceleditClicked(View view) {
        //don't add to DB
        Toast.makeText(EditProductActivity.this, "Product edit process was cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void fetchLocations(AutoCompleteTextView locationAutoView) {
        Call<List<LocationModel>> locations = sbAPI.getLocations();
        locations.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listlocations = response.body();
                    List<String> locationNames = new ArrayList<>();
                    for (LocationModel location : listlocations) {
                        locationNames.add(location.getLocation_name());
                    }
                    ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(EditProductActivity.this,
                            android.R.layout.simple_dropdown_item_1line, locationNames);
                    locationAutoView.setAdapter(locationAdapter);
                    locationAutoView.setThreshold(0);
                } else {
                    Log.e("Edit Product", "Locations response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
                Log.e("Edit Product", "Failed to fetch locations", t);
            }
        });
    }

    private void fetchProducts(AutoCompleteTextView categoryAutoView) {
        Call<List<ProductModel>> products = sbAPI.getProducts();
        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listproducts = response.body();
                    List<String> categories = new ArrayList<>();
                    for (ProductModel product : listproducts) {
                        categories.add(product.getProductCategory());
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(EditProductActivity.this,
                            android.R.layout.simple_dropdown_item_1line, categories);
                    categoryAutoView.setAdapter(categoryAdapter);
                    categoryAutoView.setThreshold(0);
                } else {
                    Log.e("Edit Product", "Products response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("Edit Product", "Failed to fetch products", t);
            }
        });
    }

    private void fetchInventory() {
        Call<List<InventoryModel>> inventories = sbAPI.getInventory();
        inventories.enqueue(new Callback<List<InventoryModel>>() {
            @Override
            public void onResponse(Call<List<InventoryModel>> call, Response<List<InventoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inventory = response.body();
                    // Use the inventory data as needed
                } else {
                    Log.e("Edit Product", "Inventory response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<InventoryModel>> call, Throwable t) {
                Log.e("Edit Product", "Failed to fetch inventory", t);
            }
        });
    }
}
