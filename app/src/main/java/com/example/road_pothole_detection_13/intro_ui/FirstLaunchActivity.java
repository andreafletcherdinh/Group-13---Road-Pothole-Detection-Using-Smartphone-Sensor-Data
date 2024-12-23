package com.example.road_pothole_detection_13.intro_ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class FirstLaunchActivity extends AppIntro {
    private boolean tutorialCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome!", "Discover the ultimate pothole detection solution for your journey.", R.drawable.intro1, getResources().getColor(R.color.light_blue_400)));
        addSlide(AppIntroFragment.newInstance("What do we do?", "Our app helps you detect and avoid potholes on any road.", R.drawable.intro2, getResources().getColor(R.color.purple_200)));
        addSlide(AppIntroFragment.newInstance("Why is it beneficial?", "Get timely alerts when you're approaching a pothole to ensure maximum safety.", R.drawable.intro3, getResources().getColor(R.color.gray_400)));
        addSlide(AppIntroFragment.newInstance("Why choose us?", "A user-friendly app that's easy to use and keeps you safer on the road.", R.drawable.intro4, getResources().getColor(R.color.teal_200)));
        setSkipButtonEnabled(true);
        setSkipText("Skip");
        setDoneText("Let's go");
    }

    @Override
    public void onSkipPressed(@NonNull Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        if (!tutorialCompleted) {
            tutorialCompleted = true;
            finishTutorial();
        }
    }

    @Override
    public void onDonePressed(@NonNull Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if (!tutorialCompleted) {
            tutorialCompleted = true;
            finishTutorial();
        }
    }

    private void finishTutorial() {
        // Sau khi FirstTime hoàn tất, chuyển sang AuthActivity
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

}
