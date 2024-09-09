package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListContentsActivity extends AppCompatActivity {
    Integer ShopListIDOfBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_contents);

        TextView txtListName = findViewById(R.id.txt_list_name);
        String listName = getIntent().getStringExtra("SHOPPING_LIST_NAME");
        if (listName != null) {
            txtListName.setText(listName); //pass button text
        }

        ShopListIDOfBtn = getIntent().getIntExtra("SHOPPING_LIST_ID", -1);
        if (ShopListIDOfBtn == -1) {
            Toast.makeText(this, "Invalid shopping list ID", Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.i("ID", ShopListIDOfBtn.toString());

        fetchAndDisplayProductsOnShopListFromDB();
    }

    private void fetchAndDisplayProductsOnShopListFromDB() {
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<ProductsOnShopListModel>> productsOnListCall = api.getProductOnListByShoplistID();
        productsOnListCall.enqueue(new Callback<List<ProductsOnShopListModel>>() {
            @Override
            public void onResponse(Call<List<ProductsOnShopListModel>> call, Response<List<ProductsOnShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductsOnShopListModel> productsOnShopList = response.body();
                    if (!productsOnShopList.isEmpty()) {
                        fetchAndDisplayProductDetails(productsOnShopList);
                    } else {
                        Toast.makeText(ShoppingListContentsActivity.this, "No products found in the list", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShoppingListContentsActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductsOnShopListModel>> call, Throwable t) {
                Toast.makeText(ShoppingListContentsActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch products on the list", t);
            }
        });
    }

    private void fetchAndDisplayProductDetails(List<ProductsOnShopListModel> productsOnShopList) {
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<ProductModel>> productsCall = api.getProducts();
        productsCall.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    TableLayout tableLayout = findViewById(R.id.tblShopListContents);
                    tableLayout.removeAllViews(); //clear any existing rows

                    //add header row first
                    LayoutInflater inflater = LayoutInflater.from(ShoppingListContentsActivity.this);
                    TableRow headerRow = (TableRow) inflater.inflate(R.layout.header_row, tableLayout, false);
                    TextView headerQty = headerRow.findViewById(R.id.txt_quantity);
                    TextView headerProductName = headerRow.findViewById(R.id.txt_product_name);
                    headerQty.setText("Qty");
                    headerProductName.setText("Product");
                    tableLayout.addView(headerRow);

                    //add rows for each product
                    for (ProductsOnShopListModel productOnShopList : productsOnShopList) {
                        for (ProductModel product : products) {
                            if (productOnShopList.getShoplist_id().equals(ShopListIDOfBtn) &&
                                    product.getProductId().equals(productOnShopList.getProduct_id()))
                            {
                                addProductRow(tableLayout, productOnShopList, product);
                            }
                        }
                    }
                } else {
                    Toast.makeText(ShoppingListContentsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(ShoppingListContentsActivity.this, "Error loading product details", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch product details", t);
            }
        });
    }

    private void addProductRow(TableLayout tableLayout, ProductsOnShopListModel productOnShopList, ProductModel product){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow rowView = (TableRow) inflater.inflate(R.layout.standard_row2, tableLayout, false);

        CheckBox checkBox = rowView.findViewById(R.id.checkbox_ticked);
        TextView txtQuantity = rowView.findViewById(R.id.txt_quantity);
        TextView txtProductName = rowView.findViewById(R.id.txt_product_name);

        // Set product details in the row
        checkBox.setChecked(productOnShopList.isTicked_or_not()); //setChecked if true
        txtQuantity.setText(String.valueOf(product.getProductQuantity()));
        txtProductName.setText(product.getProductName());

        /*
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the ticked_or_not status in the database
        });
        */

        tableLayout.addView(rowView);
    }

    public void onAddAnotherItemClicked(View view) {
    }

    public void onShareListClicked(View view) {
    }

    public void onDeleteItemClicked(View view) {
    }
}