package com.example.road_pothole_detection_13.intro_ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;

import java.util.Objects;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.intro);

        // Ẩn ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView carImageView = findViewById(R.id.intro_carImageView);
        // Lấy chiều rộng màn hình
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        // Animation: Di chuyển từ trái -> phải
        ObjectAnimator animator = ObjectAnimator.ofFloat(carImageView, "translationX", -200f, screenWidth + 200f);
        animator.setDuration(2500); // Thời gian di chuyển (3 giây)
        animator.setInterpolator(new LinearInterpolator());
        // Lắng nghe vị trí để thay đổi hình ảnh
        animator.addUpdateListener(animation -> {
            float currentPosition = (float) animation.getAnimatedValue();
            if (currentPosition >= screenWidth / 2 - carImageView.getWidth() / 2 &&
                    currentPosition <= screenWidth / 2 + carImageView.getWidth() / 2) {
                // Đổi hình ảnh tại giữa màn hình
                carImageView.setImageResource(R.drawable.car_hitted);

                // Đổi về hình cũ sau 0.5 giây
                new Handler().postDelayed(() -> {
                    carImageView.setImageResource(R.drawable.car);
                }, 100);
            }
        });

        // Thêm Listener để chuyển Activity khi animation kết thúc
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Không cần xử lý tại đây
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Hiển thị icon và label
                Animation fadeIn = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.fade_in);
                ImageView appIcon = findViewById(R.id.intro_iconImageView);
                TextView appLabel = findViewById(R.id.intro_labelTextView);
                TextView creditTextView = findViewById(R.id.intro_creditTextView);
                appIcon.startAnimation(fadeIn);
                appIcon.setVisibility(View.VISIBLE);
                appLabel.startAnimation(fadeIn);
                appLabel.setVisibility(View.VISIBLE);
                creditTextView.startAnimation(fadeIn);
                creditTextView.setVisibility(View.VISIBLE);

                // Hiển thị credit chuyển sang Activiy khác sau 2 giây
                new Handler().postDelayed(() -> {
                    // Chuyển sang Activity mới
                    Intent intent = new Intent(IntroActivity.this, AuthActivity.class);
                    startActivity(intent);

                    // Kết thúc Activity hiện tại (nếu cần)
                    finish();
                }, 3000); // 3000ms = 3 giây
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }
}