package com.example.road_pothole_detection_13;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.*;

public class NetworkUtils {
    public interface ResponseCallback {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }

    private static void sendRequest(Context context, String url, String authToken, String method, String jsonBody, ResponseCallback callback) {
        // Hiển thị loading dialog trên main thread
        ProgressDialog loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // Khởi tạo OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // Tạo request body nếu cần
        RequestBody requestBody = null;
        if (jsonBody != null) {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            requestBody = RequestBody.create(jsonBody, JSON);
        }

        // Xây dựng request
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
    }

    public static void sendGetRequestWithAuthorization(Context context, String url, String authToken, ResponseCallback callback) {
        sendRequest(context, url, authToken, "GET", null, callback);
    }

    public static void sendPatchRequestWithAuthorization(Context context, String url, String authToken, String jsonBody, ResponseCallback callback) {
        sendRequest(context, url, authToken, "PATCH", jsonBody, callback);
    }

    public static void sendPutRequestWithAuthorization(Context context, String url, String authToken, String jsonBody, ResponseCallback callback) {
        sendRequest(context, url, authToken, "PUT", jsonBody, callback);
    }
}