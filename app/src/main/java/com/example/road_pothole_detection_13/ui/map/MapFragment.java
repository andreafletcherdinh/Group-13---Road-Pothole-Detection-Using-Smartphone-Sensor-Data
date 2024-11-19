package com.example.road_pothole_detection_13.ui.map;

import android.content.Context;
import android.content.Intent
;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.road_pothole_detection_13.R;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapFragment extends Fragment implements SensorEventListener {

    private static final String MBTILES_NAME = "maptest.mbtiles";

    private MapView mapView;
    private MapboxMap map;
    private LatLngBounds bounds;
    private double minZoomLevel;
    private SwitchCompat zoomSwitch;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private static final float SHAKE_THRESHOLD = 2.5f; // Set your threshold for shake detection
    private static final float ALPHA = 0.8f;
    private LocationManager locationManager;
    private Location lastKnownLocation;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(requireContext());
        // Initialize SensorManager
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Initialize LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check if GPS provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                // Request location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

                // Get last known location
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Button btncurrentlocation = rootView.findViewById(R.id.btn_current_location);
        mapView = rootView.findViewById(R.id.mapView);
        zoomSwitch = rootView.findViewById(R.id.lockZoomSwitch);
        mapView.onCreate(savedInstanceState);
        Button zoomInButton = rootView.findViewById(R.id.btn_zoom_in);
        Button zoomOutButton = rootView.findViewById(R.id.btn_zoom_out);

        mapView.getMapAsync(mapboxMap -> {
            map = mapboxMap;

            // Existing code to display mbtiles map
            File mbtilesFile = getFileFromAssets(requireContext(), MBTILES_NAME);
            showMbTilesMap(mbtilesFile);


            zoomSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    map.setMinZoomPreference(minZoomLevel);
                } else {
                    map.setMinZoomPreference(0.0);
                }
            });
            map.setOnMarkerClickListener(marker -> {
                Intent intent = new Intent(getActivity(), PotholeDetailActivity.class);
                intent.putExtra("pothole_id", marker.getTitle()); // Pass marker title or other unique ID
                startActivity(intent);
                return false;
            });
        });
        btncurrentlocation.setOnClickListener(v -> {
            if (map != null) {
                // Define the default location (replace with your desired coordinates)
                LatLng defaultLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()); // Example location
                float defaultZoom = 15.0f; // Example zoom level

                // Move the map's camera to the default location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom));

                // Optionally, add a marker at the default location
                addMarkerAtLocation(defaultLocation.getLatitude(), defaultLocation.getLongitude(), R.drawable.marker_icon);
            } else {
                Log.e("MapFragment", "MapboxMap is not initialized.");
            }
        });
        // Set up accelerometer sensor manager
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapbox) {
                map = mapbox;  // Now we have access to the MapboxMap object
            }
        });
        zoomInButton.setOnClickListener(v -> {
            if (map != null) {
                double currentZoom = map.getCameraPosition().zoom;
                if (currentZoom < map.getMaxZoomLevel()) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(currentZoom + 1));
                }
            } else {
                Log.e("MapFragment", "MapboxMap is not initialized.");
            }
        });
        zoomOutButton.setOnClickListener(v -> {
            if (map != null) {
                double currentZoom = map.getCameraPosition().zoom;
                if (currentZoom > map.getMinZoomLevel()) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(currentZoom - 1));
                }
            } else {
                Log.e("MapFragment", "MapboxMap is not initialized.");
            }
        });

        return rootView;
    }

    private void showMbTilesMap(File mbtilesFile) {
        try {
            InputStream styleJsonInputStream = requireContext().getAssets().open("bright.json");
            File dir = new File(requireContext().getFilesDir().getAbsolutePath());
            File styleFile = new File(dir, "bright.json");
            copyStreamToFile(styleJsonInputStream, styleFile);

            bounds = getLatLngBounds(mbtilesFile);
            minZoomLevel = getMinZoom(mbtilesFile);

            // Replace placeholder with URI of the mbtiles file
            String newFileStr = readInputStreamToString(styleFile);
            newFileStr = newFileStr.replace("___FILE_URI___", "mbtiles:///" + mbtilesFile.getAbsolutePath());

            // Write new content to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(styleFile));
            writer.write(newFileStr);
            writer.close();

            // Set the map style using the edited JSON file
            map.setStyle(new Style.Builder().fromUri(Uri.fromFile(styleFile).toString()), style -> {
                // Define the initial location
                LatLng targetLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()); // Replace with your coordinates
                float zoomLevel = 15.0f; // Adjust the zoom level

                // Center the map on the initial location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, zoomLevel));

                // Add marker for the initial location
                addMarkerAtLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), R.drawable.marker_icon);
                addMarkerAtLocation(10.870360, 106.802575, R.drawable.pothole_icon);
                addMarkerAtLocation(10.870460, 106.806575, R.drawable.pothole_icon);
                addMarkerAtLocation(10.870360, 106.803575, R.drawable.pothole_icon);
                
            });
        } catch (IOException e) {
            Log.e("MapFragment", "Error loading mbtiles", e);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate gravity using a low-pass filter
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

            // Remove gravity to get linear acceleration
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float acceleration = (float) Math.sqrt(
                    Math.pow(linear_acceleration[0], 2) +
                            Math.pow(linear_acceleration[1], 2) +
                            Math.pow(linear_acceleration[2], 2)
            );

            if (acceleration > SHAKE_THRESHOLD) {
                handleShakeEvent();
            }
        }
    }
    private void handleShakeEvent() {
        if (lastKnownLocation != null) {
            addMarkerAtLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), R.drawable.pothole_icon);

        } else {
            Log.e("MapFragment", "Shake detected, but no GPS location available.");
        }
    }
    private void addMarkerAtLocation(double lat, double lng,int icon) {
        // Ensure that the map is initialized before adding a marker
        if (map != null) {
            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    // Create an Icon using IconFactory
                    IconFactory iconFactory = IconFactory.getInstance(getContext());
                    Icon originalIcon = iconFactory.fromResource(icon); // Use your custom marker drawable

                    // Scale the icon
                    Bitmap originalBitmap = originalIcon.getBitmap();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,
                            (int) (originalBitmap.getWidth() * 0.05),  // Scaling width by 0.05f
                            (int) (originalBitmap.getHeight() * 0.05), // Scaling height by 0.05f
                            false);
                    Icon scaledIcon = IconFactory.getInstance(getContext()).fromBitmap(scaledBitmap);

                    // Add the marker at the specified location
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .icon(scaledIcon)); // Set the scaled icon for the marker
                }
            });
        } else {
            // Handle the case where mapboxMap is not initialized yet
            Log.e("MapFragment", "MapboxMap is not initialized.");
        }
    }

    private File getFileFromAssets(Context context, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists()) {
            try (InputStream is = context.getAssets().open(fileName);
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private LatLngBounds getLatLngBounds(File file) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("metadata", new String[]{"name", "value"}, "name=?", new String[]{"bounds"}, null, null, null);
        cursor.moveToFirst();
        String[] boundsStr = cursor.getString(1).split(",");
        cursor.close();
        db.close();

        return new LatLngBounds.Builder()
                .include(new LatLng(Double.parseDouble(boundsStr[1]), Double.parseDouble(boundsStr[0])))
                .include(new LatLng(Double.parseDouble(boundsStr[3]), Double.parseDouble(boundsStr[2])))
                .build();
    }

    private double getMinZoom(File file) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("metadata", new String[]{"name", "value"}, "name=?", new String[]{"minzoom"}, null, null, null);
        cursor.moveToFirst();
        double minZoom = Double.parseDouble(cursor.getString(1));
        cursor.close();
        db.close();
        return minZoom;
    }

    private void copyStreamToFile(InputStream inputStream, File outputFile) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private String readInputStreamToString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            lastKnownLocation = location; // Update the last known location
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(@NonNull String provider) { }

        @Override
        public void onProviderDisabled(@NonNull String provider) { }
    };
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if necessary
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}

