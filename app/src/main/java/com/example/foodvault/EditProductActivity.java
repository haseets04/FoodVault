package com.example.foodvault;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        SeekBar seekBar = findViewById(R.id.sk_quantity);
        TextView seekBarValueTextView = findViewById(R.id.tvquantity);

        // Set initial value of TextView
        seekBarValueTextView.setText("Value: " + seekBar.getProgress());

        // Set up a listener for SeekBar changes
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current SeekBar value
                seekBarValueTextView.setText("Value: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can handle actions when the user starts touching the SeekBar if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can handle actions when the user stops touching the SeekBar if needed
            }
        });
    }
}