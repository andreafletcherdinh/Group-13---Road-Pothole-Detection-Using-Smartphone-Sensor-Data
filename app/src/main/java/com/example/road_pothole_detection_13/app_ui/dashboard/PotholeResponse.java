package com.example.road_pothole_detection_13.app_ui.dashboard;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PotholeResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Pothole> data;

    public List<Pothole> getData() {
        return data;
    }
}
