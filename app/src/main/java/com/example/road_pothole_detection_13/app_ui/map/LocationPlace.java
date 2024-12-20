package com.example.road_pothole_detection_13.app_ui.map;

public class LocationPlace {
    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;

    public LocationPlace(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
