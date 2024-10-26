package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private Integer userId;
    private EditText editFirstName, editLastName, editPassword, editConfirmPassword;
    String firstName, lastName, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);

        getExistingUserDetails();
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(UpdateProfileActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    private void getExistingUserDetails(){
        getCurrentUserIDFromSession();

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<UserModel>> getUserCall = api.getUserDetails("eq." + userId);
        getUserCall.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserModel currentUser = response.body().get(0);
                    firstName = currentUser.getUserFirstname();
                    lastName = currentUser.getUserLastname();
                    password = currentUser.getUserPassword();
                    confirmPassword = currentUser.getUserPassword();

                    editFirstName.setText(firstName);
                    editLastName.setText(lastName);
                    editPassword.setText(password);
                    editConfirmPassword.setText(confirmPassword);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Failed to load existing user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(UpdateProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onSaveUpdatesClicked(View view) {
        firstName = editFirstName.getText().toString();
        lastName = editLastName.getText().toString();
        password = editPassword.getText().toString();
        confirmPassword = editConfirmPassword.getText().toString();

        //validate input fields
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Please enter your updated first name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (lastName.isEmpty()) {
            Toast.makeText(this, "Please enter your updated last name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.equals(confirmPassword)){  //can update user record
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Save");
            builder.setMessage("Are you sure you want to save the changes?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getCurrentUserIDFromSession();

                    SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

                    UserModel updatedUser = new UserModel();
                    updatedUser.setUserFirstname(firstName);
                    updatedUser.setUserLastname(lastName);
                    updatedUser.setUserPassword(password);

                    Call<Void> updateCall = api.updateUserDetails("eq." + userId, updatedUser);
                    updateCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UpdateProfileActivity.this, "Current Settings Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpdateProfileActivity.this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(UpdateProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            Toast.makeText(UpdateProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }

    }

    public void onCancelUpdatesClicked(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the updates to your profile?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(UpdateProfileActivity.this, "Profile updates cancelled", Toast.LENGTH_SHORT).show();
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