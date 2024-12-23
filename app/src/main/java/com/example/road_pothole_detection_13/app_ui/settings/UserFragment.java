package com.example.road_pothole_detection_13.app_ui.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.road_pothole_detection_13.NetworkUtils;
import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.auth_ui.AuthActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView previewImageView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewImageView = view.findViewById(R.id.userAvatar_ImageView);

        // Khởi tạo ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        uploadImage();
                    }
                }
        );

        // Change image
        ImageView changeAvatarImageView = view.findViewById(R.id.user_changeAvatarImageView);
        changeAvatarImageView.setOnClickListener(v -> checkPermissionAndPickImage());

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
                builder.setMessage("Do you want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete token in cache
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("accessToken").commit();
                        editor.remove("rememberMe").commit();

                        // Back to login screen
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        // Lấy FragmentManager
        //FragmentManager fragmentManager = getParentFragmentManager();

        // Tạo lại Fragment
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.detach(this).attach(this).commit();

        getUserData();
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
        String photoUrl = "http://diddysfreakoffparty.online:3000" + photo;

        Picasso.get()
                .load(photoUrl)
                .into(previewImageView);
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

                    updateData();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showErrorDialog("Error", "Connection error. Please login again");
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
                            showSuccessDialog("Success", "Update avatar successfully");
                            Picasso.get()
                                    .load(selectedImageUri)
                                    .into(previewImageView);

                            // Cập nhật URL ảnh
                            try {
                                JSONObject bodyObject = new JSONObject(response);
                                JSONObject dataObject = bodyObject.getJSONObject("data");
                                String newPhotoURL = dataObject.getString("photo");
                                // Cập nhật photo URL
                                Intent intent = getActivity().getIntent();
                                intent.putExtra("photo", newPhotoURL);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            showErrorDialog("Error", "Error message: " + errorMessage);
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

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
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