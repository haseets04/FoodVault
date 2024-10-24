package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupActivity extends AppCompatActivity {
    private LinearLayout groupContainer;
    private AppState appState;
    private int currentGroupNameID; //for cancel functionality
    private GroupModel newGroup;
    private String groupName;
    private EditText groupNameInput;
    private Integer userId;
    private Integer currentGroupID;
    private SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
    private UsersInGroupModel newUserInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        appState = AppState.getInstance();
        currentGroupNameID = appState.getGroupNameID(); //store the last saved value

        groupNameInput = findViewById(R.id.groupName);
        groupNameInput.setHint("Group " + currentGroupNameID);

        groupContainer = findViewById(R.id.group_container);
        if (groupContainer == null) {
            Toast.makeText(this, "Failed to initialize group container", Toast.LENGTH_LONG).show();
        }

        createNewGroupRecord();

    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(NewGroupActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    public void onAddMembersToGroupClicked(View view) {
    }

    public void createNewGroupRecord(){
        groupName = groupNameInput.getText().toString();

        newGroup = new GroupModel();
        //set shoplist id here or when sharing a list?

        if(groupName.isEmpty()){
            newGroup.setGroupName("Group " + currentGroupNameID);
            appState.setGroupNameID(currentGroupNameID + 1);
        } else{
            newGroup.setGroupName(groupName);
        }

        Call<List<GroupModel>> insertGroupCall = api.insertGroup(newGroup);
        insertGroupCall.enqueue(new Callback<List<GroupModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupModel>> call, @NonNull Response<List<GroupModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GroupModel insertedGroup = response.body().get(0);
                    currentGroupID = insertedGroup.getGroupId();

                    Log.i("Group Id", String.valueOf(currentGroupID));
                    Toast.makeText(NewGroupActivity.this, "Group saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("Supabase Error", "Saving of Group Failed: " + response.message() + " - " + errorBody);
                    } catch (Exception e) {
                        Log.e("Supabase Error", "Error reading response body", e);
                    }
                    Toast.makeText(NewGroupActivity.this, "Saving of Group Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupModel>> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateInsertedGroupRecord(){
        groupName = groupNameInput.getText().toString();

        GroupModel updatedGroup = new GroupModel();
        if(groupName.isEmpty()){
            updatedGroup.setGroupName("Group " + currentGroupNameID);
            appState.setGroupNameID(currentGroupNameID + 1);
        } else{
            updatedGroup.setGroupName(groupName);
        }

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

    public void insertNewUsersInGroupInDB(){
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
        updateInsertedGroupRecord();
        insertNewUsersInGroupInDB();
        finish();
        //confirm saving of changes
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
                finish();            }
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

    public void deleteCurrentGroup(){
        Call<Void> deleteCall = api.deleteGroup("eq."+ currentGroupID);
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful())
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