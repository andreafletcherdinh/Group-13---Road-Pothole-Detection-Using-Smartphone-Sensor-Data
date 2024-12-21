package com.example.road_pothole_detection_13.app_ui.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.road_pothole_detection_13.NetworkUtils;
import com.example.road_pothole_detection_13.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountSettingFragment newInstance(String param1, String param2) {
        AccountSettingFragment fragment = new AccountSettingFragment();
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
        return inflater.inflate(R.layout.fragment_account_setting, container, false);
    }

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView previewImageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update data
        updateData();

        // Change avatar
        previewImageView = view.findViewById(R.id.accountSetting_changeAvatarImageView);
        // Khởi tạo ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        previewImageView.setImageURI(selectedImageUri);
                        uploadImage();
                    }
                }
        );
        previewImageView.setOnClickListener(v -> checkPermissionAndPickImage());

        // Back
        ImageView backBtn = view.findViewById(R.id.accoutSetting_backView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Change password
        TextView pwdChange_textView = view.findViewById(R.id.setting_password_change_textView);
        pwdChange_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_settings, new PasswordSettingFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Confirm button
        Button confirmBtn = view.findViewById(R.id.account_setting_confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    void updateData() {
        getUserData();

        Intent intent = getActivity().getIntent();

        // Avatar
        String photo = intent.getStringExtra("photo");
        String photoUrl = "http://diddysfreakoffparty.online:3000" + photo;
        ImageView avatarImageView = getView().findViewById(R.id.accountSetting_avatarImageView);
        Picasso.get()
                .load(photoUrl)
                .into(avatarImageView);

        // Full name
        String fullName = intent.getStringExtra("fullName");
        TextView fullName_textView = getView().findViewById(R.id.accountSetting_fullNameEditText);
        fullName_textView.setText(fullName);

        // Gender
        final String[] gender = {intent.getStringExtra("gender")};
        Spinner genderSpinner = getView().findViewById(R.id.accountSetting_genderSpinner);
        ArrayList<String> genderList = new ArrayList<>();
        // Add items
        genderList.add("male");
        genderList.add("female");
        genderList.add("other");
        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, genderList);
        genderSpinner.setAdapter(adapter);
        int position = adapter.getPosition(gender[0]);
        genderSpinner.setSelection(position);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Day of birth
        String dayOfBirth = intent.getStringExtra("birthDay");
        TextView dayOfBirth_textView = getView().findViewById(R.id.accountSetting_dayOfBirthTextDate);
        if (dayOfBirth == null) dayOfBirth_textView.setText(dayOfBirth);

        Calendar calendar = Calendar.getInstance();

        dayOfBirth_textView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Hiển thị DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Cập nhật Calendar với ngày được chọn
                        calendar.set(selectedYear, selectedMonth, selectedDay);

                        // Định dạng ngày tháng và hiển thị lên EditText
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        dayOfBirth_textView.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        // Address
        String address = intent.getStringExtra("address");
        TextView address_textView = getView().findViewById(R.id.accountSetting_addressEditText);
        address_textView.setText(address);
    }

    void updateUserProfile() {
        TextView fullName_textView = getView().findViewById(R.id.accountSetting_fullNameEditText);
        String fullName_after = fullName_textView.getText().toString();
        Spinner genderSpinner = getView().findViewById(R.id.accountSetting_genderSpinner);
        String gender_after = genderSpinner.getSelectedItem().toString();
        TextView dayOfBirth_textView = getView().findViewById(R.id.accountSetting_dayOfBirthTextDate);
        String dayOfBirth_after = dayOfBirth_textView.getText().toString();
        TextView address_textView = getView().findViewById(R.id.accountSetting_addressEditText);
        String address_after = address_textView.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            if (!fullName_after.isEmpty()) jsonObject.put("fullName", fullName_after);
            if (!gender_after.isEmpty()) jsonObject.put("gender", gender_after);
            if (!dayOfBirth_after.isEmpty()) jsonObject.put("birthDay", dayOfBirth_after);
            if (!address_after.isEmpty()) jsonObject.put("address", address_after);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (jsonObject.length() == 0) {
            showSuccessDialog("Alert", "There are no changes to make");
            return;
        }

        String url = "http://diddysfreakoffparty.online:3000/api/user/profile";
        String token = getActivity().getIntent().getStringExtra("accessToken");
        String jsonBody = jsonObject.toString();

        NetworkUtils.sendPatchRequestWithAuthorization(getContext(), url, token, jsonBody, new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                showSuccessDialog("Success", "Update profile successfully");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
                showErrorDialog("Error", "Error message: " + errorMessage);
            }
        });
    }

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
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

    void getUserData() {
        String url = "http://diddysfreakoffparty.online:3000/api/user/profile";
        String token = getActivity().getIntent().getStringExtra("accessToken");
        NetworkUtils.sendGetRequestWithAuthorization(getContext(), url, token, new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    JSONObject dataJson = responseJson.getJSONObject("data");
                    String fullName = dataJson.getString("fullName");
                    String email = dataJson.getString("email");
                    String photo = dataJson.getString("photo");
                    String birthDay = dataJson.getString("birthDay");
                    String gender = dataJson.getString("gender");
                    String address = dataJson.getString("address");

                    Intent intent = getActivity().getIntent();
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("photo", photo);
                    intent.putExtra("birthDay", birthDay);
                    intent.putExtra("gender", gender);
                    intent.putExtra("address", address);
                    intent.putExtra("accessToken", token);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorDialog("Connection error", "Error message: " + errorMessage);
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage() {
        if (selectedImageUri == null) return;

        try {
            // Chuyển đổi Uri thành File
            File imageFile = createFileFromUri(selectedImageUri);

            // Thực hiện upload
            String token = getActivity().getIntent().getStringExtra("accessToken");
            NetworkUtils.sendPatchRequestWithFile(
                    getContext(),
                    "http://diddysfreakoffparty.online:3000/api/user/update-photo", // Thay thế bằng API endpoint thực
                    token,
                    imageFile,
                    new NetworkUtils.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            showRequestSuccessDialog("Success", "Update avatar successfully");
                            Picasso.get()
                                    .load(selectedImageUri)
                                    .into(previewImageView);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            showRequestErrorDialog("Error", "Error message: " + errorMessage);
                            updateData();
                        }
                    }
            );
        } catch (Exception e) {
            showErrorDialog("Error", "Error while processing file: " + e.getMessage());
        }
    }

    private File createFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".jpg", getContext().getCacheDir());

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Selecting images needs storage access permission",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRequestSuccessDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showRequestErrorDialog(String title, String message) {
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