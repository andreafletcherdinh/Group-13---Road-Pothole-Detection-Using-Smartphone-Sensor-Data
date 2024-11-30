package com.example.road_pothole_detection_13.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent
;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.graphics.Color;
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
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

//import com.mapbox.mapboxsdk.plugins.annotation.Line;
//import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
//import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements SensorEventListener {

    private static final String MBTILES_NAME = "maptest.mbtiles";
    private MapView mapView;
//    private LineManager lineManager;
    private MapboxMap mapboxMap;
    private MapboxMap map;
    private LatLngBounds bounds;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private double minZoomLevel;
    private SwitchCompat zoomSwitch;
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do your location logic here

            } else {
                // Permission denied, show a message
                Toast.makeText(getContext(), "Location permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private static final float SHAKE_THRESHOLD = 4.0f; // Set your threshold for shake detection
    private static final float ALPHA = 0.8f;
    private static final float NOISE_THRESHOLD = 0.5f;
    private long lastShakeTime = 0;
    private static final int SHAKE_COOLDOWN_MS = 1000;
    private LocationManager locationManager;
    private Location lastKnownLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize SensorManager
        Mapbox.getInstance(requireContext());
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
                map = mapbox;
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

    private void drawPolyline() {
        // Ví dụ tọa độ cho polyline
        List<LatLng> routePoints = new ArrayList<>();
        routePoints.add(new LatLng(10.870360, 106.802575)); // Điểm A
        routePoints.add(new LatLng(10.870460, 106.806575)); // Điểm B
        routePoints.add(new LatLng(10.870360, 106.803575)); // Điểm C

        // Thêm Polyline lên MapLibre
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints) // Thêm tất cả các điểm vào polyline
                .color(Color.BLUE)   // Đặt màu cho polyline
                .width(5);           // Đặt độ dày cho polyline

        map.addPolyline(polylineOptions); // Vẽ polyline trên bản đồ
    }
    private void showMbTilesMap(File mbtilesFile) {
        try {
            InputStream styleJsonInputStream = requireContext().getAssets().open("bright.json");
            File dir = new File(requireContext().getFilesDir().getAbsolutePath());
            File styleFile = new File(dir, "bright.json");
            copyStreamToFile(styleJsonInputStream, styleFile);
            requestLocationPermission();
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
                LatLng targetLocation = new LatLng(35.12972888243833, 137.30017964781746);
                if (lastKnownLocation != null) {
                    targetLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()); // Replace with your coordinates
                    float zoomLevel = 15.0f; // Adjust the zoom level
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, zoomLevel));
                    addMarkerAtLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), R.drawable.marker_icon);
                }
                else {
                    Log.e("MapFragment", "lastKnownLocation is null.");
                }
                drawPolyline();

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
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (Math.abs(x) < NOISE_THRESHOLD) x = 0;
            if (Math.abs(y) < NOISE_THRESHOLD) y = 0;
            if (Math.abs(z) < NOISE_THRESHOLD) z = 0;

            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

            linear_acceleration[0] = x - gravity[0];
            linear_acceleration[1] = y - gravity[1];
            linear_acceleration[2] = z - gravity[2];

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

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = currentTime;

            if (lastKnownLocation != null) {
                // Hiển thị thông báo xác nhận
                new AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có muốn lưu vị trí ổ gà này không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Lưu vị trí và thêm marker
                            addMarkerAtLocation(
                                    lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude(),
                                    R.drawable.pothole_icon
                            );
                            Log.i("MapFragment", "Vị trí ổ gà đã được lưu.");
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            // Bỏ qua sự kiện
                            Log.i("MapFragment", "Người dùng đã bỏ qua việc lưu vị trí ổ gà.");
                        })
                        .setCancelable(true)
                        .show();
            } else {
                Log.e("MapFragment", "Shake detected, but no GPS location available.");
            }
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
        double minZoom = 0.0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            cursor = db.query("metadata", new String[]{"value"}, "name=?", new String[]{"minzoom"}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                minZoom = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e("MapFragment", "Error reading min zoom level", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
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
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

