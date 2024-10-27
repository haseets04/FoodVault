package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListContentsActivity extends AppCompatActivity {
    Integer ShopListIDOfBtn;
    sbAPI_ViewInventory sbAPI=SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
    List<ProductModel> groupproducts=new ArrayList<>();
    List<ProductsOnShopListModel>groupslproducts=new ArrayList<>();
    HashMap<String,Integer> findrows=new HashMap<>();
    List<Integer> id=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_contents);

        TextView txtListName = findViewById(R.id.txt_list_name);
        String listName = getIntent().getStringExtra("SHOPPING_LIST_NAME");
        if (listName != null) {
            txtListName.setText(listName); //pass button text
        }
        findViewById(R.id.btndeleterecords).setVisibility(View.GONE);
        ShopListIDOfBtn = getIntent().getIntExtra("SHOPPING_LIST_ID", -1);
        if (ShopListIDOfBtn == -1) {
            Toast.makeText(this, "Invalid shopping list ID", Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.i("ID", ShopListIDOfBtn.toString());

        fetchAndDisplayProductsOnShopListFromDB();
        Button btnGroup = findViewById(R.id.btngroup);
        btnGroup.setOnClickListener(v -> showGroupMenu(v));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the product list when the activity becomes visible
        fetchAndDisplayProductsOnShopListFromDB();
    }

    private void showGroupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.group_menu, popupMenu.getMenu()); // Create a menu resource

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.group_by_category) {
                groupItemsByCategory();
                return true;
            } else if (id == R.id.group_by_shop) {
                groupItemsByShop();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
    private void groupItemsByCategory() {
        // Fetch products and group them by category
        List<ProductModel> products = groupproducts;
        List<ProductsOnShopListModel> SLproducts =groupslproducts;// Fetch your products
        String correct="";
        Map<String, List<ProductsOnShopListModel>> groupedProducts = new HashMap<>();
        for (ProductModel product : products) {
            String category = product.getProductCategory(); // Assuming you have a method to get the category
            groupedProducts.putIfAbsent(category, new ArrayList<>());
            for(ProductsOnShopListModel SLproduct: SLproducts)
            {
                if(SLproduct.getProduct_id()== 112 && SLproduct.getShoplist_id()== 161 && product.getProductId().equals(112))
                     correct="dlfj";
                if(SLproduct.getProduct_id().equals(product.getProductId()) && SLproduct.getShoplist_id().equals(ShopListIDOfBtn)){
                    groupedProducts.get(category).add(SLproduct);
                }
            }

        }
        displayGroupedProducts(groupedProducts,products);
    }

    private void groupItemsByShop() {
        // Fetch products and group them by shop/store
        List<ProductsOnShopListModel> productsOnShopList =groupslproducts; // Fetch your products
        List<ProductModel> products = groupproducts;
        Map<String, List<ProductsOnShopListModel>> groupedProducts = new HashMap<>();
        for (ProductsOnShopListModel product : productsOnShopList) {
            if(product.getShoplist_id().equals(ShopListIDOfBtn)) {
                String shop = product.getGrocery_store(); // Assuming you have a method to get the shop
                groupedProducts.putIfAbsent(shop, new ArrayList<>());
                groupedProducts.get(shop).add(product);
            }
        }
        displayGroupedProducts(groupedProducts,products);
    }

    private void displayGroupedProducts(Map<String, List<ProductsOnShopListModel>> groupedProducts,List<ProductModel> groupprods) {
        TableLayout tableLayout = findViewById(R.id.tblShopListContents);
        tableLayout.removeAllViews(); // Clear existing rows

        for (Map.Entry<String, List<ProductsOnShopListModel>> entry : groupedProducts.entrySet()) {
            String groupHeader = entry.getKey();
            if(!entry.getValue().isEmpty()) {
                // Add header for the group
                TableRow headerRow = new TableRow(this);
                TextView headerTextView = new TextView(this);
                headerTextView.setText(groupHeader);
                headerTextView.setTextSize(18); // Set text size
                headerTextView.setTypeface(null, Typeface.BOLD); // Make it bold
                headerRow.addView(headerTextView);
                tableLayout.addView(headerRow);
                int userid = UserSession.getInstance().getUserSessionId();
                for (ProductsOnShopListModel product : entry.getValue()) {
                    // Display each product in a row
                    for (ProductModel prod : groupprods) {
                        if (product.getProduct_id().equals(prod.getProductId()) && product.getShoplist_id().equals(ShopListIDOfBtn)&&userid==prod.getUserIdForProduct().intValue())
                            addProductRow(tableLayout, product, prod);
                    }
                    // Adjust as necessary
                }
            }
        }
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
                    groupslproducts=productsOnShopList;
                    groupproducts=products;
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
        String rowTag = "row" + (findrows.size());
        rowView.setTag(rowTag);
        findrows.put(rowTag,productOnShopList.getProducts_on_list_id());

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
        Intent intent=new Intent(ShoppingListContentsActivity.this,AddItemToSLActivity.class);
        Intent getintent=getIntent();
        intent.putExtra("Activity","ShoppingListContentsActivity");
        intent.putExtra("SHOPPING_LIST_ID",getintent.getIntExtra("SHOPPING_LIST_ID",-1));
        startActivity(intent);
    }

    public void onShareListClicked(View view) {
    }

    public void onDeleteItemClicked(View view) {
        Toast.makeText(ShoppingListContentsActivity.this, "Click the products you want to do delete", Toast.LENGTH_SHORT).show();
        TableLayout tableLayout = findViewById(R.id.tblShopListContents);

        int count =tableLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            TableRow row=(TableRow)tableLayout.getChildAt(i);
            if(row instanceof TableRow)
            {
                row.setClickable(true);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row.setBackgroundColor(ContextCompat.getColor(ShoppingListContentsActivity.this, R.color.rowdelete));
                     //   Toast.makeText(ShoppingListContentsActivity.this,""+findrows.get(row.getTag()),Toast.LENGTH_SHORT).show();
                        id.add(findrows.get(row.getTag()));

                    }
                });

            }
        }
        Button deleterecords=findViewById(R.id.btndeleterecords);
        deleterecords.setEnabled(true);
        deleterecords.setVisibility(View.VISIBLE);
        FloatingActionButton fabdelete=findViewById(R.id.fltbtn_delete_item);
        fabdelete.setEnabled(false);
        deleterecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleterecords(id);
                deleterecords.setEnabled(false);
                deleterecords.setVisibility(View.GONE);
            }
        });

    }
    private void deleterecords(List<Integer> ids)
    {
        for (Integer id: ids) {
            int i=id;
          //  Toast.makeText(ShoppingListContentsActivity.this,i+"",Toast.LENGTH_SHORT);
            Call<Void> deleterecorscall=sbAPI.deleteSLproduct("eq."+i);
            deleterecorscall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        try {
                            Log.e("Custom",response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else
                    {
                        FloatingActionButton fabdelete=findViewById(R.id.fltbtn_delete_item);
                        fabdelete.setEnabled(true);
                        fabdelete.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }

        fetchAndDisplayProductsOnShopListFromDB();
    }
}