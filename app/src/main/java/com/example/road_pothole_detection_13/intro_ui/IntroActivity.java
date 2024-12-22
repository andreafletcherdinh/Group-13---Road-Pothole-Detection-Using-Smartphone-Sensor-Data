package com.example.road_pothole_detection_13.intro_ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
            Intent intent = new Intent(this, FirstTime.class);
            startActivity(intent);
            finish();
        }, 3000); // 3 giây
    }
}
