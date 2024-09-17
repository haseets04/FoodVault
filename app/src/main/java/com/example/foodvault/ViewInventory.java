package com.example.foodvault;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewInventory extends AppCompatActivity {
    private static final String TAG = "ViewInventory";
    private sbAPI_ViewInventory sbAPI;
    private List<Findrow> findrowsList= new ArrayList<>();
    List<ProductModel> listproducts;
    List<LocationModel> listlocations;
    private ArrayAdapter<String> locationAdapter;
    int locid;
    private Integer userId=UserSession.getInstance().getUserSessionId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
        // Initialize Retrofit and API interface


        // Fetch and display data
        fetchAndDisplayData(api);

        ImageButton addRecord = findViewById(R.id.btnaddproduct);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewInventory.this, AddProductActivity.class));
                //fetchAndDisplayData(api);
            }
        });

        Button filteritems=findViewById(R.id.btn_filteritems);
        setupmenu(filteritems);


        ImageButton deleterecord = findViewById(R.id.btnDelete);
        Button btnNewLocation = findViewById(R.id.btn_newLocation);
        btnNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewInventory.this);
                builder.setTitle("Add New Location");

                // Set up the input field
                final EditText input = new EditText(ViewInventory.this);
                input.setHint("Enter location name");
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Add Location", (dialog, which) -> {
                    String newLocationName = input.getText().toString().trim();

                    if (!newLocationName.isEmpty()) {
                        // Add the new location to the list
                        LocationModel newLocation = new LocationModel(newLocationName);

                      listlocations.add(newLocation);


                        Call<LocationModel> insertLocation= api.insertlocation(new LocationModel(newLocationName));
                        insertLocation.enqueue(new Callback<LocationModel>() {
                                                   @Override
                                                   public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                                                       boolean test=response.isSuccessful();
                                                       if(!response.isSuccessful())
                                                           return;
                                                       Toast.makeText(ViewInventory.this,"Location added successfully",Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onFailure(Call<LocationModel> call, Throwable t) {
                                                       Log.e("Location Insert Error", t.getMessage());
                                                   }
                                               }


                        );

                        Toast.makeText(ViewInventory.this, "New Location Added: " + newLocationName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewInventory.this, "Location name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                // Show the dialog
                builder.show();
            }
        });
        deleterecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewInventory.this, "Click a record you want to delete", Toast.LENGTH_SHORT).show();
                TableLayout tableLayout = findViewById(R.id.tblInventory);
                int tblcount = tableLayout.getChildCount();
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
                                             deleteRecord(tableRow, tableLayout, api); // Implement deleteRecord() method

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

       ImageButton btnedit = findViewById(R.id.btnedit);
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
                                               intent.putExtra("category",((TextView) tableRow.getChildAt(3)).getText().toString());
                                               intent.putExtra("location",((TextView) tableRow.getChildAt(4)).getText().toString());
                                               intent.putExtra("product_id",userId);
                                               for (Findrow row:
                                                       findrowsList) {
                                                   if (row!=null&&row.getRecordTag()==tableRow.getTag())
                                                   {
                                                       intent.putExtra("product_id",row.getProduct_id());

                                                       for (ProductModel product:
                                                               listproducts) {
                                                           if(product!=null &&product.getProductId()==row.getProduct_id())
                                                           {
                                                               intent.putExtra("location_id",product.getLocation_id());
                                                               locid=product.getLocation_id();
                                                           }
                                                       }
                                                   }
                                               }
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


    private void deleteRecord(TableRow tableRow, TableLayout table, sbAPI_ViewInventory api){

        for (Findrow row:findrowsList) {
            if (tableRow != null && tableRow.getTag().toString().equals(row.getRecordTag())) {
               Call<Void> deleteinventorycall = api.deleteproduct("eq."+ row.getProduct_id());

               deleteinventorycall.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

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
                   public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                       Log.e(TAG, "unable to connect to product table, product was not deleted");
                   }
               });

            }
        }

      // table.removeView(tableRow);
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(ViewInventory.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }
    public void setupmenu(Button btnfilter)
    {
        btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewInventory.this, btnfilter);
                // Inflate the popup menu using the XML file
                Menu menu= popupMenu.getMenu();
                menu.add(0,1,Menu.NONE,"Categorize Inventory by location");
                menu.add(0,2,Menu.NONE,"Filter by Category");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case 1:
                                showMultipleChoiceDialog();
                                return true;
                            case 2:
                                showMultipleChoiceDialog();
                                return true;
                        }
                       return true;
                    }
                });
                popupMenu.show();
            }
        });
    }
    private void showMultipleChoiceDialog() {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_listview, null);

        ListView listView = dialogView.findViewById(R.id.list_view);

        // Define the data for the ListView
        String[] items = {"Option 1", "Option 2", "Option 3","Option 4","Option 5","Option 1", "Option 2", "Option 3","Option 4","Option 5","Option 1", "Option 2", "Option 3","Option 4","Option 5"};

        // Create and set the custom adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.text_view, items);
        listView.setAdapter(adapter);

        // Create and show the AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Select an Option")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the selected item
                    int selectedPosition = listView.getCheckedItemPosition();
                    if (selectedPosition != ListView.INVALID_POSITION) {
                        String selectedItem = items[selectedPosition];
                        Toast.makeText(this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }





    private void fetchAndDisplayData(sbAPI_ViewInventory sbAPI) {
        Call<List<LocationModel>> locations = sbAPI.getLocations();
        Call<List<ProductModel>> products = sbAPI.getProducts();
       locations.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationModel>> call, @NonNull Response<List<LocationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listlocations = response.body();
                    TableLayout tableLayout = findViewById(R.id.tblInventory);
                    products.enqueue(new Callback<List<ProductModel>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<ProductModel>> call, @NonNull Response<List<ProductModel>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listproducts = response.body();
                                Log.d(TAG, "Products fetched successfully: " + listproducts.toString());

                                for (ProductModel product : listproducts) {
                                        for (LocationModel location : listlocations) {
                                            if (product.getUserIdForProduct().equals(getCurrentUserIDFromSession()) && //for that user
                                                    product.getLocationId() != null &&
                                                    product.getLocationId().equals(location.getLocation_id()))
                                            {
                                                addProductRecord(tableLayout, product, location);
                                            }
                                        }
                                }
                            } else {
                                Log.e(TAG, "Products response unsuccessful or body is null");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<ProductModel>> call, @NonNull Throwable t) {
                            Toast.makeText(ViewInventory.this, "Failed to display products", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to fetch products", t);
                        }
                    });
                } else {
                    Log.e(TAG, "Inventory response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationModel>> call, @NonNull Throwable t) {
               // Toast.makeText(ViewInventory.this, "Failed to display locations", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch locations", t);
            }
        });
    }

    private void addProductRecord(TableLayout tableLayout, ProductModel product, LocationModel location){  //, InventoryModel inv) {
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
        int test= product.getProductQuantity(); //inv.getQuantity();
        quantity.setText(""+test);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(product.getProductExpirationDate());
        expirationdate.setText(formattedDate);
        locationname.setText(location.getLocation_name());
        category.setText(product.getProductCategory());
        Date today = new Date();//getting todays date
        expired.setChecked(product.getProductExpirationDate() != null && product.getProductExpirationDate().before(today)); //expired if equal to and before today

        // Add additional views and set data as needed

        tableLayout.addView(rowView);
    }

}

