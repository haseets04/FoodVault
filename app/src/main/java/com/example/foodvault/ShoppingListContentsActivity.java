package com.example.foodvault;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListContentsActivity extends AppCompatActivity {
    Integer ShopListIDOfBtn;
    sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
    List<ProductModel> groupproducts = new ArrayList<>();
    List<ProductsOnShopListModel> groupslproducts = new ArrayList<>();
    HashMap<String,Integer> findrows = new HashMap<>();
    List<GroupModel> listGroups=new ArrayList<>();
    List<Integer> id = new ArrayList<>();

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

        Toast.makeText(this, "ShopListID: " + ShopListIDOfBtn, Toast.LENGTH_SHORT).show();
        //Log.i("ID", ShopListIDOfBtn.toString());

        fetchAndDisplayProductsOnShopListFromDB();

        Button btnGroup = findViewById(R.id.btn_group);
        btnGroup.setOnClickListener(v -> showGroupMenu(v));

        FloatingActionButton share = findViewById(R.id.fltbtn_share_list);
        share.setOnClickListener(v-> ShareList(SupabaseClient.getClient().create(sbAPI_ViewInventory.class)));
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
            }
            else if (id == R.id.group_by_shop) {
                groupItemsByShop();
                return true;
            }
            else if (id == R.id.sort_ascending) {
                sortItemsAscending();
                return true;
            }
            else if (id == R.id.sort_descending) {
                sortItemsDescending();
                return true;
            }
            else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void groupItemsByCategory() {
        // Fetch products and group them by category
        List<ProductModel> products = groupproducts;
        List<ProductsOnShopListModel> SLproducts = groupslproducts;// Fetch your products
        String correct="";
        Map<String, List<ProductsOnShopListModel>> groupedProducts = new HashMap<>();
        for (ProductModel product : products) {
            String category = product.getProductCategory(); // Assuming you have a method to get the category
            groupedProducts.putIfAbsent(category, new ArrayList<>());
            for(ProductsOnShopListModel SLproduct: SLproducts)
            {

                if(SLproduct.getProduct_id().equals(product.getProductId()) && SLproduct.getShoplist_id().equals(ShopListIDOfBtn)){
                    groupedProducts.get(category).add(SLproduct);
                }
            }

        }
        displayGroupedProducts(groupedProducts,products);
    }

    private void groupItemsByShop() {
        // Fetch products and group them by shop/store
        List<ProductsOnShopListModel> productsOnShopList = groupslproducts; // Fetch your products
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

    private void sortItemsAscending() {
        List<ProductModel> products = groupproducts;
        List<ProductsOnShopListModel> slProducts = new ArrayList<>(groupslproducts);

        // Sort slProducts based on corresponding product names
        slProducts.sort((sl1, sl2) -> {
            String name1 = "";
            String name2 = "";

            // Find corresponding product names
            for (ProductModel product : products) {
                if (product.getProductId().equals(sl1.getProduct_id())) {
                    name1 = product.getProductName();
                }
                if (product.getProductId().equals(sl2.getProduct_id())) {
                    name2 = product.getProductName();
                }
            }

            return name1.compareToIgnoreCase(name2);
        });

        // Display the sorted products
        TableLayout tableLayout = findViewById(R.id.tblShopListContents);
        tableLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow headerRow = (TableRow) inflater.inflate(R.layout.header_row, tableLayout, false);
        TextView headerQty = headerRow.findViewById(R.id.txt_quantity);
        TextView headerProductName = headerRow.findViewById(R.id.txt_product_name);
        headerQty.setText("Qty");
        headerProductName.setText("Product");
        tableLayout.addView(headerRow);

        // Add sorted product rows
        for (ProductsOnShopListModel slProduct : slProducts) {
            if (slProduct.getShoplist_id().equals(ShopListIDOfBtn)) {
                for (ProductModel product : products) {
                    if (product.getProductId().equals(slProduct.getProduct_id())) {
                        addProductRow(tableLayout, slProduct, product);
                        break;
                    }
                }
            }
        }
    }

    private void sortItemsDescending() {
        List<ProductModel> products = groupproducts;
        List<ProductsOnShopListModel> slProducts = new ArrayList<>(groupslproducts);

        // Sort slProducts based on corresponding product names
        slProducts.sort((sl1, sl2) -> {
            String name1 = "";
            String name2 = "";

            // Find corresponding product names
            for (ProductModel product : products) {
                if (product.getProductId().equals(sl1.getProduct_id())) {
                    name1 = product.getProductName();
                }
                if (product.getProductId().equals(sl2.getProduct_id())) {
                    name2 = product.getProductName();
                }
            }

            return name2.compareToIgnoreCase(name1);  // Reversed comparison for descending order
        });

        // Display the sorted products
        TableLayout tableLayout = findViewById(R.id.tblShopListContents);
        tableLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow headerRow = (TableRow) inflater.inflate(R.layout.header_row, tableLayout, false);
        TextView headerQty = headerRow.findViewById(R.id.txt_quantity);
        TextView headerProductName = headerRow.findViewById(R.id.txt_product_name);
        headerQty.setText("Qty");
        headerProductName.setText("Product");
        tableLayout.addView(headerRow);

        // Add sorted product rows
        for (ProductsOnShopListModel slProduct : slProducts) {
            if (slProduct.getShoplist_id().equals(ShopListIDOfBtn)) {
                for (ProductModel product : products) {
                    if (product.getProductId().equals(slProduct.getProduct_id())) {
                        addProductRow(tableLayout, slProduct, product);
                        break;
                    }
                }
            }
        }
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

    private void ShareList(sbAPI_ViewInventory sbAPI) {
        Call<List<GroupModel>> groups = sbAPI.getGroups();

        groups.enqueue(new Callback<List<GroupModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupModel>> call, @NonNull Response<List<GroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listGroups = response.body();
                    Call<List<UsersInGroupModel>> usersingroup=sbAPI.getusersingroups();
                    usersingroup.enqueue(new Callback<List<UsersInGroupModel>>() {
                        @Override
                        public void onResponse(Call<List<UsersInGroupModel>> call, Response<List<UsersInGroupModel>> response) {
                            if(!response.isSuccessful())
                                return;
                            Iterator<GroupModel> iterator = listGroups.iterator();
                            while (iterator.hasNext()) {
                                GroupModel group = iterator.next();
                                boolean isAdmin = false;

                                for (UsersInGroupModel uig : response.body()) {
                                    if (uig.getUser_id().equals(UserSession.getInstance().getUserSessionId()) && uig.isIs_admin() &&
                                            group.getGroupId().equals(uig.getGroup_id())) {
                                        isAdmin = true; // The user is an admin for this group
                                        break; // No need to check other uigs for this group
                                    }
                                }

                                if (!isAdmin) {
                                    iterator.remove(); // Remove the group if the user is not admin
                                }
                            }


                            Log.d(TAG, "Groups fetched successfully: " + listGroups.toString());
                            showGroupsDialog(listGroups);
                        }

                        @Override
                        public void onFailure(Call<List<UsersInGroupModel>> call, Throwable t) {

                        }
                    });

                } else {
                    Log.e(TAG, "Groups response unsuccessful or body is null");

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupModel>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch groups", t);

            }
        });
    }

    private void showGroupsDialog(List<GroupModel> listGroups) {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);

        ListView listView = dialogView.findViewById(R.id.list_view);

        // Use HashSet to ensure unique group names
        HashSet<String> uniqueGroups = new HashSet<>();
        for (GroupModel group : listGroups) {
            uniqueGroups.add(group.getGroupName());
        }

        // Convert to ArrayList for ListView adapter
        ArrayList<String> groupNames = new ArrayList<>(uniqueGroups);

        // Set up the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, groupNames);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Display the dialog
        new AlertDialog.Builder(this)
                .setTitle("Select Group to Share List")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the selected group
                    int selectedPosition = listView.getCheckedItemPosition();
                    if (selectedPosition != ListView.INVALID_POSITION) {
                        String selectedGroup = groupNames.get(selectedPosition);

                        // Find the matching GroupModel based on group name
                        GroupModel selectedGroupModel = null;
                        for (GroupModel group : listGroups) {
                            if (group.getGroupName().equals(selectedGroup)) {
                                selectedGroupModel = group;
                                break;
                            }
                        }

                        // Proceed with the selected group
                        if (selectedGroupModel != null) {
                            int selectedGroupId = selectedGroupModel.getGroupId();
                            shareListWithGroup(selectedGroupId);

                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to handle sharing the list with the selected group ID
    private void shareListWithGroup(int groupId) {
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        Call<List<ShopListModel>> getShoppingListsCall = api.getShoppingLists();
        getShoppingListsCall.enqueue(new Callback<List<ShopListModel>>() {
            @Override
            public void onResponse(Call<List<ShopListModel>> call, Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ShopListModel> shoppingLists = response.body();
                    for (ShopListModel list : shoppingLists) {
                        if (list.getShoplistId().equals(ShopListIDOfBtn)) {
                            list.setGroup_id(groupId);
                            Call<Void> updateShoppingListCall = api.updateShoplist("eq." + ShopListIDOfBtn, list);
                            updateShoppingListCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ShoppingListContentsActivity.this, "Shopping List shared", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                            Log.e("Supabase Error", "Saving of Shopping List Failed: " + response.message() + " - " + errorBody);
                                            Toast.makeText(ShoppingListContentsActivity.this, "Sharing of Shopping List Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e("Supabase Error", "Error reading response body", e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                    Toast.makeText(ShoppingListContentsActivity.this, "Failed to save Shopping List", Toast.LENGTH_SHORT).show();
                                    Log.e("Supabase Error", "Failure to save shopping list: " + t.getMessage());
                                }
                            });
                            break; // Exit the loop once the list is found and updated
                        }
                    }
                } else {
                    Log.e(TAG, "Shopping list response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<ShopListModel>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch shopping lists", t);
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
                ProductsOnShopListModel product=new ProductsOnShopListModel(productOnShopList.getGrocery_store(),checkBox.isChecked(),productOnShopList.getShoplist_id(), productOnShopList.getProduct_id(),  productOnShopList.getShoplistproducts_quantity());
                product.setProducts_on_list_id(productOnShopList.getProducts_on_list_id());
                Call<Void> insertslproduct=sbAPI.updateSLproduct(id,product);
                ProductModel temp=new ProductModel();
                for (ProductModel prod:
                        groupproducts) {
                    if(productOnShopList.getProduct_id().equals(prod.getProductId()))
                        temp=prod;
                }
                if(product.isTicked_or_not())
                    temp.setProductQuantity(temp.getProductQuantity()+productOnShopList.getShoplistproducts_quantity());
                Call<Void> insertproduct=sbAPI.updateProduct("eq."+productOnShopList.getProduct_id(),temp);
                insertproduct.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
                insertslproduct.enqueue(new Callback<Void>() {
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
        txtQuantity.setText(String.valueOf(productOnShopList.getShoplistproducts_quantity()));
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

    /*public void onShareListClicked(View view) {
    }*/

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
    private void deleterecords(List<Integer> ids) {
        for (Integer id: ids) {
            int i=id;
          //  Toast.makeText(ShoppingListContentsActivity.this,i+"",Toast.LENGTH_SHORT);
            Call<Void> deleterecordscall=sbAPI.deleteSLproduct("eq."+i);

            deleterecordscall.enqueue(new Callback<Void>() {
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

    public void onReturnViewClicked(View view) {
        finish();
    }
}