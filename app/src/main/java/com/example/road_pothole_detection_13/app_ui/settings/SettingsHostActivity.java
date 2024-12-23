package com.example.road_pothole_detection_13.app_ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.road_pothole_detection_13.R;

public class SettingsHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_host);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro_carImageView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Open fragment
        Intent intent = getIntent();
        String message = intent.getStringExtra("fragment");
        if (message.equals("account")) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_settings, new AccountSettingFragment())
                        .commit();
            }
        } else if (message.equals("about_us")) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_settings, new AboutUsFragment())
                        .commit();
            }
        } else if (message.equals("app_settings")) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_settings, new AppSettingsFragment())
                        .commit();
            }
        }
    }
}