package com.example.foodvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpirationPeriodActivity extends AppCompatActivity {
    //private AppState appState;
    private int currentExpirationPeriod; //for cancel functionality
    NumberPicker expPeriodPicker;
    TextView txtSetExpirationPeriod;
    Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiration_period);

        expPeriodPicker = findViewById(R.id.number_picker_expiration_period);
        expPeriodPicker.setMinValue(0);
        expPeriodPicker.setMaxValue(365); //change later

        txtSetExpirationPeriod = findViewById(R.id.txt_set_expiration_period);

        getCurrentExpirationPeriodFromDB();

        //TextView updates when NumberPicker value changes
        expPeriodPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //update expirationPeriod and TextView
                currentExpirationPeriod = newVal;
                txtSetExpirationPeriod.setText("Set to " + newVal + " days before");
            }
        });
    }

    public Integer getCurrentUserIDFromSession(){
        userId = UserSession.getInstance().getUserSessionId();
        if (userId == null) {
            Toast.makeText(ExpirationPeriodActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return userId;
    }

    public void getCurrentExpirationPeriodFromDB(){
        getCurrentUserIDFromSession();

        SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);
        Call<List<UserModel>> getUserCall = api.getUserDetails("eq." + userId);
        getUserCall.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserModel currentUser = response.body().get(0);
                    currentExpirationPeriod = currentUser.getExpirationPeriod(); //get expiration period from DB

                    expPeriodPicker.setValue(currentExpirationPeriod); //set to last saved/default value
                    txtSetExpirationPeriod.setText("Set to " + currentExpirationPeriod + " days before"); //set last saved/default value

                } else {
                    Toast.makeText(ExpirationPeriodActivity.this, "Failed to load expiration period", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Toast.makeText(ExpirationPeriodActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSaveSettingsClicked(View view) {
        //confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Save");
        builder.setMessage("Are you sure you want to save the changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //appState.setExpirationPeriod(currentExpirationPeriod); //save changes locally

                //update current user expiration period in DB
                getCurrentUserIDFromSession();

                SupabaseAPI api = SupabaseClient.getClient().create(SupabaseAPI.class);

                UserModel updatedUser = new UserModel();
                updatedUser.setExpirationPeriod(currentExpirationPeriod);
                UserSession.getInstance().setExpiration_period(currentExpirationPeriod); //to use in NotificationActivity

                Call<Void> updateCall = api.updateUserDetails("eq." + userId, updatedUser);
                updateCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ExpirationPeriodActivity.this, "Current Settings Saved: " + currentExpirationPeriod, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExpirationPeriodActivity.this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                            /*try {
                                String errorBody = response.errorBody().string(); // Inspect the error message from the server
                                Toast.makeText(ExpirationPeriodActivity.this, "Failed: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(ExpirationPeriodActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                finish(); //go back to SettingsActivity
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
    }

    public void onCancelSettingsClicked(View view) {
        //confirmation dialog
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Confirm Cancel");
        builder2.setMessage("Are you sure you want to cancel the edited changes?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish(); //go back to SettingsActivity
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
}