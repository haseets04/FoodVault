package com.example.foodvault;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewShoppingListActivity extends AppCompatActivity {
    private LinearLayout shoppingListContainer;
    private AppState appState;
    private int currentShopListNameID; // for cancel functionality
    ShopListModel newShoppingList;
    String shopListName;

    EditText shopListNameInput;
    List<ProductsOnShopListModel> shoppingListProductsModels;
    List<ProductModel> products;
    private Integer userId;
    sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

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

        if (shoppingListContainer == null) {
            Toast.makeText(this, "Failed to initialize shopping list container", Toast.LENGTH_LONG).show();
        }

        Spinner spnGroupProducts = findViewById(R.id.spinner_group_products_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGroupProducts.setAdapter(adapter);
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

        newItemRow.addView(checkBox);
        newItemRow.addView(qtyTextView);
        newItemRow.addView(nameTextView);

        shoppingListContainer.addView(newItemRow);
    }

    public void loadpreviousitems(int shoplistid) {
        Call<List<ProductsOnShopListModel>> getshoppinglistproducts = sbAPI.getshoppinglistproducts();
        getshoppinglistproducts.enqueue(new Callback<List<ProductsOnShopListModel>>() {
            @Override
            public void onResponse(Call<List<ProductsOnShopListModel>> call, Response<List<ProductsOnShopListModel>> response) {
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
            public void onFailure(Call<List<ProductsOnShopListModel>> call, Throwable t) {
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
                    for (ProductsOnShopListModel slProduct : shoppingListProductsModels) {
                        if (slProduct.getShoplist_id() == shoplistid && slProduct.getProduct_id() == product.getProductId()) {
                            LinearLayout linearLayout = inflateLinearLayout(NewShoppingListActivity.this);
                            CheckBox checkBox = linearLayout.findViewById(R.id.cbxTicked);
                            TextView textView1 = linearLayout.findViewById(R.id.tvQuantity);
                            TextView tvslproductqty = linearLayout.findViewById(R.id.tvName);
                            checkBox.setChecked(slProduct.isTicked_or_not());
                            textView1.setText("" + slProduct.getShoplistproducts_quantity());
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
        shopListName = shopListNameInput.getText().toString();
        int id= getIntent().getIntExtra("SHOPPING_LIST_ID", 0);
        newShoppingList = new ShopListModel();
        if (shopListName.isEmpty()) {
            newShoppingList.setShoplistName("Shopping List " + currentShopListNameID);
            appState.setShopListNameID(currentShopListNameID + 1);
        } else {
            newShoppingList.setShoplistId(id);
            newShoppingList.setShoplistName(shopListName);
            newShoppingList.setUserIdForShopList(userId);
        }

        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        Call<Void> insertListCall = api.updateShoplist("eq." + id, newShoppingList);
        insertListCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewShoppingListActivity.this, "Shopping List saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    startActivity(new Intent(NewShoppingListActivity.this,ShoppingListActivity.class));
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

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Failed to save Shopping List", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failure to save shopping list: " + t.getMessage());
            }
        });
    }

    public void onCancelNewShopList(View view) {
    }
}
