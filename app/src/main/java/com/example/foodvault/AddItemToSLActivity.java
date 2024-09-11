package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemToSLActivity extends AppCompatActivity {
    sbAPI_ViewInventory sbAPI=SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
    private List<ProductModel> listproducts2 = new ArrayList<>();
    List<InventoryModel> inventory=new ArrayList<>();
    private List<String> productnames=new ArrayList<>();
    private Integer addedId=new Integer(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_slactivity);
        NumberPicker quanty=findViewById(R.id.number_picker_quantity);
        quanty.setMaxValue(100);
        quanty.setMinValue(0);
        TextView tvquant= findViewById(R.id.tvquantity);
        AutoCompleteTextView name=findViewById(R.id.autoname);
        CheckBox cbxbought=findViewById(R.id.cbxbought);
        TextView store=findViewById(R.id.tvgrocerystore);
        Button add=findViewById(R.id.btn_add);
        Call<List<ProductModel>> products = sbAPI.getProducts();

        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listproducts2 = response.body();
                    for (ProductModel products:
                            listproducts2) {
                        productnames.add(products.getProductName());
                    }
                    ArrayAdapter<String> shoppinglistAdapter = new ArrayAdapter<>(AddItemToSLActivity.this,
                            android.R.layout.simple_dropdown_item_1line, productnames);
                    name.setAdapter(shoppinglistAdapter);
                    name.setThreshold(0);
                } else {
                    Log.e("Edit Product", "Products response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("Edit Product", "Failed to fetch products", t);
            }
        });
        quanty.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tvquant.setText("Quantity To Buy: "+ newVal);

            }
        });



        add.setOnClickListener(v->
        {
            boolean checked = false;
            for (ProductModel product :
                    listproducts2) {

                if (name.getText().toString().toUpperCase().equals(product.getProductName().toUpperCase()) && !checked) {
                    addedId = product.getProductId();
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Record")
                            .setMessage("Are you sure you want to add this item it already have in stock in your product table")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<ShoppingListProductsModel> insertitem = sbAPI.insertShoppingListItem(new ShoppingListProductsModel(store.getText().toString(), cbxbought.isChecked(), 4, addedId));
                                    insertitem.enqueue(new Callback<ShoppingListProductsModel>() {

                                        @Override
                                        public void onResponse(Call<ShoppingListProductsModel> call, Response<ShoppingListProductsModel> response) {
                                            if (response.isSuccessful()) {


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ShoppingListProductsModel> call, Throwable t) {
                                            Log.e("Shopping list insertion", "Item not added to shoppinglist table");
                                        }
                                    });

                                    Toast.makeText(AddItemToSLActivity.this, "Record Successfully edited", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddItemToSLActivity.this, NewShoppingListActivity.class);
                                    intent.putExtra("qty", quanty.getValue());
                                    intent.putExtra("name", name.getText().toString());
                                    startActivity(intent);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(AddItemToSLActivity.this, " Product was not added to the shopping list.If you wish to exit, click the back button", Toast.LENGTH_SHORT).show();


                                }
                            }).setCancelable(false)
                            .show();


                    checked = true;
                }







            }
           if(!checked)
            Toast.makeText(AddItemToSLActivity.this, "Cant add product that is not listed under inventory", Toast.LENGTH_SHORT).show();







        });
    }

}