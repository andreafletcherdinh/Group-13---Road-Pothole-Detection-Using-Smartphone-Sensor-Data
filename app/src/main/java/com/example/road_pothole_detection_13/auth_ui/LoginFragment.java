package com.example.road_pothole_detection_13.auth_ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.road_pothole_detection_13.MainActivity;
import com.example.road_pothole_detection_13.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Forgot password fragment
        TextView forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_auth, new ForgotPasswordFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // SignUp fragment
        TextView signUpText = view.findViewById(R.id.signUpText);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_auth, new SignUpFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Login
        EditText emailEditText = view.findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.editTextTextPassword);
        Button logInButton = view.findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                logIn(view, email, password);
            }
        });

        // Email Text View check
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

        // Password Text View check
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
    }

    private void logIn(View view, String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showDialog("Alert", "Please fill out all fields!");
            return;
        }
        if (!isValidEmail(email)) {
            showDialog("Alert", "Email address must be valid");
            return;
        }

        new logInTask(email, password).execute(getString(R.string.host) + "/auth/login");
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

    private class logInTask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private final String email;
        private final String password;

        // Constructor để truyền các giá trị động vào phần body
        public logInTask(String email, String password) {
            this.email = email;
            this.password = password;
            showLoadingDialog();
        }


        @Override
        protected Pair<Integer, String> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String urlString = params[0];
            String postData = "{\n" +
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
                // Xoá back stack
                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Lấy ra AccessToken
                String accessToken;
                try {
                    JSONObject jsonObject = new JSONObject(result.second);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    accessToken = dataObject.getString("accessToken");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Chuyển sang MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("accessToken", accessToken);
                startActivity(intent);
                getActivity().finish();

            } else if (result.first == 401) {
                showDialog("Alert", "Your email or password are incorrect! Please try again");

            } else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}