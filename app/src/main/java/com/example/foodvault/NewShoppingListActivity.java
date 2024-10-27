package com.example.foodvault;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewShoppingListActivity extends AppCompatActivity {
    private LinearLayout shoppingListContainer;
    private AppState appState;
<<<<<<< HEAD
    private int currentShopListNameID; // for cancel functionality
    ShopListModel newShoppingList;
    String shopListName;

    EditText shopListNameInput;
    List<ShoppingListProductsModel> shoppingListProductsModels;
    List<ProductModel> products;
    private Integer userId;
    sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
=======
    private int currentShopListNameID; //for cancel functionality
    private ShopListModel newShoppingList;
    private String shopListName;
    private EditText shopListNameInput;
    private Integer userId;
    private Integer currentShopListID;
    private SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);

        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null || userId == 0) {
            userId = getIntent().getIntExtra("USER_ID", 0);
            UserSession.getInstance().setUserSessionId(userId);
        }
        appState = AppState.getInstance();
        currentShopListNameID = appState.getShopListNameID(); // store the last saved value

        shopListNameInput = findViewById(R.id.shopListName);
        shopListNameInput.setHint("Shopping List " + currentShopListNameID);

        shoppingListContainer = findViewById(R.id.shopping_list_container);
<<<<<<< HEAD

=======
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
        if (shoppingListContainer == null) {
            Toast.makeText(this, "Failed to initialize shopping list container", Toast.LENGTH_LONG).show();
        }

        createShoppingListRecord();

        Spinner spnGroupProducts = findViewById(R.id.spinner_group_products_list);
        String[] groupingOptions = {"", "Product Category", "Grocery Store", "Both"};

        // Create an ArrayAdapter for the Spinner, with custom spinner item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupingOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnGroupProducts.setAdapter(adapter);

        // Set an item selected listener for the Spinner
        spnGroupProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle what happens when an item is selected
                String selectedGroupingOption = (String) parent.getItemAtPosition(position);

                // Open the GroceryStoreActivity when "Grocery Store" is selected
                if ("Grocery Store".equals(selectedGroupingOption)) {
                    Intent intent = new Intent(NewShoppingListActivity.this, GroceryStoreActivity.class);
                    intent.putExtra("shoppingListName", shopListNameInput.getText().toString());
                    startActivity(intent);
                } else if("Product Category".equals(selectedGroupingOption)) {


                } else if("Both".equals(selectedGroupingOption)) {


                } else if (!selectedGroupingOption.isEmpty()) {
                    // Show a toast for other selected items
                    Toast.makeText(NewShoppingListActivity.this, "Selected: " + selectedGroupingOption, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when no item is selected
            }
        });

        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGroupProducts.setAdapter(adapter);
<<<<<<< HEAD
        spnGroupProducts.setPrompt(getString(R.string.spinner_prompt));

        FloatingActionButton add = findViewById(R.id.fltbtn_add_item);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewShoppingListActivity.this, AddItemToSLActivity.class);
                intent.putExtra("SHOPPING_LIST_ID", getIntent().getIntExtra("SHOPPING_LIST_ID", 0));
                intent.putExtra("USER_ID", UserSession.getInstance().getUserSessionId()); // Add this line
                startActivity(intent);
            }
        });
        loadpreviousitems(getIntent().getIntExtra("SHOPPING_LIST_ID", -1));
    }

    public void onAddItemClicked(View view) {
=======
        spnGroupProducts.setPrompt(getString(R.string.spinner_prompt));*/
    }

    public void onAddItemClicked(View view) { //done in separate Use Case
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
        LinearLayout newItemRow = new LinearLayout(this);
        newItemRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newItemRow.setOrientation(LinearLayout.HORIZONTAL);
        newItemRow.setPadding(0, 8, 0, 8);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView qtyTextView = new TextView(this);
        qtyTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        qtyTextView.setGravity(Gravity.CENTER);
        qtyTextView.setText("3"); // Set default quantity or obtain from input

        TextView nameTextView = new TextView(this);
        nameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2.0f
        ));
        nameTextView.setText("New Item"); // Set default name or obtain from input

<<<<<<< HEAD
=======
        // Add the elements to the new row
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
        newItemRow.addView(checkBox);
        newItemRow.addView(qtyTextView);
        newItemRow.addView(nameTextView);

        shoppingListContainer.addView(newItemRow);
    }

