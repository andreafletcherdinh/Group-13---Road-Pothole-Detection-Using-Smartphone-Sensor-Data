package com.example.road_pothole_detection_13.auth_ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.road_pothole_detection_13.R;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "resetPasswordToken";

    // TODO: Rename and change types of parameters
    private String resetPasswordToken;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resetPasswordToken = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText passwordEditText = view.findViewById(R.id.editTextTextPassword3);
        EditText confirmedPasswordEditText = view.findViewById(R.id.editTextTextConfirmPassword2);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString().trim();
                String confirmedPassword = confirmedPasswordEditText.getText().toString().trim();

                confirm(view, password, confirmedPassword);
            }
        });

        ImageView turnback = view.findViewById(R.id.turnback3);
        turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xoá back stack
                getParentFragmentManager().popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);

                // Chuyển về Login Fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_auth, new LoginFragment());
                transaction.commit();
            }
        });

        // Check passwordEditText
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
                        passwordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        passwordEditText.setError("Password must not be empty");

                        Button confirmButton = view.findViewById(R.id.confirmButton);
                        confirmButton.setEnabled(false);
                    }
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    passwordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    passwordEditText.setError("Password must not be empty");

                    Button confirmButton = view.findViewById(R.id.confirmButton);
                    confirmButton.setEnabled(false);
                } else {
                    passwordEditText.setBackgroundResource(R.drawable.rounded_border);

                    Button confirmButton = view.findViewById(R.id.confirmButton);
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Check confirmedPasswordEditText
        confirmedPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(confirmedPasswordEditText.getText().toString().trim())) {
                        confirmedPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        confirmedPasswordEditText.setError("Password must not be empty");

                        Button confirmButton = view.findViewById(R.id.confirmButton);
                        confirmButton.setEnabled(false);
                    }
                }
            }
        });
        confirmedPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    confirmedPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmedPasswordEditText.setError("Confirmed password must not be empty");

                    Button confirmButton = view.findViewById(R.id.confirmButton);
                    confirmButton.setEnabled(false);
                } if (!s.toString().equals(passwordEditText.getText().toString())) {
                    confirmedPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmedPasswordEditText.setError("Confirmed password must be matched");

                    Button confirmButton = view.findViewById(R.id.confirmButton);
                    confirmButton.setEnabled(false);
                } else {
                    confirmedPasswordEditText.setBackgroundResource(R.drawable.rounded_border);

                    Button confirmButton = view.findViewById(R.id.confirmButton);
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private ProgressDialog progressDialog;
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void confirm(View view, String password, String confirmedPassword) {
        if (!password.equals(confirmedPassword)) {
            showDialog("Alert", "Confirmed password must be matched");
            return;
        }
        new confirmTask(password).execute(getString(R.string.host) + "/auth/reset-password?token=" + resetPasswordToken);
    }

    private class confirmTask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private String password;

        // Constructor để truyền các giá trị động vào phần body
        public confirmTask(String password) {
            this.password = password;
            showLoadingDialog();
        }

        @Override
        protected Pair<Integer, String> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String urlString = params[0];
            String postData = "{\n" +
                    "    \"newPassword\": \"" + password + "\"\n" +
                    "}";
            try {
                // Định nghĩa MediaType cho JSON
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                // Tạo RequestBody từ postData
                RequestBody body = RequestBody.create(postData, JSON);

                // Tạo Request cho yêu cầu POST
                Request request = new Request.Builder()
                        .url(urlString)
                        .put(body)
                        .build();

                // Thực hiện yêu cầu và lấy phản hồi
                try (Response response = client.newCall(request).execute()) {
                    int responseCode = response.code();
                    String responseBody = response.body() != null ? response.body().string() : "";
                    return new Pair<>(responseCode, responseBody);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Pair<>(-1, "Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(Pair<Integer, String> result) {
            // Đóng dialog loading
            dismissLoadingDialog();

            // Hiển thị dialog kết quả
            if (result.first == 200) {
                showDialog("Success", "Reset password successfully. Please login.");

                // Xoá back stack
                getParentFragmentManager().popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);

                // Chuyển về Login Fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_auth, new LoginFragment());
                transaction.commit();
            } else if (result.first == 400){
                showDialog("Alert", "New password must be longer than or equal to 6 characters");
            } else if (result.first == 403){
                showDialog("Alert", "This request has been expired, please try again later");
            }
            else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }
    }
}