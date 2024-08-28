package com.example.foodvault;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewShoppingListActivity extends AppCompatActivity {
    private LinearLayout shoppingListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);

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
    }

    public void onAddItemClicked(View view) { //done in separate Use Case
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

        // Add the elements to the new row
        newItemRow.addView(checkBox);
        newItemRow.addView(qtyTextView);
        newItemRow.addView(nameTextView);

        // Add the new row to the shopping list container
        shoppingListContainer.addView(newItemRow);
    }

    public void onSaveNewShopList(View view) {
        EditText shopListNameInput = findViewById(R.id.shopListName);
        String shopListName = shopListNameInput.getText().toString();

        ShopListModel newShoppingList = new ShopListModel();
        newShoppingList.setShoplistName(shopListName);

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

        Call<Void> insertListCall = api.insertShoppingList(newShoppingList);
        insertListCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewShoppingListActivity.this, "Shopping List saved", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NewShoppingListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onCancelNewShopList(View view) {
        //don't add to DB
        Toast.makeText(NewShoppingListActivity.this, "New Shopping List entry cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
