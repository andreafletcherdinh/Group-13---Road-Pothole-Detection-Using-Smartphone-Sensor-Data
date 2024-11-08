package com.example.road_pothole_detection_13.auth_ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.road_pothole_detection_13.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";

    // TODO: Rename and change types of parameters
    private String email;

    public VerificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerificationFragment newInstance(String param1, String param2) {
        VerificationFragment fragment = new VerificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView emailTextView = view.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        Button verifyButton = view.findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer code;
                EditText numberEditText = view.findViewById(R.id.editTextNumber);
                try {
                    code = Integer.parseInt(numberEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    showDialog("Alert", "Please fill out the code");
                    return;
                }
                verify(code);
            }
        });

        TextView counterTextView = view.findViewById(R.id.counterTextView);
        startCountdown(counterTextView);

        TextView resendTextView = view.findViewById(R.id.resendTextView);
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counterTextView.getText().toString().trim().equals("now")) {
                    sendCode(view, email);
                    startCountdown(counterTextView);
                }
                else {
                    showDialog("Alert", "Please wait for another request");
                }
            }
        });

        ImageView turnback = view.findViewById(R.id.turnback2);
        turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
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

    ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
    Bundle args = new Bundle();

    private void verify(Integer code) {
        new verifyTask(code).execute(getString(R.string.host) + "/auth/verify-pin");
    }

    private class verifyTask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private Integer code;

        // Constructor để truyền các giá trị động vào phần body
        public verifyTask(Integer code) {
            this.code = code;
            showLoadingDialog();
        }

        @Override
        protected Pair<Integer, String> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String urlString = params[0];
            String postData = "{\n" +
                    "    \"pin\": \"" + code + "\"\n," +
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

                // Lấy Reset Pasword Token
                String resetPasswordToken;
                try {
                    JSONObject jsonObject = new JSONObject(result.second);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    resetPasswordToken = dataObject.getString("resetPasswordToken");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Thêm resetPasswordToken làm args
                args.putString("resetPasswordToken", resetPasswordToken);
                resetPasswordFragment.setArguments(args);

                // Chuyển đến Verification Fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_auth, resetPasswordFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (result.first == 403) {
                showDialog("Alert", "The OTP code is invalid.");
            } else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }
    }

    public void startCountdown(final TextView counterTextView) {
        // Thời gian bắt đầu là 60 giây và đếm ngược mỗi 1 giây
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật TextView với số giây còn lại
                counterTextView.setText("in " + String.valueOf(millisUntilFinished / 1000) + " seconds");
            }

            @Override
            public void onFinish() {
                // Khi hoàn thành, đặt TextView về 0
                counterTextView.setText("now");
            }
        }.start();
    }

    private void sendCode(View view, String email) {
        if (TextUtils.isEmpty(email)) {
            showDialog("Alert", "Email field must not be empty");
            return;
        }
        new sendCodetask(email).execute(getString(R.string.host) + "/auth/request-password-reset");
    }

    private class sendCodetask extends AsyncTask<String, Void, Pair<Integer, String>> {
        private String email;

        // Constructor để truyền các giá trị động vào phần body
        public sendCodetask(String email) {
            this.email = email;
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
                showDialog("Notify", "A new OTP has been sent!");
            } else if (result.first == 404) {
                showDialog("Alert", "This email is not registered.");
            } else {
                showDialog("Alert", "Something went wrong, please try again later.");
            }
        }

    }
}