package com.example.road_pothole_detection_13.app_ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.road_pothole_detection_13.NetworkUtils;
import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordSettingFragment newInstance(String param1, String param2) {
        PasswordSettingFragment fragment = new PasswordSettingFragment();
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
        return inflater.inflate(R.layout.fragment_password_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back
        ImageView backBtn = view.findViewById(R.id.password_setting_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Current password editText
        EditText currentPasswordEditText = view.findViewById(R.id.passwordSetting_currentPasswordEditText);
        currentPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (!hasFocus) {
                    if (TextUtils.isEmpty(currentPasswordEditText.getText().toString())) {
                        currentPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        currentPasswordEditText.setError("Password must not be empty");

                        confirmBtn.setEnabled(false);
                    }
                }
            }
        });
        currentPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (TextUtils.isEmpty(s)) {
                    currentPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    currentPasswordEditText.setError("Password must not be empty");

                    confirmBtn.setEnabled(false);
                } else {
                    currentPasswordEditText.setBackgroundResource(R.drawable.rounded_border);

                    confirmBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // New password editText
        EditText newPasswordEditText = view.findViewById(R.id.passwordSetting_newPasswordEditText);
        newPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (!hasFocus) {
                    if (TextUtils.isEmpty(newPasswordEditText.getText().toString())) {
                        newPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        newPasswordEditText.setError("Password must not be empty");

                        confirmBtn.setEnabled(false);
                    }
                }
            }
        });
        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (TextUtils.isEmpty(s)) {
                    newPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    newPasswordEditText.setError("Password must not be empty");

                    confirmBtn.setEnabled(false);
                } else {
                    newPasswordEditText.setBackgroundResource(R.drawable.rounded_border);

                    confirmBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Confirm password editText
        EditText confirmPasswordEditText = view.findViewById(R.id.passwordSetting_confirmPasswordEditText);
        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (!hasFocus) {
                    if (TextUtils.isEmpty(confirmPasswordEditText.getText().toString().trim())) {
                        confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        confirmPasswordEditText.setError("Password must not be empty");

                        confirmBtn.setEnabled(false);
                    }
                }
            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
                if (TextUtils.isEmpty(s)) {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmPasswordEditText.setError("Confirmed password must not be empty");

                    confirmBtn.setEnabled(false);
                } if (!s.toString().equals(newPasswordEditText.getText().toString())) {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmPasswordEditText.setError("Confirmed password must be matched");

                    confirmBtn.setEnabled(false);
                } else {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.rounded_border);

                    confirmBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Confirm
        Button confirmBtn = view.findViewById(R.id.password_setting_confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    void changePassword() {
        EditText currentPasswordEditText = getView().findViewById(R.id.passwordSetting_currentPasswordEditText);
        String currentPassword = currentPasswordEditText.getText().toString();
        EditText newPasswordEditText = getView().findViewById(R.id.passwordSetting_newPasswordEditText);
        String newPassword = newPasswordEditText.getText().toString();

        String url = "http://diddysfreakoffparty.online:3000/api/auth/change-password";
        String token = getActivity().getIntent().getStringExtra("accessToken");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("currentPassword", currentPassword);
            jsonObject.put("newPassword", newPassword);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonBody = jsonObject.toString();

        NetworkUtils.sendPutRequestWithAuthorization(getContext(), url, token, jsonBody, new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                showSuccessDialog("Success", "Please login again");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
                showErrorDialog("Failed to change password", "Error message: " + errorMessage);
            }
        });
    }

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();

                    // Delete token in cache
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("accessToken");
                    editor.commit();

                    // Turn off auto login
                    editor.remove("rememberMe");
                    editor.commit();

                    // Back to login screen
                    Intent intent = new Intent(requireContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}