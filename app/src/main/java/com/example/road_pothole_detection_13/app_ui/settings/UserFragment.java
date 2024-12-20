package com.example.road_pothole_detection_13.app_ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update data
        updateData();

        // Account setting
        View account_layout = view.findViewById(R.id.account_layout);
        account_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSettingsHostActivity("account");
            }
        });

        // About us
        View about_us_layout = view.findViewById(R.id.about_us_layout);
        about_us_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSettingsHostActivity("about_us");
            }
        });

        // App settings
        View settings_layout = view.findViewById(R.id.setting_layout);
        settings_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSettingsHostActivity("app_settings");
            }
        });

        // Logout
        View logout_layout = view.findViewById(R.id.log_out_layout);
        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notice");
                builder.setMessage("Are you want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void OpenSettingsHostActivity(String fragmentName) {
        String fullName = getActivity().getIntent().getStringExtra("fullName");
        String email = getActivity().getIntent().getStringExtra("email");
        String photo = getActivity().getIntent().getStringExtra("photo");
        String birthDay = getActivity().getIntent().getStringExtra("birthDay");
        String gender = getActivity().getIntent().getStringExtra("gender");
        String address = getActivity().getIntent().getStringExtra("address");
        String token = getActivity().getIntent().getStringExtra("accessToken");

        Intent intent = new Intent(getActivity(), SettingsHostActivity.class);
        intent.putExtra("fragment", fragmentName);
        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("photo", photo);
        intent.putExtra("birthDay", birthDay);
        intent.putExtra("gender", gender);
        intent.putExtra("address", address);
        intent.putExtra("accessToken", token);
        startActivity(intent);
    }

    private void updateData() {
        Intent intent = getActivity().getIntent();

        // Name
        String fullName = intent.getStringExtra("fullName");
        TextView fullNameTextView = getView().findViewById(R.id.fullNameTextView);
        fullNameTextView.setText(fullName);

        // Avatar
        String photo = intent.getStringExtra("photo");


    }

}