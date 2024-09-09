package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_LIST = 1;
    Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        fetchShoppingListsFromDatabase(); //and creates buttons dynamically
    }

    public void onAddListClicked(View view) {
        //startActivity(new Intent(ShoppingListActivity.this, NewShoppingListActivity.class));
        Intent intent = new Intent(ShoppingListActivity.this, NewShoppingListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_LIST && resultCode == RESULT_OK) {
            // Fetch and refresh the shopping lists
            fetchShoppingListsFromDatabase();
        }
    }

    public void onEditListClicked(View view) {
    }

    public void onRemoveListClicked(View view) {
    }

    // Method to dynamically create buttons for each shopping list record
    private void createButtonsForShoppingLists(List<ShopListModel> shoppingLists) {
        LinearLayout linearLayout = findViewById(R.id.linear_layout_shop_list); //add buttons to this layout

        linearLayout.removeAllViews(); //clear any existing views first

        //loop through each shopping list and create a button for it
        for (ShopListModel shoppingList : shoppingLists) {
            if(shoppingList.getUserIdForShopList().equals(getCurrentUserIDFromSession())){
                Button button = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 16, 0, 16); // Set margins (left, top, right, bottom)
                button.setLayoutParams(params);
                button.setPadding(16, 1, 16, 1); // Set padding (left, top, right, bottom)
                button.setText(shoppingList.getShoplistName());
                button.setBackgroundResource(R.drawable.shoplistbtn_background);
                button.setTextColor(getResources().getColor(R.color.textColor));
                button.setOnClickListener(v -> {
                    //view contents of list
                    Intent intent = new Intent(ShoppingListActivity.this, ShoppingListContentsActivity.class);
                    intent.putExtra("SHOPPING_LIST_ID", shoppingList.getShoplistId()); //pass shopping list ID
                    intent.putExtra("SHOPPING_LIST_NAME", shoppingList.getShoplistName()); //pass shopping list name
                    startActivity(intent);
                });

                //add the button to the layout
                linearLayout.addView(button);
            }
        }
    }

    private void fetchShoppingListsFromDatabase() {
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<ShopListModel>> call = api.getShoppingLists("*");
        call.enqueue(new Callback<List<ShopListModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopListModel>> call, @NonNull Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ShopListModel> shoppingLists = response.body();
                    Log.d("Supabase Response", response.toString());
                    if (shoppingLists != null && !shoppingLists.isEmpty()) {
                        createButtonsForShoppingLists(shoppingLists);
                    } else {
                        Toast.makeText(ShoppingListActivity.this, "No items found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShoppingListActivity.this, "Response unsuccessful: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopListModel>> call, @NonNull Throwable t) {
                Toast.makeText(ShoppingListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(ShoppingListActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

}