package com.example.foodvault;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ViewInventory extends AppCompatActivity {

    private static final String TAG = "ViewInventory";
    private sbAPI_ViewInventory sbAPI;
    private List<Findrow> findrowsList= new ArrayList<>();
    List<InventoryModel> inventory;
    List<ProductModel> listproducts;
    List<LocationModel> listlocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        // Initialize Retrofit and API interface
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        // Fetch and display data

        fetchAndDisplayData(api);
        ImageButton deleterecord= findViewById(R.id.btnDelete);
        deleterecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewInventory.this, "Click a record you want to delete", Toast.LENGTH_SHORT).show();
                TableLayout tableLayout = findViewById(R.id.tblInventory);
                int tblcount=tableLayout.getChildCount();
                for (int i = 1; i < tblcount; i++) {
                    View child = tableLayout.getChildAt(i);

                    if (child instanceof TableRow) {
                        TableRow tableRow = (TableRow) child;
                        tableRow.setClickable(true);
                        tableRow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete Record")
                                        .setMessage("Are you sure you want to delete the " + ((TextView) tableRow.getChildAt(0)).getText().toString() + " record?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Handle the record deletion here
                                             deleteRecord(tableRow, tableLayout,api); // Implement deleteRecord() method

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(ViewInventory.this, "Delete process canceled, Click delete(minus) again", Toast.LENGTH_SHORT).show();

                                            }
                                        }).setCancelable(false)
                                        .show();
                                for (int i = 1; i < tblcount; i++) {
                                    View child = tableLayout.getChildAt(i);

                                    if (child instanceof TableRow) {
                                        TableRow tableRow = (TableRow) child;
                                        tableRow.setClickable(false);
                                    }
                                }
                            }
                        });
                    }
                }


            }
        });

        ImageButton btnedit=findViewById(R.id.btnedit);
       btnedit.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View v) {
               Toast.makeText(ViewInventory.this, "Click a record you want to edit", Toast.LENGTH_SHORT).show();
               TableLayout tableLayout = findViewById(R.id.tblInventory);
               int tblcount=tableLayout.getChildCount();
               for (int i = 1; i < tblcount; i++) {
                   View child = tableLayout.getChildAt(i);

                   if (child instanceof TableRow) {
                       TableRow tableRow = (TableRow) child;
                       tableRow.setClickable(true);
                       tableRow.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               new AlertDialog.Builder(v.getContext())
                                       .setTitle("Edit Record")
                                       .setMessage("Are you sure you want to edit the " + ((TextView) tableRow.getChildAt(0)).getText().toString() + " record?")
                                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               TextView tvquantity= (TextView) tableRow.getChildAt(1);
                                               int qty=(Integer.parseInt(tvquantity.getText().toString()));
                                               Intent intent=new Intent(ViewInventory.this, EditProductActivity.class);
                                               intent.putExtra("name",((TextView) tableRow.getChildAt(0)).getText().toString());
                                               intent.putExtra("quantity",qty);
                                               intent.putExtra("expiration",((TextView) tableRow.getChildAt(2)).getText().toString());
                                               intent.putExtra("category",tableRow.getChildAt(3).toString());
                                               intent.putExtra("location",((TextView) tableRow.getChildAt(4)).getText().toString());
                                               startActivity(intent);

                                           }
                                       })
                                       .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               Toast.makeText(ViewInventory.this, "Edit process canceled, Click Edit(pen) again", Toast.LENGTH_SHORT).show();

                                           }
                                       }).setCancelable(false)
                                       .show();

                               for (int i = 1; i < tblcount; i++) {
                                   View child = tableLayout.getChildAt(i);

                                   if (child instanceof TableRow) {
                                       TableRow tableRow = (TableRow) child;
                                       tableRow.setClickable(false);
                                   }
                               }
                           }
                       });
                   }
               }


           }
       });
    }

    private void deleteRecord(TableRow tableRow,TableLayout table,sbAPI_ViewInventory api ){

        for (Findrow row:findrowsList
             ) {
            if (tableRow != null&&tableRow.getTag().toString()==row.getRecordTag()) {
               Call<Void> deleteinventorycall= api.deleteinvrecord("eq."+row.getProduct_id());

               deleteinventorycall.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(Call<Void> call, Response<Void> response) {

                       if(response.isSuccessful())
                       {
                           Toast.makeText(ViewInventory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                           for (int i = table.getChildCount() - 1; i > 0; i--) {
                               table.removeViewAt(i);
                           }
                           fetchAndDisplayData(api);
                       }
                       else
                           Log.e(TAG, "Product response unsuccessful");

                   }

                   @Override
                   public void onFailure(Call<Void> call, Throwable t) {
                       Log.e(TAG, "unable to connect to product table, product was not deleted");
                   }
               });

            }
        }

      // table.removeView(tableRow);


    }

    private void fetchAndDisplayData(sbAPI_ViewInventory sbAPI) {
        Call<List<LocationModel>> locations= sbAPI.getLocations();
        Call<List<InventoryModel>> inventories=sbAPI.getInventory();
        Call<List<ProductModel>> products=sbAPI.getProducts();
       locations.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                     listlocations = response.body();
                    TableLayout tableLayout = findViewById(R.id.tblInventory);
                   inventories.enqueue(new Callback<List<InventoryModel>>() {
                        @Override
                        public void onResponse(Call<List<InventoryModel>> call, Response<List<InventoryModel>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                inventory = response.body();

                                products.enqueue(new Callback<List<ProductModel>>() {
                                    @Override
                                    public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                             listproducts = response.body();

                                            for (ProductModel product : listproducts) {
                                                for (InventoryModel inv : inventory) {
                                                    for (LocationModel location : listlocations) {
                                                        if (product.getProductId().equals(inv.getProductId()) && product.getLocation_id().equals(location.getLocation_id())) {
                                                            addProductRecord(tableLayout, product, location, inv);
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "Products response unsuccessful or body is null");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                                        Toast.makeText(ViewInventory.this, "Failed to display products", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed to fetch products", t);
                                    }
                                });
                            } else {
                                Log.e(TAG, "Inventory response unsuccessful or body is null");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<InventoryModel>> call, Throwable t) {
                            Toast.makeText(ViewInventory.this, "Failed to display inventory", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to fetch inventory", t);
                        }
                    });
                }  else {
                    Log.e(TAG, "Locations response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
               // Toast.makeText(ViewInventory.this, "Failed to display locations", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch locations", t);
            }
        });
    }

    private void addProductRecord(TableLayout tableLayout, ProductModel product,LocationModel location, InventoryModel inv) {
        LayoutInflater inflater = LayoutInflater.from(this);
        int count=tableLayout.getChildCount();
        String rowTag = "row" + (count);
        findrowsList.add(new Findrow(product.getProductId(),rowTag));
        TableRow rowView = (TableRow) inflater.inflate(R.layout.standard_row, tableLayout, false);
        rowView.setTag(rowTag);
        TextView tvproductname = (TextView) rowView.getChildAt(0);
        TextView quantity = (TextView) rowView.getChildAt(1);
        TextView expirationdate = (TextView) rowView.getChildAt(2);
        TextView locationname = (TextView) rowView.getChildAt(4);
        TextView category = (TextView) rowView.getChildAt(3);
        CheckBox expired = (CheckBox) rowView.getChildAt(5);
        tvproductname.setText(product.getProductName());
        int test= inv.getQuantity();
        quantity.setText(""+test);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(product.getProductExpirationDate());
        expirationdate.setText(formattedDate);
        locationname.setText(location.getLocation_name());
        category.setText(product.getProductCategory());
        Date today = new Date();//getting todays date
        expired.setChecked(product.getProductExpirationDate() != null && product.getProductExpirationDate().before(today));


        // Add additional views and set data as needed

        tableLayout.addView(rowView);
    }

}
