package com.example.road_pothole_detection_13.ui.map;

import android.os.Bundle;
import android.widget.TextView;
import com.example.road_pothole_detection_13.R;
import androidx.appcompat.app.AppCompatActivity;


public class PotholeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_detail);

        // Get the pothole ID from the intent
        String potholeId = getIntent().getStringExtra("pothole_id");

        // Display pothole details based on the ID
        TextView detailTextView = findViewById(R.id.pothole_detail);
        detailTextView.setText("Details for: " + potholeId);
    }
}