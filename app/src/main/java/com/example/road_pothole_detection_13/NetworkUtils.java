package com.example.road_pothole_detection_13;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class NetworkUtils {
    public interface ResponseCallback {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }

    private static void sendRequest(Context context, String url, String authToken, String method,
                                    RequestBody requestBody, ResponseCallback callback) {
        ProgressDialog loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken);

        if (method.equalsIgnoreCase("PATCH")) {
            requestBuilder.patch(requestBody);
        } else if (method.equalsIgnoreCase("GET")) {
            requestBuilder.get();
        } else if (method.equalsIgnoreCase("PUT")) {
            requestBuilder.put(requestBody);
        }

        Request request = requestBuilder.build();

        try {
            // Thực hiện request bất đồng bộ
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Chuyển về main thread để update UI
                    new Handler(Looper.getMainLooper()).post(() -> {
                        loadingDialog.dismiss();
                        callback.onFailure("Request failed: " + e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseBody = response.body() != null ? response.body().string() : "";

                    // Chuyển về main thread để update UI
                    new Handler(Looper.getMainLooper()).post(() -> {
                        loadingDialog.dismiss();
                        if (response.isSuccessful()) {
                            callback.onSuccess(responseBody);
                        } else {
                            callback.onFailure("Request failed with code: " + response.code());
                        }
                    });
                }
            });
        } catch (Exception e) {
            // Đóng dialog và báo lỗi nếu có exception khi khởi tạo request
            new Handler(Looper.getMainLooper()).post(() -> {
                loadingDialog.dismiss();
                callback.onFailure("Exception occurred: " + e.getMessage());
            });
        }
    }

    public static void sendGetRequestWithAuthorization(Context context, String url,
                                                       String authToken, ResponseCallback callback) {
        sendRequest(context, url, authToken, "GET", null, callback);
    }

    public static void sendPatchRequestWithAuthorization(Context context, String url,
                                                         String authToken, String jsonBody,
                                                         ResponseCallback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        sendRequest(context, url, authToken, "PATCH", requestBody, callback);
    }

    public static void sendPatchRequestWithFile(Context context, String url, String authToken, File file, ResponseCallback callback) {
        // Kiểm tra file type
        String fileExtension = getFileExtension(file.getName()).toLowerCase();
        if (!isValidImageType(fileExtension)) {
            callback.onFailure("Invalid file type. Only jpg, jpeg, png, webp are allowed.");
            return;
        }

        try {
            // Xác định MIME type chính xác dựa trên file extension
            String mimeType = getMimeType(fileExtension);

            // Tạo RequestBody cho file với MIME type cụ thể
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(file, MediaType.parse(mimeType)))
                    .build();

            sendRequest(context, url, authToken, "PATCH", requestBody, callback);
        } catch (Exception e) {
            callback.onFailure("Error creating request: " + e.getMessage());
        }
    }

    public static void sendPutRequestWithAuthorization(Context context, String url,
                                                       String authToken, String jsonBody,
                                                       ResponseCallback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        sendRequest(context, url, authToken, "PUT", requestBody, callback);
    }

    private static String getFileExtension(String fileName) {
        int lastDotPosition = fileName.lastIndexOf('.');
        if (lastDotPosition > 0) {
            return fileName.substring(lastDotPosition + 1);
        }
        return "";
    }

    private static boolean isValidImageType(String extension) {
        return extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("png") ||
                extension.equals("webp");
    }

    private static String getMimeType(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            default:
                return "image/jpeg"; // fallback to jpeg
        }
    }
}