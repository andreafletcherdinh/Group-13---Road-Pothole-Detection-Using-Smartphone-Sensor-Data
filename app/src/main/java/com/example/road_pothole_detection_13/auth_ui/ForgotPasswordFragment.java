package com.example.road_pothole_detection_13.auth_ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
 * Use the {@link ForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment newInstance(String param1, String param2) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
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
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    VerificationFragment verificationFragment = new VerificationFragment();
    Bundle args = new Bundle();
    String sentEmail;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailEditText = view.findViewById(R.id.editTextTextEmailAddress3);
        Button sendCodeButton = view.findViewById(R.id.sendCodeButton);
        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                sendCode(view, email);
            }
        });

        ImageView turnback = view.findViewById(R.id.turnback);
        turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Check emailEditText
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
                        emailEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        emailEditText.setError("Email address must not be empty");
                    }
                }
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    emailEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    emailEditText.setError("Email address must not be empty");
                } if (!isValidEmail(s.toString())) {
                    emailEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    emailEditText.setError("Email address is invalid");
                } else {
                    emailEditText.setBackgroundResource(R.drawable.rounded_border);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(emailPattern);
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

    private void sendCode(View view, String email) {
        if (TextUtils.isEmpty(email)) {
            showDialog("Alert", "Email field must not be empty");
            return;
        }
        if (!isValidEmail(email)) {
            showDialog("Alert", "The email address must be valid");
            return;
        }
        new sendCodetask(email).execute(getString(R.string.host) + "/auth/request-password-reset");
    }

    private class sendCodetask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private String email;

        // Constructor để truyền các giá trị động vào phần body
        public sendCodetask(String email) {
            this.email = email;
            sentEmail = email;
            showLoadingDialog();
        }

        @Override
        protected Pair<Integer, String> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String urlString = params[0];
            String postData = "{\n" +
                    "    \"email\": \"" + email + "\"\n" +
                    "}";
            try {
                // Định nghĩa MediaType cho JSON
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                // Tạo RequestBody từ postData
                RequestBody body = RequestBody.create(postData, JSON);

                // Tạo Request cho yêu cầu POST
                Request request = new Request.Builder()
                        .url(urlString)
                        .post(body)
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

                // Thêm email làm args
                args.putString("email", sentEmail);
                verificationFragment.setArguments(args);

                // Chuyển đến Verification Fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_auth, verificationFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (result.first == 404){
                showDialog("Alert", "This email is not registered.");
            }
            else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }
    }
}