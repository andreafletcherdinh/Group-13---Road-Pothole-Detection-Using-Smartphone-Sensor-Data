package com.example.road_pothole_detection_13.app_ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("map/potholes")
    Call<PotholeResponse> getPotholes();
}