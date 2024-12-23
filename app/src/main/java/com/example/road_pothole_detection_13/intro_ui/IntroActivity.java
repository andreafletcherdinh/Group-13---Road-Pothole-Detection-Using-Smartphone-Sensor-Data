package com.example.road_pothole_detection_13.intro_ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        // Ẩn ActionBar (nếu có)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hiệu ứng animation cho carImageView
        ImageView carImageView = findViewById(R.id.intro_carImageView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;

        ObjectAnimator animator = ObjectAnimator.ofFloat(carImageView, "translationX", -200f, screenWidth + 200f);
        animator.setDuration(2500); // Thời gian 2.5 giây
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

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                showCreditsAndTransition();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
    }

    private void showCreditsAndTransition() {
        ImageView appIcon = findViewById(R.id.intro_iconImageView);
        TextView appLabel = findViewById(R.id.intro_labelTextView);
        TextView creditTextView = findViewById(R.id.intro_creditTextView);

        appIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        appIcon.setVisibility(View.VISIBLE);
        appLabel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        appLabel.setVisibility(View.VISIBLE);
        creditTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        creditTextView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            // Kiểm tra xem có phải lần đầu tiên mở app không
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            Boolean isFirstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
            Intent intent;
            // Nếu có
            if (isFirstLaunch) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstLaunch", false);
                editor.apply();
                intent = new Intent(this, FirstLaunchActivity.class);
            }
            else {
                intent = new Intent(this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000); // 3 giây
    }
}