<<<<<<< HEAD
    public void loadpreviousitems(int shoplistid) {
        Call<List<ShoppingListProductsModel>> getshoppinglistproducts = sbAPI.getshoppinglistproducts();
        getshoppinglistproducts.enqueue(new Callback<List<ShoppingListProductsModel>>() {
            @Override
            public void onResponse(Call<List<ShoppingListProductsModel>> call, Response<List<ShoppingListProductsModel>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    try {
                        Log.e("Custom", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                shoppingListProductsModels = response.body();
                loadproducts(shoplistid);
            }

            @Override
            public void onFailure(Call<List<ShoppingListProductsModel>> call, Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Failure to load shopping list", Toast.LENGTH_SHORT).show();
                Log.e("Custom", "There was a failure loading shopping list products");
            }
        });
    }

    public void loadproducts(int shoplistid) {
        Call<List<ProductModel>> getproducts = sbAPI.getProducts();

        getproducts.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("Custom", "Response unsuccessful or product body is null");
                    return;
                }
                products = response.body();
                for (ProductModel product : products) {
                    for (ShoppingListProductsModel slProduct : shoppingListProductsModels) {
                        if (slProduct.shoplist_id == shoplistid && slProduct.getProduct_id() == product.getProductId()) {
                            LinearLayout linearLayout = inflateLinearLayout(NewShoppingListActivity.this);
                            CheckBox checkBox = linearLayout.findViewById(R.id.cbxTicked);
                            TextView textView1 = linearLayout.findViewById(R.id.tvQuantity);
                            TextView tvslproductqty = linearLayout.findViewById(R.id.tvName);
                            checkBox.setChecked(slProduct.ticked_or_not);
                            textView1.setText("" + slProduct.getShoplistprocuts_quantity());
                            tvslproductqty.setText(product.getProductName());

                            LinearLayout parentlayout = findViewById(R.id.rowholder);
                            parentlayout.addView(linearLayout);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                Log.e("Custom", "Failed to fetch products: " + t.getMessage());
                Toast.makeText(NewShoppingListActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LinearLayout inflateLinearLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (LinearLayout) inflater.inflate(R.layout.shoppingitemrow, null);
    }

    public void onSaveNewShopList(View view) {
=======
    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(NewShoppingListActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    public void createShoppingListRecord(){
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
        shopListName = shopListNameInput.getText().toString();
        int id= getIntent().getIntExtra("SHOPPING_LIST_ID", 0);
        newShoppingList = new ShopListModel();
<<<<<<< HEAD
        if (shopListName.isEmpty()) {
=======
        newShoppingList.setUserIdForShopList(getCurrentUserIDFromSession());

        if(shopListName.isEmpty()){
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
            newShoppingList.setShoplistName("Shopping List " + currentShopListNameID);
            appState.setShopListNameID(currentShopListNameID + 1);
        } else {
            newShoppingList.setShoplistId(id);
            newShoppingList.setShoplistName(shopListName);
            newShoppingList.setUserIdForShopList(userId);
        }

<<<<<<< HEAD
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        Call<Void> insertListCall = api.updateShoplist("eq." + id, newShoppingList);
        insertListCall.enqueue(new Callback<Void>() {
=======
        Call<List<ShopListModel>> insertListCall = api.insertShoppingList(newShoppingList);
        insertListCall.enqueue(new Callback<List<ShopListModel>>() {
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
            @Override
            public void onResponse(@NonNull Call<List<ShopListModel>> call, @NonNull Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ShopListModel insertedShopList = response.body().get(0);
                    currentShopListID = insertedShopList.getShoplistId();

                    Log.i("ShopList Id", String.valueOf(currentShopListID));
                    Toast.makeText(NewShoppingListActivity.this, "Shopping List saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
<<<<<<< HEAD
                    finish();
                    startActivity(new Intent(NewShoppingListActivity.this,ShoppingListActivity.class));
=======
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("Supabase Error", "Saving of Shopping List Failed: " + response.message() + " - " + errorBody);
                    } catch (Exception e) {
                        Log.e("Supabase Error", "Error reading response body", e);
                    }
                    Toast.makeText(NewShoppingListActivity.this, "Saving of Shopping List Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

<<<<<<< HEAD
=======
            @Override
            public void onFailure(@NonNull Call<List<ShopListModel>> call, @NonNull Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onSaveNewShopList(View view) {
        shopListName = shopListNameInput.getText().toString();

        ShopListModel updatedShopList = new ShopListModel();
        if(shopListName.isEmpty() || shopListName == null){
            updatedShopList.setShoplistName("Shopping List " + currentShopListNameID);
            appState.setShopListNameID(currentShopListNameID + 1);
        } else{
            updatedShopList.setShoplistName(shopListName);
        }

        Call<Void> updateCall = api.updateShopListDetails("eq." + currentShopListID, updatedShopList);
        updateCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewShoppingListActivity.this, "Current Settings Saved: " + shopListName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewShoppingListActivity.this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                }
            }
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Failed to save Shopping List", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failure to save shopping list: " + t.getMessage());
            }
        });
<<<<<<< HEAD
    }
=======
        finish(); //go back to ShoppingListActivity
        //confirm saving of changes
    }

    public void onCancelNewShopList(View view) { //don't add to DB
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the new entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete shopping list entry
                deleteCurrentShoppingList();
                Toast.makeText(NewShoppingListActivity.this, "New Shopping List entry cancelled", Toast.LENGTH_SHORT).show();
                finish();            }
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

    public void deleteCurrentShoppingList(){
        Call<Void> deleteCall = api.deleteShoppingList("eq."+ currentShopListID);

        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(NewShoppingListActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(NewShoppingListActivity.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
>>>>>>> 6d00511cc4c4dab33eef064a1ce8e179ab75b474
}
