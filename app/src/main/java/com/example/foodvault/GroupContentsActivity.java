package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupContentsActivity extends AppCompatActivity {
    private SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
    private Integer groupIDOfBtn;
    private String shopListName;
    private List<Integer> selectedUserIds;

    // Declare a map to hold the member IDs and their corresponding TableRows
    private SparseArray<TableRow> memberRowsMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contents);

        TextView txtGroupName = findViewById(R.id.txt_group_name);
        String groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName != null) {
            txtGroupName.setText(groupName); //pass button text
        }

        groupIDOfBtn = getIntent().getIntExtra("GROUP_ID", -1);
        if (groupIDOfBtn == -1) {
            Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchAndDisplayGroupMembersFromDB();
    }

    private void fetchAndDisplayGroupMembersFromDB() {
        Call<List<UsersInGroupModel>> groupMembersCall = api.getMembersByGroupID("eq." + groupIDOfBtn);

        groupMembersCall.enqueue(new Callback<List<UsersInGroupModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsersInGroupModel>> call, @NonNull Response<List<UsersInGroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UsersInGroupModel> groupMembers = response.body();
                    if (!groupMembers.isEmpty()) {
                        displayGroupMembers(groupMembers);
                    } else {
                        Toast.makeText(GroupContentsActivity.this, "No group members found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GroupContentsActivity.this, "Failed to load group members", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UsersInGroupModel>> call, @NonNull Throwable t) {
                Toast.makeText(GroupContentsActivity.this, "Error loading group members", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch group members", t);
            }
        });
    }

    private void displayGroupMembers(List<UsersInGroupModel> groupMembers) {
        TableLayout tableLayout = findViewById(R.id.tblGroupContents);
        tableLayout.removeAllViews();   //clear any existing rows except the header row

        fetchShoppingListLinkedWithGroupID(groupMembers, tableLayout);
    }

    private void fetchShoppingListLinkedWithGroupID(List<UsersInGroupModel> groupMembers, TableLayout tableLayout) {
        Call<List<ShopListModel>> call = api.getShopListByGroupID("eq." + groupIDOfBtn);

        call.enqueue(new Callback<List<ShopListModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopListModel>> call, @NonNull Response<List<ShopListModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ShopListModel shopList = response.body().get(0);
                    shopListName = shopList != null ? shopList.getShoplistName() : "None";
                } else {
                    shopListName = "None"; // Set a default value if no matching list is found
                    Toast.makeText(GroupContentsActivity.this, "Failed to load shop list with groupID", Toast.LENGTH_SHORT).show();
                }
                // Display the header row with the fetched shop list name
                addHeaderRow(tableLayout);
                // Continue to display group members now that header is set
                displayGroupMemberRows(groupMembers, tableLayout);
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopListModel>> call, @NonNull Throwable t) {
                shopListName = "Null"; // Set a default in case of failure
                Toast.makeText(GroupContentsActivity.this, "Error loading shop list with groupID", Toast.LENGTH_SHORT).show();
                Log.e("Supabase Error", "Failed to fetch shop list with groupID", t);
                // Display the header row and continue to display group members even if the fetch failed
                addHeaderRow(tableLayout);
                displayGroupMemberRows(groupMembers, tableLayout);
            }
        });
    }

    private void addHeaderRow(TableLayout tableLayout) {
        TableRow headerRow = new TableRow(this);
        TableRow row = new TableRow(this);
        TableRow headerRow2 = new TableRow(GroupContentsActivity.this);

        TextView headerTextView = new TextView(this);
        headerTextView.setText("Shopping list shared: ");
        headerTextView.setTextColor(getResources().getColor(R.color.black));
        headerTextView.setTextSize(20);
        headerTextView.setTypeface(null, Typeface.BOLD);
        headerTextView.setPadding(8, 8, 8, 8);

        TextView rowTextView = new TextView(this);
        rowTextView.setText(shopListName + "\n");
        rowTextView.setTextColor(getResources().getColor(R.color.black));
        rowTextView.setTextSize(18);
        rowTextView.setPadding(8, 8, 8, 8);

        TextView header2TextView = new TextView(GroupContentsActivity.this);
        header2TextView.setText("Group members: ");
        header2TextView.setTextColor(getResources().getColor(R.color.black));
        header2TextView.setTextSize(20);
        header2TextView.setTypeface(null, Typeface.BOLD);
        header2TextView.setPadding(8, 8, 8, 8);

        headerRow.addView(headerTextView);
        row.addView(rowTextView);
        headerRow2.addView(header2TextView);

        tableLayout.addView(headerRow);
        tableLayout.addView(row);
        tableLayout.addView(headerRow2);
    }

    private void displayGroupMemberRows(List<UsersInGroupModel> groupMembers, TableLayout tableLayout) {
        selectedUserIds = new ArrayList<>();

        // Clear previous rows from TableLayout
        //tableLayout.removeAllViews();
        memberRowsMap.clear();

        for (UsersInGroupModel member : groupMembers) {
            Integer memberUserID = member.getUser_id();
            Call<List<UserModel>> getUserCall = api.getUserDetails("eq." + memberUserID);

            getUserCall.enqueue(new Callback<List<UserModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        UserModel currentUser = response.body().get(0);
                        String firstName = currentUser.getUserFirstname();
                        String lastName = currentUser.getUserLastname();
                        boolean isAnAdmin = member.isIs_admin();

                        TableRow row = new TableRow(GroupContentsActivity.this);
                        row.setLayoutParams(new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        if (isAnAdmin) {
                            TextView adminTextView = new TextView(GroupContentsActivity.this);
                            adminTextView.setText(firstName + " " + lastName + " (Admin)");
                            adminTextView.setTextColor(getResources().getColor(R.color.black));
                            adminTextView.setTextSize(18);

                            row.addView(adminTextView);

                            tableLayout.addView(row); // Add admin row at the top
                        } else {
                            CheckBox checkBox = new CheckBox(GroupContentsActivity.this);
                            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    selectedUserIds.add(memberUserID);
                                } else {
                                    selectedUserIds.remove(memberUserID);
                                }
                            });
                            checkBox.setLayoutParams(new TableRow.LayoutParams(
                                    0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f));
                            row.addView(checkBox);

                            TextView memberTextView = new TextView(GroupContentsActivity.this);
                            memberTextView.setText(firstName + " " + lastName + " (Member)");
                            memberTextView.setTextColor(getResources().getColor(R.color.black));
                            memberTextView.setTextSize(18);
                            memberTextView.setLayoutParams(new TableRow.LayoutParams(
                                    0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                            memberTextView.setPadding(4, 0, 0, 0);

                            row.addView(memberTextView);

                            tableLayout.addView(row);

                            // Store this row in the map with memberUserID as the key
                            memberRowsMap.put(memberUserID, row);
                        }
                    } else {
                        Toast.makeText(GroupContentsActivity.this, "Failed to get member details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                    Toast.makeText(GroupContentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void onAddMemberClicked(View view) {
    }

    public void onRemoveMemberClicked(View view) {
        if (selectedUserIds.isEmpty()) {
            Toast.makeText(GroupContentsActivity.this, "Please select members to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(GroupContentsActivity.this)
                .setTitle("Confirm Removal of Member(s)")
                .setMessage("Are you sure you want to remove the selected member(s)?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with deletion
                    for (Integer memberUserID : selectedUserIds) {
                        Call<Void> deleteUsersCall = api.deleteMembersInGroup("eq." + memberUserID, "eq." + groupIDOfBtn);
                        deleteUsersCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Remove row from TableLayout
                                    TableRow row = memberRowsMap.get(memberUserID);
                                    if (row != null) {
                                        TableLayout tableLayout = findViewById(R.id.tblGroupContents);
                                        tableLayout.removeView(row); // Remove row from the display
                                        memberRowsMap.remove(memberUserID); // Remove entry from map
                                    }
                                } else {
                                    Toast.makeText(GroupContentsActivity.this, "Failed to delete Members: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Toast.makeText(GroupContentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Clear selected IDs list after processing
                    selectedUserIds.clear();
                    Toast.makeText(GroupContentsActivity.this, "Members deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}