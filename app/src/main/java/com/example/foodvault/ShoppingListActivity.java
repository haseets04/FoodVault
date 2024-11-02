package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
    private Integer userId;
    private List<ShopListModel> shoppingLists;
    private Integer newestShopListId;
    private SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        fetchShoppingListsFromDatabase(); //and creates buttons dynamically
    }

    public void onAddListClicked(View view) {
        ShopListModel sl = new ShopListModel();
        sl.setUserIdForShopList(userId);

        Call<Void> newshoppinglist = api.insertShoppingList2(sl);
        newshoppinglist.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    fetchNewestShoppingList();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShoppingListActivity.this, "Error adding shopping list", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchNewestShoppingList() {
        Call<List<ShopListModel>> call = api.getShoppingLists("*");
        call.enqueue(new Callback<List<ShopListModel>>() {
            @Override
            public void onResponse(Call<List<ShopListModel>> call, Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ShopListModel> allLists = response.body();
                    // Find the newest shopping list for the current user
                    ShopListModel newestList = null;
                    for (ShopListModel list : allLists) {
                        if (list.getUserIdForShopList().equals(userId)) { //dont show empty named lists
                            if (newestList == null || list.getShoplistId() > newestList.getShoplistId()) {
                                newestList = list;
                            }
                        }
                    }

                    if (newestList != null) {
                        newestShopListId = newestList.getShoplistId();
                        Intent intent = new Intent(ShoppingListActivity.this, NewShoppingListActivity.class);
                        intent.putExtra("SHOPPING_LIST_ID", newestShopListId);
                        startActivityForResult(intent, REQUEST_CODE_ADD_LIST);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ShopListModel>> call, Throwable t) {
                Toast.makeText(ShoppingListActivity.this, "Error fetching newest shopping list", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Error: " + t.getMessage());
            }
        });
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
                int width = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        width, // Set fixed width in pixels
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
                    shoppingLists = response.body();
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

    public void onHomeClicked(View view) {
        startActivity(new Intent(ShoppingListActivity.this, DashboardActivity.class));
    }
}