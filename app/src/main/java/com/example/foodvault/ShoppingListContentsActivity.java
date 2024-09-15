package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListContentsActivity extends AppCompatActivity {
    Integer ShopListIDOfBtn;
    sbAPI_ViewInventory sbAPI=SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
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
            public void onResponse(@NonNull Call<List<ProductsOnShopListModel>> call, @NonNull Response<List<ProductsOnShopListModel>> response) {
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
            public void onFailure(@NonNull Call<List<ProductsOnShopListModel>> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
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
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Toast.makeText(ShoppingListContentsActivity.this, "Error loading product details", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch product details", t);
            }
        });
    }

    private void addProductRow(TableLayout tableLayout, ProductsOnShopListModel productOnShopList, ProductModel product){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow rowView = (TableRow) inflater.inflate(R.layout.standard_row2, tableLayout, false);

        CheckBox checkBox = rowView.findViewById(R.id.checkbox_ticked);
       checkBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // Toast.makeText(ShoppingListContentsActivity.this, "Product Successfully edited", Toast.LENGTH_SHORT).show();
               String id="eq."+productOnShopList.getProducts_on_list_id();
               ShoppingListProductsModel product=new ShoppingListProductsModel(productOnShopList.getGrocery_store(),checkBox.isChecked(),productOnShopList.getShoplist_id(), productOnShopList.getProduct_id(),  0);
               product.setProducts_on_list_id(productOnShopList.getProducts_on_list_id());
               Call<Void> insertproduct=sbAPI.updateSLproduct(id,product);
               insertproduct.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(Call<Void> call, Response<Void> response) {
                       if(!response.isSuccessful()) {
                           try {
                               Log.e("Custom",response.errorBody().string());
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
                       }
                       Toast.makeText(ShoppingListContentsActivity.this, "Product Successfully edited", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onFailure(Call<Void> call, Throwable t) {

                   }
               });
           }
       });
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