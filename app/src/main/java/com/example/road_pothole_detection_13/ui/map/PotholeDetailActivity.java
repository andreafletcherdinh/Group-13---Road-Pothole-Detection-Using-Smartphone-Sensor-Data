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

        // **FindViewById cho các thành phần**
        RadioButton rbLight = findViewById(R.id.rb_light);
        RadioButton rbModerate = findViewById(R.id.rb_moderate);
        RadioButton rbSevere = findViewById(R.id.rb_severe);
        Button btnSubmit = findViewById(R.id.btn_submit);

        // **Thiết lập OnClickListener cho Button Submit**
        btnSubmit.setOnClickListener(v -> {
            // Kiểm tra trạng thái của RadioButton
            if (rbLight.isChecked()) {
                sendResult("Light");
            } else if (rbModerate.isChecked()) {
                sendResult("Moderate");
            } else if (rbSevere.isChecked()) {
                sendResult("Severe");
            } else {
                // Hiển thị thông báo nếu chưa chọn mức độ
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


