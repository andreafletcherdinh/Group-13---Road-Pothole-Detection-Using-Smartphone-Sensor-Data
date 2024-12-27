// File: ApiService.java
/* TODO: Khởi tạo API Endpoints
 * Retrofit yêu cầu interface để generate HTTP client
 * 
*/


package com.example.road_pothole_detection_13.app_ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;

// Định nghĩa một API endpoint để lấy dữ liệu về các ổ gà thông qua một yêu cầu HTTP GET.
public interface ApiService {
    @GET("YOUR_API_ENDPOINT")
    Call<PotholeResponse> getPotholes();
}