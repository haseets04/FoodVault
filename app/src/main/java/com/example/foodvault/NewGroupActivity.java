package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupActivity extends AppCompatActivity {
    private LinearLayout groupContainer;
    private AppState appState;
    private int currentGroupNameID;
    private GroupModel newGroup;
    private String groupName;
    private EditText groupNameInput;
    private Integer userId;
    private Integer currentGroupID;
    private final SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
    private UsersInGroupModel newUserInGroup;
    private List<UsersInGroupModel> usersingroups=new ArrayList<>();
    private List<UserModel> groupUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        appState = AppState.getInstance();
        currentGroupNameID = appState.getGroupNameID();

        groupNameInput = findViewById(R.id.groupName);
        groupNameInput.setHint("Group " + currentGroupNameID);

        groupContainer = findViewById(R.id.group_container);
        if (groupContainer == null) {
            Toast.makeText(this, "Failed to initialize group container", Toast.LENGTH_LONG).show();
        }

        getCurrentGroup();
    }

    public void getCurrentGroup() {
        currentGroupID = getIntent().getIntExtra("GROUP_ID", 0);
        if (currentGroupID == 0) {
            Toast.makeText(NewGroupActivity.this, "Group doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);

        Call<List<UsersInGroupModel>> usersInGroupCall = api.getusers("eq." + currentGroupID);
        usersInGroupCall.enqueue(new Callback<List<UsersInGroupModel>>() {
            @Override
            public void onResponse(Call<List<UsersInGroupModel>> call, Response<List<UsersInGroupModel>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(NewGroupActivity.this, "Failed to fetch group members", Toast.LENGTH_SHORT).show();
                    return;
                }

                groupUsers.clear();
                List<UsersInGroupModel> usersInGroup = response.body();
                for (UsersInGroupModel userInGroup : usersInGroup) {
                    if (userInGroup.getGroup_id().equals(currentGroupID)) {
                        fetchUserDetails(userInGroup.getUser_id());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UsersInGroupModel>> call, Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(Integer userId) {
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
        Call<List<UserModel>> callUsers = api.getUserDetails("eq." + userId);
        callUsers.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserModel user = response.body().get(0);
                    groupUsers.add(user);
                   int gusers= groupUsers.size();

                    HashSet<UserModel> setUsers=new HashSet<>(groupUsers);
                    int actualusers=setUsers.size();
                    if(gusers>actualusers)
                        Toast.makeText(NewGroupActivity.this,"Member already added",Toast.LENGTH_SHORT).show();
                    groupUsers=new ArrayList<>(setUsers);
                    populateGroupContainer(groupUsers);
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error fetching user details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateGroupContainer(List<UserModel> users) {
        runOnUiThread(() -> {
            // Clear any existing views except the group name EditText
            int childCount = groupContainer.getChildCount();
            if (childCount > 1) {
                groupContainer.removeViews(1, childCount - 1);
            }
            groupContainer.removeAllViews();

            // Create and add a TextView for each user
            for (UserModel user : users) {
                TextView userView = new TextView(this);

                // Set the text to the user's full name
                String fullName = user.getUserFirstname() + " " + user.getUserLastname();
                userView.setText(fullName);

                // Style the TextView
                userView.setTextSize(16);
                userView.setPadding(20, 10, 20, 10);
                userView.setTextColor(getResources().getColor(android.R.color.black));

                // Create layout parameters
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 8);
                userView.setLayoutParams(params);

                // Add a background with rounded corners
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(8);
                shape.setColor(getResources().getColor(android.R.color.white));
                shape.setStroke(2, getResources().getColor(android.R.color.darker_gray));
                userView.setBackground(shape);

                // Add the TextView to the container
                groupContainer.addView(userView);
            }
        });
    }

    public Integer getCurrentUserIDFromSession() {
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(NewGroupActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    public void onAddMembersToGroupClicked(View view) {
        final EditText userCodeInput = new EditText(this);
        userCodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Enter User Code")
                .setMessage("Please enter the user code (Positive Whole Number only):")
                .setView(userCodeInput)
                .setPositiveButton("OK", (dialog, which) -> {
                    String userCodeString = userCodeInput.getText().toString().trim();
                    if (isNaturalNumber(userCodeString)) {
                        int userCode = Integer.parseInt(userCodeString);
                        addUserToDatabase(Integer.valueOf(userCode));
                    } else {
                        Toast.makeText(this, "Please enter a valid usercode.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addUserToDatabase(Integer userid) {
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
        UsersInGroupModel uig = new UsersInGroupModel();
        uig.setGroup_id(currentGroupID);
        uig.setIs_admin(false);
        uig.setUser_id(userid);

        boolean userExists = false;

        for (UsersInGroupModel user : usersingroups) {
            if (user.getUser_id().equals(userid)&&user.getGroup_id().equals(currentGroupID)) {
                userExists = true;
                break; // No need to continue checking once we found the user
            }
        }

        if (!userExists) {
            usersingroups.add(uig);
            Call<UsersInGroupModel> addCall = api.addusertogroup(uig);
            addCall.enqueue(new Callback<UsersInGroupModel>() {
                @Override
                public void onResponse(@NonNull Call<UsersInGroupModel> call, @NonNull Response<UsersInGroupModel> response) {
                    if (response.isSuccessful()) {

                    } else {
                        Toast.makeText(NewGroupActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UsersInGroupModel> call, @NonNull Throwable t) {
                    //  Toast.makeText(NewGroupActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    fetchUserDetailsAndUpdate(userid);
                }
            });// Only add newUser if it doesn't exist
        }else
        {
            Toast.makeText(NewGroupActivity.this,"User already in group",Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchUserDetailsAndUpdate(Integer userId) {
        sbAPI_ViewInventory api = SupabaseClient.getClient().create(sbAPI_ViewInventory.class);
        Call<List<UserModel>> callUsers = api.getUserDetails("eq." + userId);
        callUsers.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserModel newUser = response.body().get(0);
                    boolean userExists = false;

                    for (UserModel user : groupUsers) {
                        if (user.getUserId().equals(newUser.getUserId())) {
                            userExists = true;
                            break; // No need to continue checking once we found the user
                        }
                    }

                    if (!userExists) {
                        groupUsers.add(newUser); // Only add newUser if it doesn't exist
                    }else
                    {
                        Toast.makeText(NewGroupActivity.this,"User already in group",Toast.LENGTH_SHORT).show();
                    }

                    populateGroupContainer(groupUsers);
                } else {
                    Toast.makeText(NewGroupActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error fetching user details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNaturalNumber(String str) {
        return !str.isEmpty() && str.matches("\\d+");
    }

    public void updateInsertedGroupRecord() {
        groupName = groupNameInput.getText().toString();

        GroupModel updatedGroup = new GroupModel();
        if (groupName.isEmpty()) {
            updatedGroup.setGroupName("Group " + currentGroupNameID);
            appState.setGroupNameID(currentGroupNameID + 1);
        } else {
            updatedGroup.setGroupName(groupName);
        }
        updatedGroup.setGroupId(currentGroupID);
        Call<Void> updateCall = api.updateGroupDetails("eq." + currentGroupID, updatedGroup);
        updateCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewGroupActivity.this, "Current Settings Saved: " + groupName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewGroupActivity.this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void insertNewUsersInGroupInDB() {
        newUserInGroup = new UsersInGroupModel();
        newUserInGroup.setGroup_id(currentGroupID);
        newUserInGroup.setUser_id(getCurrentUserIDFromSession());
        newUserInGroup.setIs_admin(true);

        Call<Void> insertUserInGroupCall = api.insertUserInGroup(newUserInGroup);
        insertUserInGroupCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewGroupActivity.this, "User In Group saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("Supabase Error", "Saving of User in Group Failed: " + response.message() + " - " + errorBody);
                    } catch (Exception e) {
                        Log.e("Supabase Error", "Error reading response body", e);
                    }
                    Toast.makeText(NewGroupActivity.this, "Saving of User in Group Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSaveNewGroupClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Save");
        builder2.setMessage("Are you sure you want to save the entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateInsertedGroupRecord();
                insertNewUsersInGroupInDB();
                finish();
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

    public void onCancelNewGroupClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the new entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCurrentGroup();
                Toast.makeText(NewGroupActivity.this, "New Group entry cancelled", Toast.LENGTH_SHORT).show();
                finish();
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

    public void deleteCurrentGroup() {
        Call<Void> deleteCall = api.deleteGroup("eq." + currentGroupID);
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful())
                    Toast.makeText(NewGroupActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NewGroupActivity.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}