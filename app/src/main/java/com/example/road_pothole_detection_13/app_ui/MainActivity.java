package com.example.road_pothole_detection_13.app_ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.road_pothole_detection_13.NetworkUtils;
import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.road_pothole_detection_13.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        });
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Get user info
        String url = "http://diddysfreakoffparty.online:3000/api/user/profile";
        String token = getIntent().getStringExtra("accessToken");

        NetworkUtils.sendGetRequestWithAuthorization(this, url, token, new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    JSONObject dataJson = responseJson.getJSONObject("data");
                    String fullName = dataJson.getString("fullName");
                    String email = dataJson.getString("email");
                    String photo = dataJson.getString("photo");
                    String birthDay = dataJson.getString("birthDay");
                    String gender = dataJson.getString("gender");
                    String address = dataJson.getString("address");

                    Intent intent = getIntent();
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("photo", photo);
                    intent.putExtra("birthDay", birthDay);
                    intent.putExtra("gender", gender);
                    intent.putExtra("address", address);
                    intent.putExtra("accessToken", token);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorDialog("Error", "Connection error. Please login again");
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();

                    // Delete token in cache
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("accessToken").commit();
                    editor.remove("rememberMe").commit();

                    // Back to login screen
                    Intent intent = new Intent(MainActivity.this ,AuthActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

}