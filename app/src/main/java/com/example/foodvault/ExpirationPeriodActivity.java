package com.example.foodvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class ExpirationPeriodActivity extends AppCompatActivity {
    private AppState appState;
    private int currentExpirationPeriod; //for cancel functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiration_period);

        appState = AppState.getInstance();
        currentExpirationPeriod = appState.getExpirationPeriod(); //store the last saved value

        NumberPicker expPeriodPicker = findViewById(R.id.number_picker_expiration_period);
        expPeriodPicker.setMinValue(0);
        expPeriodPicker.setMaxValue(365); //change later
        expPeriodPicker.setValue(currentExpirationPeriod); //set to last saved/default value

        TextView txtSetExpirationPeriod = findViewById(R.id.txt_set_expiration_period);
        txtSetExpirationPeriod.setText("Set to " + currentExpirationPeriod + " days before"); //set default value

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

    public void onSaveSettingsClicked(View view) {
        //confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Save");
        builder.setMessage("Are you sure you want to save the changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                appState.setExpirationPeriod(currentExpirationPeriod); //save changes
                Toast.makeText(ExpirationPeriodActivity.this , "Current Settings Saved: " + currentExpirationPeriod, Toast.LENGTH_SHORT).show();
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