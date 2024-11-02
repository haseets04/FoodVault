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

public class ViewGroupsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_LIST = 1;
    Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        fetchGroupsFromDatabase(); //and creates buttons dynamically
    }

    public void onAddGroupClicked(View view) {
        Intent intent = new Intent(ViewGroupsActivity.this, NewGroupActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_LIST && resultCode == RESULT_OK) {
            // Fetch and refresh the groups
            fetchGroupsFromDatabase();
        }
    }

    public void onEditGroupClicked(View view) {
    }

    public void onRemoveGroupClicked(View view) {
    }

    // Method to dynamically create buttons for each group record
    private void createButtonsForGroups(List<GroupModel> groupsList, List<UsersInGroupModel> userInGroupList) { //fix
        LinearLayout linearLayout = findViewById(R.id.linear_layout_groups); //add buttons to this layout

        linearLayout.removeAllViews(); //clear any existing views first

        //loop through each group and create a button for it
        for (GroupModel group : groupsList) {
            for(UsersInGroupModel userInGroup : userInGroupList){
                if(userInGroup.getUser_id().equals(getCurrentUserIDFromSession()) && (userInGroup.getGroup_id().equals(group.getGroupId()))){
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
                    button.setText(group.getGroupName());
                    button.setBackgroundResource(R.drawable.shoplistbtn_background); //check
                    button.setTextColor(getResources().getColor(R.color.textColor));
                    button.setOnClickListener(v -> {
                        //view contents of list
                        Intent intent = new Intent(ViewGroupsActivity.this, GroupContentsActivity.class);
                        intent.putExtra("GROUP_ID", group.getGroupId()); //pass group ID
                        intent.putExtra("GROUP_NAME", group.getGroupName()); //pass group name
                        startActivity(intent);
                    });

                    //add the button to the layout
                    linearLayout.addView(button);
                }

            }


        }
    }

    private void fetchGroupsFromDatabase() {
        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<GroupModel>> call = api.getGroups("*");
        Call<List<UsersInGroupModel>> usersInGroupCall = api.getUsersInGroups("*");

        call.enqueue(new Callback<List<GroupModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupModel>> call, @NonNull Response<List<GroupModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GroupModel> groups = response.body();
                    Log.d("Supabase Response", response.toString());

                    usersInGroupCall.enqueue(new Callback<List<UsersInGroupModel>>() {
                        @Override
                        public void onResponse(Call<List<UsersInGroupModel>> call, Response<List<UsersInGroupModel>> response) {
                            if(response.isSuccessful() && response.body() != null){
                                List<UsersInGroupModel> usersInGroups = response.body();

                                if (!groups.isEmpty() && !usersInGroups.isEmpty()) {
                                    createButtonsForGroups(groups, usersInGroups);
                                } else {
                                    Toast.makeText(ViewGroupsActivity.this, "No items found.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<UsersInGroupModel>> call, Throwable t) {
                            Toast.makeText(ViewGroupsActivity.this, "Error in UsersInGroup: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(ViewGroupsActivity.this, "Response unsuccessful: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupModel>> call, @NonNull Throwable t) {
                Toast.makeText(ViewGroupsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(ViewGroupsActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

}