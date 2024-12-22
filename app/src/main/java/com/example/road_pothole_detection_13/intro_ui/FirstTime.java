package com.example.road_pothole_detection_13.intro_ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class FirstTime extends AppIntro {
    private boolean tutorialCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Hey", "This is Inside Android", R.drawable.intro1, getResources().getColor(R.color.light_blue_400)));
        addSlide(AppIntroFragment.newInstance("What we do?", "We detect pothole in your route", R.drawable.intro2, getResources().getColor(R.color.gray_400)));
        addSlide(AppIntroFragment.newInstance("Is it benefit?", "We will alert when you close with pothole", R.drawable.intro3, getResources().getColor(R.color.gray_400)));
        addSlide(AppIntroFragment.newInstance("What do we have", "Safe and easy to use", R.drawable.intro4, getResources().getColor(R.color.black)));
        setSkipButtonEnabled(true);
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
