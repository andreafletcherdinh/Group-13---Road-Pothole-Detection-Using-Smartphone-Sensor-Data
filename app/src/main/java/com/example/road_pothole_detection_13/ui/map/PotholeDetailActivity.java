package com.example.road_pothole_detection_13.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.road_pothole_detection_13.R;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PotholeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_detail);

        // Thêm RadioGroup
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button btnSubmit = findViewById(R.id.btn_submit);

// Xử lý sự kiện cho nút Submit
        btnSubmit.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId(); // Lấy ID của RadioButton đã chọn

            if (selectedId == R.id.rb_light) {
                sendResult("Light");
            } else if (selectedId == R.id.rb_moderate) {
                sendResult("Moderate");
            } else if (selectedId == R.id.rb_severe) {
                sendResult("Severe");
            } else {
                Toast.makeText(this, "Please select a severity level", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // **Hàm gửi kết quả về Activity trước đó**
    private void sendResult(String severity) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("severity", severity);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}


