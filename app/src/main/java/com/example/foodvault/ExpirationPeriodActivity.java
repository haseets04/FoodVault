package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class ExpirationPeriodActivity extends AppCompatActivity {
    private int expirationPeriod = 30; //default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiration_period);

        NumberPicker expPeriodPicker = findViewById(R.id.number_picker_expiration_period);
        expPeriodPicker.setMinValue(0);
        expPeriodPicker.setMaxValue(365); //change later
        expPeriodPicker.setValue(expirationPeriod); //set default value

        TextView txtSetExpirationPeriod = findViewById(R.id.txt_set_expiration_period);
        txtSetExpirationPeriod.setText("Set to " + expirationPeriod + " days before"); //set with default value

        //set up listener to update TextView when NumberPicker value changes
        expPeriodPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //update expirationPeriod and TextView
                expirationPeriod = newVal;
                txtSetExpirationPeriod.setText("Set to " + expirationPeriod + " days before");
            }
        });

    }

    public void onSaveSettingsClicked(View view) {
        Toast.makeText(this, "Current Settings Saved: " + expirationPeriod, Toast.LENGTH_SHORT).show();
        //finish(); //go back to SettingsActivity
    }

    public void onCancelSettingsClicked(View view) {
        expirationPeriod = 30;
        finish();
    }
}