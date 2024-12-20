package com.example.road_pothole_detection_13.app_ui.dashboard;

import com.google.gson.annotations.SerializedName;

public class Pothole {
    @SerializedName("_id")
    private String id;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("severity")
    private String severity;

    public String getCreatedAt() {
        return createdAt;
    }
}