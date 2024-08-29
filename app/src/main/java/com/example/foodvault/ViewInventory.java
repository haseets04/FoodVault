package com.example.foodvault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ViewInventory extends AppCompatActivity {

    private static final String TAG = "ViewInventory";
    private sbAPI_ViewInventory sbAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        // Initialize Retrofit and API interface
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        // Fetch and display data
        fetchAndDisplayData(api);
    }

    private void fetchAndDisplayData(sbAPI_ViewInventory sbAPI) {
        Call<List<LocationModel>> locations= sbAPI.getLocations();
        Call<List<InventoryModel>> inventories=sbAPI.getInventory();
        Call<List<ProductModel>> products=sbAPI.getProducts();
       locations.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationModel> locations = response.body();
                    TableLayout tableLayout = findViewById(R.id.tblInventory);
                   inventories.enqueue(new Callback<List<InventoryModel>>() {
                        @Override
                        public void onResponse(Call<List<InventoryModel>> call, Response<List<InventoryModel>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<InventoryModel> inventory = response.body();

                                products.enqueue(new Callback<List<ProductModel>>() {
                                    @Override
                                    public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            List<ProductModel> products = response.body();

                                            for (ProductModel product : products) {
                                                for (InventoryModel inv : inventory) {
                                                    for (LocationModel location : locations) {
                                                        if (product.getProductId().equals(inv.getProductId()) && product.getLocation_id().equals(location.getLocation_id())) {
                                                            addProductRecord(tableLayout, product, location, inv);
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "Products response unsuccessful or body is null");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                                        Toast.makeText(ViewInventory.this, "Failed to display products", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed to fetch products", t);
                                    }
                                });
                            } else {
                                Log.e(TAG, "Inventory response unsuccessful or body is null");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<InventoryModel>> call, Throwable t) {
                            Toast.makeText(ViewInventory.this, "Failed to display inventory", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to fetch inventory", t);
                        }
                    });
                }  else {
                    Log.e(TAG, "Locations response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
               // Toast.makeText(ViewInventory.this, "Failed to display locations", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch locations", t);
            }
        });
    }

    private void addProductRecord(TableLayout tableLayout, ProductModel product,LocationModel location, InventoryModel inv) {
        LayoutInflater inflater = LayoutInflater.from(this);
        int count=tableLayout.getChildCount();
        String rowTag = "row" + (count);
        TableRow rowView = (TableRow) inflater.inflate(R.layout.standard_row, tableLayout, false);
        rowView.setTag(rowTag);
        TextView tvproductname = (TextView) rowView.getChildAt(0);
        TextView quantity = (TextView) rowView.getChildAt(1);
        TextView expirationdate = (TextView) rowView.getChildAt(2);
        TextView locationname = (TextView) rowView.getChildAt(3);
        TextView category = (TextView) rowView.getChildAt(4);
        CheckBox expired = (CheckBox) rowView.getChildAt(5);
        tvproductname.setText(product.getProductName());
        int test= inv.getQuantity();
        quantity.setText(""+test);
        expirationdate.setText(product.getProductExpirationDate().toString());
        locationname.setText(location.getLocation_name());
        category.setText(product.getProductCategory());
        Date today=new Date();//getting todays date
        expired.setChecked(product.getProductExpirationDate() != null && product.getProductExpirationDate().before(today));


        // Add additional views and set data as needed

        tableLayout.addView(rowView);
    }
}
