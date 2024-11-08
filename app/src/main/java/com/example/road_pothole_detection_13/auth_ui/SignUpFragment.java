package com.example.road_pothole_detection_13.auth_ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.road_pothole_detection_13.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SignUp button
        EditText nameEditText = view.findViewById(R.id.editTextName);
        EditText emailEditText = view.findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordEditText = view.findViewById(R.id.editTextTextPassword2);
        EditText confirmPasswordEditText = view.findViewById(R.id.editTextTextConfirmPassword);
        Button signUpButton = view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmedPassword = confirmPasswordEditText.getText().toString().trim();

                signUp(view, name, email, password, confirmedPassword);
            }
        });

        // Turnback button
        ImageView turnback = view.findViewById(R.id.turnback4);
        turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Check nameEditText
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(nameEditText.getText().toString().trim())) {
                        nameEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        nameEditText.setError("Name must not be empty");
                    }
                }
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    nameEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    nameEditText.setError("Name must not be empty");
                } else {
                    nameEditText.setBackgroundResource(R.drawable.rounded_border);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        // Check passwordEditText
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
                        passwordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        passwordEditText.setError("Password must not be empty");
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
                } else {
                    passwordEditText.setBackgroundResource(R.drawable.rounded_border);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Check confirmedPasswordEditText
        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(confirmPasswordEditText.getText().toString().trim())) {
                        confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                        confirmPasswordEditText.setError("Password must not be empty");
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
                if (TextUtils.isEmpty(s)) {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmPasswordEditText.setError("Confirmed password must not be empty");
                } if (!s.toString().equals(passwordEditText.getText().toString())) {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.red_rounded_border);
                    confirmPasswordEditText.setError("Confirmed password must be matched");
                } else {
                    confirmPasswordEditText.setBackgroundResource(R.drawable.rounded_border);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void signUp(View view, String name, String email, String password, String confirmedPassword) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showDialog("Alert", "Please fill out all fields!");
            return;
        }
        if (!isValidEmail(email)) {
            showDialog("Alert", "Email address must be valid");
            return;
        }
        if (!password.equals(confirmedPassword)) {
            showDialog("Alert", "Confirmed password must be matched!");
            return;
        }

        new signUpTask(name, email, password).execute(getString(R.string.host) + "/auth/signup");
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

    private class signUpTask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private String name;
        private String email;
        private String password;

        // Constructor để truyền các giá trị động vào phần body
        public signUpTask(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
            showLoadingDialog();
        }


        @Override
        protected Pair<Integer, String> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String urlString = params[0];
            String postData = "{\n" +
                    "    \"fullName\": \"" + name + "\",\n" +
                    "    \"email\": \"" + email + "\",\n" +
                    "    \"password\": \"" + password + "\"\n" +
                    "}";
            try {
                // Định nghĩa MediaType cho JSON
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                // Lấy device id của thiết bị
                String deviceID = getDeviceId(getContext());
                // Tạo RequestBody từ postData
                RequestBody body = RequestBody.create(postData, JSON);

                // Tạo Request cho yêu cầu POST
                Request request = new Request.Builder()
                        .url(urlString)
                        .addHeader("device-id", deviceID)
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
                showDialog("Success", "Sign up successfully. Please login");

                // Xoá back stack
                getParentFragmentManager().popBackStack(null, getParentFragmentManager().POP_BACK_STACK_INCLUSIVE);

                // Chuyển về Login Fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_auth, new LoginFragment());
                transaction.commit();
            } else if (result.first == 400) {
                showDialog("Alert", "Password must be longer than or equal to 6 characters");
            } else if (result.first == 409){
                showDialog("Alert", "This email has already taken, please use another email.");
            }
            else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}