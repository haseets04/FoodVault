package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemToSLActivity extends AppCompatActivity {
    sbAPI_ViewInventory sbAPI=SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
    private List<ProductModel> listproducts2 = new ArrayList<>();

    private List<String> productnames=new ArrayList<>();
    private Integer addedId=new Integer(0);
    private Integer userId=UserSession.getInstance().getUserSessionId();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userId==null)
            Toast.makeText(AddItemToSLActivity.this, "Error: User ID not found", Toast.LENGTH_SHORT).show();



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

       // Thread thread = new Thread(this::fetch);

       /* try{
            thread.start();
            thread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        } */







        products.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listproducts2 = response.body();
                    for (ProductModel products:
                            listproducts2) {
                        if(userId== products.getUserIdForProduct())
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
                            .setTitle("Existing Item")
                            .setMessage("Are you sure you want to add this item it already have in stock in your product table")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<ProductsOnShopListModel> insertitem = sbAPI.insertShoppingListItem(new ProductsOnShopListModel(store.getText().toString(), cbxbought.isChecked(), 4, addedId,quanty.getValue()));
                                    insertitem.enqueue(new Callback<ProductsOnShopListModel>() {

                                        @Override
                                        public void onResponse(Call<ProductsOnShopListModel> call, Response<ProductsOnShopListModel> response) {
                                            if (response.isSuccessful()) {


                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ProductsOnShopListModel> call, Throwable t) {
                                            Log.e("Shopping list insertion", "Item not added to shoppinglist table");
                                        }
                                    });

                                    Toast.makeText(AddItemToSLActivity.this, "Record Successfully edited", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddItemToSLActivity.this, NewShoppingListActivity.class);
                                    intent.putExtra("addclicked",true);
                                    intent.putExtra("purchased",cbxbought.isChecked());
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
                {
                    if(cbxbought.isChecked())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("What is the Quantity Purchased of this item?");

                        // Set up the input
                        final EditText input = new EditText(this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Ensure only numeric input
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String userInput = input.getText().toString();
                                try {
                                    int quantity = Integer.parseInt(userInput);
                                    // Process the quantity as needed
                                    Toast.makeText(AddItemToSLActivity.this, "Quantity entered: " + quantity, Toast.LENGTH_SHORT).show();
                                    ProductModel newproduct=new ProductModel();
                                    newproduct.setProductQuantity(quantity);
                                    newproduct.setProductName(name.getText().toString().trim());
                                    newproduct.setLocationId(2);
                                    newproduct.setUserIdForProduct(userId);
                                    newproduct.setProductCategory("N/A");
                                    newproduct.setProductExpirationDate(new Date());
                                    Call<ProductModel> insertproduct=sbAPI.insertProduct(newproduct);
                                    insertproduct.enqueue(new Callback<ProductModel>() {
                                        @Override
                                        public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {

                                            if(!response.isSuccessful())
                                                return;

                                            Intent intent=new Intent(AddItemToSLActivity.this,NewShoppingListActivity.class);
                                        fetchLastProduct(store,cbxbought,quanty);

                                            boolean itemadded=true;
                                            intent.putExtra("itemadded",itemadded);
                                            Toast.makeText(AddItemToSLActivity.this, "Product Successfully added to ", Toast.LENGTH_SHORT).show();
                                             intent = new Intent(AddItemToSLActivity.this, NewShoppingListActivity.class);
                                             intent.putExtra("addclicked",true);
                                             intent.putExtra("purchased",cbxbought.isChecked());
                                            intent.putExtra("qty", quanty.getValue());
                                            intent.putExtra("name", name.getText().toString());
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onFailure(Call<ProductModel> call, Throwable t) {

                                        }
                                    });
                                } catch (NumberFormatException e) {
                                    Toast.makeText(AddItemToSLActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                    //Call<ProductModel> addproduct=sbAPI.insertProduct(new ProductModel())
                }



        });
    }


    public void fetch(){
        ProductModel temp = new ProductModel();
        Call<List<ProductModel>> productCall = sbAPI.getLastProduct();

        Response<List<ProductModel>> productResponse = null;

        try{
            productResponse = productCall.execute();
        }catch(IOException e){
            e.printStackTrace();
        }


        if (productResponse.isSuccessful()) {
            temp = productResponse.body().get(0);
            Log.i("Custom", temp.toString());
        }else{

            try {
                Log.e("Custom", productResponse.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void fetchLastProduct(TextView store,CheckBox cbxbought,NumberPicker quantity) {



        Call<List<ProductModel>> call = sbAPI.getLastProduct();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ProductModel lastProduct = response.body().get(0);

                    Call<ProductsOnShopListModel> insertitem = sbAPI.insertShoppingListItem(new ProductsOnShopListModel(store.getText().toString(), cbxbought.isChecked(), 4,lastProduct.getProductId() ,quantity.getValue()));


                    insertitem.enqueue(new Callback<ProductsOnShopListModel>() {

                        @Override
                        public void onResponse(Call<ProductsOnShopListModel> call, Response<ProductsOnShopListModel> response) {
                            if (!response.isSuccessful())
                                return;
                            else{
                                try{
                               Log.i("Custom",response.errorBody().string());
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }

                            Toast.makeText(AddItemToSLActivity.this, "Product Successfully added", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ProductsOnShopListModel> call, Throwable t) {
                            Log.e("Shopping list insertion", "Item not added to shoppinglist table");
                        }
                    });
                    Log.d("API", "Last product: " + lastProduct.toString());
                } else {
                    Log.e("API", "No product found or empty response");
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("API", "Failed to fetch last product", t);
            }
        });
    }

}