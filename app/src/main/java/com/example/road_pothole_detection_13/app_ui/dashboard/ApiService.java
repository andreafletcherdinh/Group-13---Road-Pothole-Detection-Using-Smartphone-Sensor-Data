package com.example.road_pothole_detection_13.app_ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("http://diddysfreakoffparty.online:3000/api/map/potholes")
    Call<PotholeResponse> getPotholes();
}