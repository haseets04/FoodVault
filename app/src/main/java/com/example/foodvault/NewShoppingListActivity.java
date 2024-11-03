package com.example.foodvault;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class NewShoppingListActivity extends AppCompatActivity {
    private LinearLayout shoppingListContainer;
    private AppState appState;
    private int currentShopListNameID; // for cancel functionality
    private String shopListName;
    private EditText shopListNameInput;
    private List<ProductsOnShopListModel> shoppingListProductsModels;
    private List<ProductModel> products;
    private Integer userId;
    private Integer currentShopListID;
    private final sbAPI_ViewInventory sbAPI = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);

        userId = getCurrentUserIDFromSession();
        if (userId == null || userId == 0) {
            Toast.makeText(this, "Error: Invalid user session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentShopListID = getIntent().getIntExtra("SHOPPING_LIST_ID", -1);
        if (currentShopListID == -1) {
            Toast.makeText(this, "Error: No shopping list ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Toast.makeText(NewShoppingListActivity.this, "" + currentShopListID, Toast.LENGTH_SHORT).show(); //

        appState = AppState.getInstance();
        currentShopListNameID = appState.getShopListNameID(); // store the last saved value

        shopListNameInput = findViewById(R.id.shopListName);
        shopListNameInput.setHint("Shopping List " + currentShopListNameID);

        shoppingListContainer = findViewById(R.id.shopping_list_container);

        if (shoppingListContainer == null) {
            Toast.makeText(this, "Failed to initialize shopping list container", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton add = findViewById(R.id.fltbtn_add_item);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewShoppingListActivity.this, AddItemToSLActivity.class);
                intent.putExtra("SHOPPING_LIST_ID", currentShopListID);
                intent.putExtra("USER_ID", UserSession.getInstance().getUserSessionId()); // Add this line
                startActivity(intent);
            }
        });
        loadpreviousitems();
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(NewShoppingListActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
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

    public void loadpreviousitems() {
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
                loadproducts();
            }

            @Override
            public void onFailure(Call<List<ProductsOnShopListModel>> call, Throwable t) {
                Toast.makeText(NewShoppingListActivity.this, "Failure to load shopping list", Toast.LENGTH_SHORT).show();
                Log.e("Custom", "There was a failure loading shopping list products");
            }
        });
    }

    public void loadproducts() {
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
                        if (slProduct.getShoplist_id() == currentShopListID && slProduct.getProduct_id() == product.getProductId()) {
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
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Save");
        builder2.setMessage("Are you sure you want to save the entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shopListName = shopListNameInput.getText().toString();

                ShopListModel updatedList = new ShopListModel();
                updatedList.setShoplistId(currentShopListID);
                updatedList.setUserIdForShopList(userId);

                if (shopListName.isEmpty()) {
                    updatedList.setShoplistName("Shopping List " + currentShopListNameID);
                    Toast.makeText(NewShoppingListActivity.this, "Shopping List " + currentShopListNameID, Toast.LENGTH_SHORT).show();

                    appState.setShopListNameID(currentShopListNameID + 1);
                } else {
                    updatedList.setShoplistName(shopListName);
                }

                sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

                Call<Void> updateCall = api.updateShoplist("eq." + currentShopListID, updatedList);
                updateCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(NewShoppingListActivity.this, "Shopping List saved", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
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

    public void onCancelNewShopList(View view) { //don't add to DB
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the new entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCurrentShoppingList();
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

    public void deleteCurrentShoppingList(){
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<Void> deleteCall = api.deleteShoppingList("eq."+ currentShopListID);

        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(NewShoppingListActivity.this, "New Shopping List entry cancelled", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
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
}
