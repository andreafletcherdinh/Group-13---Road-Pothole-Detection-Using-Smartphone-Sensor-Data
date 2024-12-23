package com.example.road_pothole_detection_13.app_ui.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.road_pothole_detection_13.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppSettingsFragment newInstance(String param1, String param2) {
        AppSettingsFragment fragment = new AppSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vibration setting
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        Boolean isVibrationAllowed = sharedPreferences.getBoolean("isVibrationAllowed", true);
        Switch vibrationSwitch = view.findViewById(R.id.vibrate_switch);
        vibrationSwitch.setChecked(isVibrationAllowed);

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("isVibrationAllowed", true);
                }
                else {
                    editor.putBoolean("isVibrationAllowed", false);
                }
                editor.commit();
            }
        });

        // Back
        ImageView backBtn = view.findViewById(R.id.appSettings_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // App info
        ConstraintLayout appInfoBtn = view.findViewById(R.id.appInfo_layout);
        appInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_settings, new ApplicationInformationFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}