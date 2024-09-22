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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupActivity extends AppCompatActivity {
    private LinearLayout groupContainer;
    private AppState appState;
    private int currentGroupNameID; //for cancel functionality
    GroupModel newGroup;
    String groupName;
    EditText groupNameInput;
    Integer userId;
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

    public void onSaveNewGroupClicked(View view) {
        groupName = groupNameInput.getText().toString();

        newGroup = new GroupModel();
        newGroup.setUserIdForGroup(getCurrentUserIDFromSession());
        //set shoplist id here or when sharing a list?

        if(groupName.isEmpty() || groupName == null){
            newGroup.setGroupName("Group " + currentGroupNameID);
            appState.setGroupNameID(currentGroupNameID + 1);
        } else{
            newGroup.setGroupName(groupName);
        }

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

        Call<Void> insertGroupCall = api.insertGroup(newGroup);
        insertGroupCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewGroupActivity.this, "Group saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
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
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //confirm saving of changes
        
        
    }

    public void onCancelNewGroupClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the new entry?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
}