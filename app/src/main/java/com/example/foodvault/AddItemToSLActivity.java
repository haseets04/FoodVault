package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemToSLActivity extends AppCompatActivity { //:)
    // Retrofit API client instance
    sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

    // Data storage
    private List<ProductModel> listproducts2 = new ArrayList<>();
    private List<String> productnames = new ArrayList<>();
    private Integer addedId = 0;
    private Integer userId = UserSession.getInstance().getUserSessionId(); // Get user session ID

    // Activity-related data
    String act="";
    int slid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_slactivity);

        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null || userId == 0) {
            userId = getIntent().getIntExtra("USER_ID", 0);
            UserSession.getInstance().setUserSessionId(userId);
        }

        // Initialize UI components
        NumberPicker quanty = findViewById(R.id.number_picker_quantity);
        quanty.setMaxValue(100);
        quanty.setMinValue(0);

        TextView tvquant = findViewById(R.id.tvquantity);
        AutoCompleteTextView name = findViewById(R.id.autoname);
        CheckBox cbxbought = findViewById(R.id.cbxbought);
        TextView store = findViewById(R.id.tvgrocerystore);
        Button add = findViewById(R.id.btn_add);

        act = getIntent().getStringExtra("Activity");

        slid = getIntent().getIntExtra("SHOPPING_LIST_ID", 0);
        if (slid == 0) {
            Toast.makeText(this, "Invalid shopping list ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, "ShopListID: " + slid, Toast.LENGTH_SHORT).show();

        // Fetch products from the server
        fetchProducts(name);

        // Quantity picker listener
        quanty.setOnValueChangedListener((picker, oldVal, newVal) -> tvquant.setText("Quantity To Buy: " + newVal));

        // Add item button click listener
        add.setOnClickListener(v -> {
            String productName = name.getText().toString().trim();
            if (productName.isEmpty()) {
                Toast.makeText(this, "Please enter a product name", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean itemExists = false;

            // Check if product exists in the list
            for (ProductModel product : listproducts2) {
                if (productName.equalsIgnoreCase(product.getProductName()) &&
                        userId.equals(product.getUserIdForProduct()) && !itemExists) {
                    itemExists = true;
                    addedId = product.getProductId();

                    // Prompt user if the product already exists in inventory
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Existing Item")
                            .setMessage("This item already exists in your inventory. Are you sure you want to add it?")
                            .setNegativeButton("Yes", (dialog, which) -> {
                                addItemToShoppingList(store, cbxbought,quanty,name);
                                navigateToNextActivity();
                            })
                            .setPositiveButton("No", (dialog, which) ->
                                    Toast.makeText(AddItemToSLActivity.this, "Product not added to the shopping list.", Toast.LENGTH_SHORT).show())
                            .setCancelable(false)
                            .show();
                    break;
                }
            }

            if (!itemExists) {
                if (cbxbought.isChecked()) {
                    promptForPurchasedQuantity(store, cbxbought, quanty, name);
                } else {
                    insertNewProduct(name, store, cbxbought, quanty);
                }
            }
        });
    }

    private void fetchProducts(AutoCompleteTextView name) {
        Call<List<ProductModel>> products = sbAPI.getProducts();

        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listproducts2 = response.body();
                    for (ProductModel product : listproducts2) {
                        if (userId.equals(product.getUserIdForProduct())) {
                            productnames.add(product.getProductName());
                        }
                    }
                    ArrayAdapter<String> shoppinglistAdapter = new ArrayAdapter<>(AddItemToSLActivity.this,
                            android.R.layout.simple_dropdown_item_1line, productnames);
                    name.setAdapter(shoppinglistAdapter);
                    name.setThreshold(0);

                } else {
                    Log.e("Fetch Products", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("Fetch Products", "Failed to fetch products", t);
            }
        });
    }

    private void addItemToShoppingList(TextView store, CheckBox cbxbought, NumberPicker quanty, AutoCompleteTextView name) {
        Call<ProductsOnShopListModel> insertItem = sbAPI.insertShoppingListItem(
                new ProductsOnShopListModel(store.getText().toString(), false, slid, addedId, quanty.getValue()));

        insertItem.enqueue(new Callback<ProductsOnShopListModel>() {
            @Override
            public void onResponse(Call<ProductsOnShopListModel> call, Response<ProductsOnShopListModel> response) {
                if (response.isSuccessful()) {
                    // Successfully added to the shopping list
                    Log.d("Add Item", "Item successfully added to the shopping list.");
                    // Ensure to pass 'name' here
                } else {
                    Log.e("Add Item", "Failed to add item to the shopping list: " + response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<ProductsOnShopListModel> call, Throwable t) {
                Log.e("Add Item", "Error adding item to the shopping list.", t);
            }
        });
    }


    private void promptForPurchasedQuantity(TextView store, CheckBox cbxbought, NumberPicker quanty, AutoCompleteTextView name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantity Purchased");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString();
            try {
                int quantity = Integer.parseInt(userInput);
                insertNewProductWithQuantity(name, store, cbxbought, quanty, quantity);
            } catch (NumberFormatException e) {
                Toast.makeText(AddItemToSLActivity.this, "Invalid quantity. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
            //navigateToNextActivity(cbxbought,quanty,name);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void insertNewProduct(AutoCompleteTextView name, TextView store, CheckBox cbxbought, NumberPicker quanty) {
        ProductModel newProduct = new ProductModel();
        newProduct.setProductQuantity(0);
        newProduct.setProductName(name.getText().toString().trim());
        newProduct.setLocationId(2); // Change this if location varies
        newProduct.setUserIdForProduct(userId);
        newProduct.setProductCategory("N/A");
        newProduct.setProductExpirationDate(new Date());

        Call<ProductModel> insertProduct = sbAPI.insertProduct(newProduct);
        insertProduct.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.isSuccessful()) {
                    fetchLastProduct(store, cbxbought,quanty,name);
                } else {
                    Log.e("Insert Product", "Failed to insert product");
                }
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Log.e("Insert Product", "Error inserting product", t);
            }
        });
    }

    private void insertNewProductWithQuantity(AutoCompleteTextView name, TextView store, CheckBox cbxbought, NumberPicker quanty, int quantity) {
        ProductModel newProduct = new ProductModel();
        newProduct.setProductQuantity(quantity);
        newProduct.setProductName(name.getText().toString().trim());
        newProduct.setLocationId(2); // Change this if location varies
        newProduct.setUserIdForProduct(userId);
        newProduct.setProductCategory("N/A");
        newProduct.setProductExpirationDate(new Date());

        Call<ProductModel> insertProduct = sbAPI.insertProduct(newProduct);
        insertProduct.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.isSuccessful()) {
                    fetchLastProduct(store, cbxbought, quanty,name);
                } else {
                    Log.e("Insert Product", "Failed to insert product with quantity");
                }
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Log.e("Insert Product", "Error inserting product with quantity", t);
            }
        });
    }

    private void fetchLastProduct(TextView store, CheckBox cbxbought, NumberPicker quanty, AutoCompleteTextView name) {

        Call<List<ProductModel>> products = sbAPI.getProducts();
        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> temp = new ArrayList<>(response.body());

                    // Find the last added product using a comparator
                    temp.sort(new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel p1, ProductModel p2) {
                            // Assuming ProductModel has a method getAddedDate() returning a Date object
                            return p2.getProductId().compareTo(p1.getProductId());
                        }
                    });

                    ProductModel lastAddedProduct = temp.get(0);
                    addedId = lastAddedProduct.getProductId();
                    addItemToShoppingList(store, cbxbought, quanty, name);
                    navigateToNextActivity();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("Fetch Last Product", "Failed to fetch last product.", t);
            }
        });
    }

    private void navigateToNextActivity() {
        Intent intent;

        // Check which activity to navigate to after saving
        if ("ShoppingListContentsActivity".equals(act)) {
            // Go back to the shopping list contents screen if that's where we came from
            intent = new Intent(AddItemToSLActivity.this, ShoppingListContentsActivity.class);
        } else {
            // If navigating from a different path, go back to NewShoppingListActivity
            intent = new Intent(AddItemToSLActivity.this, NewShoppingListActivity.class);
        }

        // Pass user and shopping list details
        intent.putExtra("USER_ID", UserSession.getInstance().getUserSessionId());
        intent.putExtra("SHOPPING_LIST_ID", slid);

        // Clear the activity stack to avoid returning to AddItemToSLActivity on back press
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish current activity to prevent it from being in the back stack
        finish();
    }

    public void onCancelProductClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the new entry?");
        builder2.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //navigateToNextActivity();
                Intent intent;
                if (act.equals("ShoppingListContentsActivity")) {
                    intent = new Intent(AddItemToSLActivity.this, ShoppingListContentsActivity.class);
                } else {
                    intent = new Intent(AddItemToSLActivity.this, NewShoppingListActivity.class);
                }
                intent.putExtra("USER_ID", UserSession.getInstance().getUserSessionId());
                intent.putExtra("SHOPPING_LIST_ID", slid);
                startActivity(intent);
                finish();
            }
        });
        builder2.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog2 = builder2.create();
        dialog2.show();
    }

}
