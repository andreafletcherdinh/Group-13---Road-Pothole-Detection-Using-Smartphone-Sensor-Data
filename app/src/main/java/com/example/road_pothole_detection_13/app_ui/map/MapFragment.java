package com.example.road_pothole_detection_13.app_ui.map;

import com.example.road_pothole_detection_13.app_ui.map.*;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getCodeCacheDir;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;

import android.Manifest;
import android.adservices.adselection.AdSelectionOutcome;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent
;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import com.example.road_pothole_detection_13.R;
import com.example.road_pothole_detection_13.databinding.FragmentMapBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
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
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MapFragment extends Fragment implements SensorEventListener {

    private static final String MBTILES_NAME = "maptest.mbtiles";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private MapboxMap map;
    private LatLngBounds bounds;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private SwitchCompat zoomSwitch;
    private Marker searchmarker;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];
    private static final float LIGHT_SHAKE_THRESHOLD = 5.0f;
    private static final float MEDIUM_SHAKE_THRESHOLD = 8.0f;
    private static final float SEVERE_SHAKE_THRESHOLD = 12.0f; // Set your threshold for shake detection
    private static final float ALPHA = 0.8f;
    private static final float NOISE_THRESHOLD = 0.5f;
    private long lastShakeTime = 0;
    private static final int SHAKE_COOLDOWN_MS = 1000;
    private LocationManager locationManager;
    private Location lastKnownLocation;
    private LatLng currentLocation = null;
    private final Handler handler = new Handler();
    private Runnable locationUpdater;
    private final List<LatLng> routePoints = new ArrayList<>();
    private final List<Polyline> polylines = new ArrayList<>();
    private final List<LatLng> potholePoints = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private static final int REQUEST_CODE = 1;
    private RelativeLayout layoutButtonGroup;
    private boolean isLayoutVisible = true;
    private final List<com.example.road_pothole_detection_13.app_ui.map.LocationPlace> suggestions = new ArrayList<>();
    private  Button directionButton;
    private Button navigationButton;
    private ImageButton exitNavigationButton;

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
                // Kiểm tra quyền truy cập vị trí
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Đăng ký lắng nghe vị trí thay đổi
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            500, // Khoảng thời gian cập nhật (1 giây)
                            0,    // Khoảng cách tối thiểu để cập nhật (1 mét)
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    // Cập nhật vị trí hiện tại theo thời gian thực
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    // In ra log hoặc cập nhật UI
                                    Log.e("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {}

                                @Override
                                public void onProviderEnabled(@NonNull String provider) {}

                                @Override
                                public void onProviderDisabled(@NonNull String provider) {}
                            }
                    );

                    // Lấy vị trí cuối cùng (nếu có)
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        Log.d("LastKnownLocation", "Lat: " + lastKnownLocation.getLatitude() +
                                ", Lng: " + lastKnownLocation.getLongitude());
                    }
                } else {
                    // Yêu cầu quyền nếu chưa được cấp
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(requireContext(), "GPS chưa được bật, vui lòng bật GPS", Toast.LENGTH_SHORT).show();
        }
}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Button btncurrentlocation = rootView.findViewById(R.id.btn_current_location);
        mapView = rootView.findViewById(R.id.mapView);
        SearchView searchView = rootView.findViewById(R.id.search_view);
        Button reportButton = rootView.findViewById(R.id.btn_report_pothole);
        layoutButtonGroup = rootView.findViewById(R.id.layout_button_group);
        directionButton = rootView.findViewById(R.id.btn_direction);
        navigationButton = rootView.findViewById(R.id.btn_navigation);
        exitNavigationButton = rootView.findViewById(R.id.btn_exitNavigation);
        mapView.onCreate(savedInstanceState);
        Button zoomInButton = rootView.findViewById(R.id.btn_zoom_in);
        Button zoomOutButton = rootView.findViewById(R.id.btn_zoom_out);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchAdapter(suggestions, location -> {
            // Handle item click: Move camera to selected location
            addPointAtLocation(location.getLatitude(),location.getLongitude());
            moveToLocation(location.getLatitude(), location.getLongitude());
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView after selection
            hideKeyboard();
        });
        recyclerView.setAdapter(adapter);


        mapView.getMapAsync(mapboxMap -> {
            map = mapboxMap;

            // Existing code to display mbtiles map
            File mbtilesFile = getFileFromAssets(requireContext(), MBTILES_NAME);
            showMbTilesMap(mbtilesFile);

            map.addOnMapClickListener(point -> {
                double latitude = point.getLatitude();
                double longitude = point.getLongitude();
                removeAllPolylines();
                // Tạo luồng mới
                addPointAtLocation(latitude, longitude);
                return true;
            });

        });
        reportButton.setOnClickListener(v -> {

            Intent intent = new Intent(requireContext(), PotholeDetailActivity.class);
            intent.putExtra("latitude", lastKnownLocation.getLatitude());
            intent.putExtra("longitude", lastKnownLocation.getLongitude());
            startActivityForResult(intent, REQUEST_CODE);

        });
        searchView.setOnSearchClickListener(v -> {
            searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(rootView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchplace(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    searchplace(newText);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
                return true;
            }
        });

        btncurrentlocation.setOnClickListener(v -> {

            if (map != null) {
                LatLng newLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 16.0f));
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
                if (currentZoom < 17) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(currentZoom + 1));
                }
            } else {
                Log.e("MapFragment", "MapboxMap is not initialized.");
            }
        });
        zoomOutButton.setOnClickListener(v -> {
            if (map != null) {
                double currentZoom = map.getCameraPosition().zoom;
                if (currentZoom > 12) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(currentZoom - 1));
                }
            } else {
                Log.e("MapFragment", "MapboxMap is not initialized.");
            }
        });


        return rootView;
    }
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
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void drawPolyline(List<LatLng> route) {
        // Ví dụ tọa độ cho polyline
             // Thêm Polyline lên MapLibre
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(route) // Thêm tất cả các điểm vào polyline
                .color(Color.parseColor("#b642f5"))   // Đặt màu cho polyline
                .width(6);           // Đặt độ dày cho polyline

        Polyline polyline = map.addPolyline(polylineOptions);
        polylines.add(polyline);
    }
    private void removeAllPolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear(); // Dọn sạch danh sách
    }
    private void showMbTilesMap(File mbtilesFile) {
        try {
            InputStream styleJsonInputStream = requireContext().getAssets().open("bright.json");
            File dir = new File(requireContext().getFilesDir().getAbsolutePath());
            File styleFile = new File(dir, "bright.json");
            copyStreamToFile(styleJsonInputStream, styleFile);
            requestLocationPermission();
            bounds = getLatLngBounds(mbtilesFile);

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
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                            .zoom(17) // Giữ mức zoom hiện tại
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                    addMarkerAtLocation(targetLocation.getLatitude() ,targetLocation.getLongitude());
                    startLocationUpdates();
                    initcurrentLocationLayer(style);
                    initPotholeLayer(style);
                    initSinglePointLayer(style);
                }
                else {
                    Log.e("MapFragment", "lastKnownLocation is null.");
                }

            });

         getdata();
        } catch (IOException e) {
            Log.e("MapFragment", "Error loading mbtiles", e);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String severity = data.getStringExtra("severity");
            addPotholeToGeoJson(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(),severity);
            postLocation(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(), severity);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Loại bỏ nhiễu
            if (Math.abs(x) < NOISE_THRESHOLD) x = 0;
            if (Math.abs(y) < NOISE_THRESHOLD) y = 0;
            if (Math.abs(z) < NOISE_THRESHOLD) z = 0;

            // Tách lực trọng trường và gia tốc tuyến tính
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

            if (acceleration > LIGHT_SHAKE_THRESHOLD) {
                String severity = "Light"; // Mặc định là mức nhẹ
                if (acceleration > SEVERE_SHAKE_THRESHOLD) {
                    severity = "Severe"; // Mức nghiêm trọng
                } else if (acceleration > MEDIUM_SHAKE_THRESHOLD) {
                    severity = "Moderate"; // Mức vừa
                }
                if (isNavigationModeEnabled) {
                    handleShakeEvent(severity);
                }
            }
        }
    }
    private void handleShakeEvent(String severity) {
        // Rung thiết bị nếu được thiết lập
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        Boolean isVibrationAllowed = sharedPreferences.getBoolean("isVibrationAllowed", true);
        if (isVibrationAllowed) {
            // Lấy Vibrator từ hệ thống
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

            // Kiểm tra nếu thiết bị hỗ trợ rung
            if (vibrator != null) {
                vibrator.vibrate(500); // Rung trong 500ms (0.5 giây)
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = currentTime;
        if (lastKnownLocation != null) {
            // Hiển thị thông báo xác nhận
            new AlertDialog.Builder(getContext())
                    .setTitle("Phát hiện ổ gà")
                    .setMessage("Bạn có muốn lưu vị trí ổ gà này với mức độ: " + severity + "?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Lưu vị trí và thêm marker
                        addPotholeToGeoJson(
                                lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude(),
                                severity // Thêm mức độ vào GeoJson
                        );
                        postLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), severity);
                        Log.i("MapFragment", "Vị trí ổ gà (" + severity + ") đã được lưu.");
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
    private void startLocationUpdates() {
        locationUpdater = new Runnable() {
            @Override
            public void run() {
                if (map != null && lastKnownLocation != null  ) {
                    // Define the default location (replace with your desired coordinates)

                    LatLng defaultLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()); // Example location
                    addMarkerAtLocation(defaultLocation.getLatitude(), defaultLocation.getLongitude());
                    if (isNavigationModeEnabled) {
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                                .zoom(18) // Giữ mức zoom hiện tại
                                .build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                    handler.postDelayed(this, 500);
                } else {
                    Log.e("MapFragment", "MapboxMap is not initialized.");
                }
            }
        };
        // Bắt đầu chu kỳ đầu tiên
        handler.post(locationUpdater);
    }


    private final List<Feature> potholeFeatures = new ArrayList<>();
    private void initPotholeLayer(@NonNull Style style) {
        if (map != null) {
            // Tạo GeoJsonSource
            GeoJsonSource potholeSource = new GeoJsonSource("pothole-source", FeatureCollection.fromFeatures(new ArrayList<>()));
            style.addSource(potholeSource);

            // Tạo SymbolLayer hiển thị các điểm pothole
            SymbolLayer potholeLayer = new SymbolLayer("pothole-layer", "pothole-source");
            potholeLayer.setProperties(
                    PropertyFactory.iconImage("pothole-icon"), // Icon của pothole
                    PropertyFactory.iconSize(0.05f),
                    PropertyFactory.iconIgnorePlacement(true),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconOpacity(1.0f),// Kích thước của icon
                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_CENTER)
            );
            style.addLayer(potholeLayer);

            // Đăng ký icon cho SymbolLayer
            style.addImage("pothole-icon", BitmapFactory.decodeResource(getResources(), R.drawable.pothole_icon));
        }
    }
    private void initSinglePointLayer(@NonNull Style style) {
        // Tạo GeoJsonSource ban đầu
        GeoJsonSource pointSource = new GeoJsonSource("single-point-source", Feature.fromGeometry(Point.fromLngLat(0, 0)));
        style.addSource(pointSource);

        // Tạo SymbolLayer để hiển thị hình ảnh
        SymbolLayer pointLayer = new SymbolLayer("single-point-layer", "single-point-source");
        pointLayer.setProperties(
                PropertyFactory.iconImage("custom-icon"),
                PropertyFactory.iconSize(0.05f), // Kích thước biểu tượng
                PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM)
        );
        style.addLayer(pointLayer);

        // Đăng ký hình ảnh biểu tượng
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pin); // Thay R.drawable.ic_pin bằng biểu tượng của bạn
        style.addImage("custom-icon", bitmap);
    }
    private void addPotholeToGeoJson(double lat, double lng, String severity) {
        map.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
            GeoJsonSource potholeSource = style.getSourceAs("pothole-source");
            if (potholeSource != null) {
                // Lấy danh sách hiện tại

                // Thêm điểm mới
                Feature newFeature = Feature.fromGeometry(Point.fromLngLat(lng, lat));
                potholeFeatures.add(newFeature);

                // Cập nhật lại GeoJsonSource
                potholeSource.setGeoJson(FeatureCollection.fromFeatures(potholeFeatures));
            }
        }
        });
    }
    private void initcurrentLocationLayer (@NonNull Style style)
    {
        if (map != null)
        {
            GeoJsonSource pointSource = new GeoJsonSource("currentLocation-source", Feature.fromGeometry(Point.fromLngLat(0, 0)));
            style.addSource(pointSource);
            currentLocation = null;
            // Tạo SymbolLayer để hiển thị hình ảnh
            SymbolLayer pointLayer = new SymbolLayer("currentLocation-layer", "currentLocation-source");
            pointLayer.setProperties(
                    PropertyFactory.iconImage("currentLocation-icon"),
                    PropertyFactory.iconSize(0.08f), // Kích thước biểu tượng
                    PropertyFactory.iconIgnorePlacement(true),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconOpacity(1.0f),
                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM)
            );
            style.addLayer(pointLayer);

            // Đăng ký hình ảnh biểu tượng
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon); // Thay R.drawable.ic_pin bằng biểu tượng của bạn
            style.addImage("currentLocation-icon", bitmap);
        }
    }

    private void addMarkerAtLocation(double lat, double lng) {
        map.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Dữ liệu GeoJSON mới
                LatLng newLocation = new LatLng(lat, lng);
                if (currentLocation != null && currentLocation.getLatitude() == lat && currentLocation.getLongitude() == lng) {

                    return; // Không cập nhật nếu vị trí không thay đổi
                }
                currentLocation = newLocation;
                GeoJsonSource pointSource = style.getSourceAs("currentLocation-source");
                if (pointSource != null) {
                    // Cập nhật tọa độ của GeoJsonSource
                    pointSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(lng, lat)));
                } else {
                    Log.e("MapFragment", "GeoJsonSource not found!");
                }
                checkProximityToPothole(currentLocation, potholeOnRoute);
            }
            });
    }
    private Feature currentFeature;
    private boolean isNavigationModeEnabled = false;
    private void addPointAtLocation(double lat, double lng) {
        if (map != null) {
            map.getStyle(style -> {
                GeoJsonSource pointSource = style.getSourceAs("single-point-source");
                if (pointSource == null) {
                    currentFeature = Feature.fromGeometry(Point.fromLngLat(lng, lat));
                    pointSource = new GeoJsonSource("single-point-source", currentFeature);
                    style.addSource(pointSource);
                } else {
                    currentFeature = Feature.fromGeometry(Point.fromLngLat(lng, lat));
                    pointSource.setGeoJson(currentFeature);
                }

                if (style.getLayer("single-point-layer") == null) {
                    SymbolLayer pointLayer = new SymbolLayer("single-point-layer", "single-point-source");
                    pointLayer.setProperties(
                            PropertyFactory.iconImage("custom-icon"),
                            PropertyFactory.iconSize(0.05f),
                            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM)
                    );
                    style.addLayer(pointLayer);
                }
                if (isLayoutVisible) {
                    layoutButtonGroup.setVisibility(View.VISIBLE); // Hiện layout
                    isLayoutVisible = false;
                }
                 directionButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         getDirections(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lat, lng);
                         CameraPosition position = new CameraPosition.Builder()
                                 .target(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())) // Di chuyển đến tọa độ latLng
                                 .zoom(16)       // Đặt mức zoom
                                 .build();
                         map.getUiSettings().setAllGesturesEnabled(true);
                         map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                     }
                });
                navigationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isNavigationModeEnabled = true;
                        warnedPotholes.clear();
                        //startNavigationUpdates(latitude,longitude);
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())) // Di chuyển đến tọa độ latLng
                                .zoom(18)       // Đặt mức zoom
                                .build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                        // Nếu có pothole trên đoạn, thông báo hoặc thực hiện hành động khác
                        checkProximityToPothole(currentLocation,potholeOnRoute);
                    }
                });

                exitNavigationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isLayoutVisible) {
                            layoutButtonGroup.setVisibility(View.GONE); // Ẩn layout
                            isLayoutVisible = true;
                        }
                        isNavigationModeEnabled = false;
                        removeAllPolylines();
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())) // Di chuyển đến tọa độ latLng
                                .zoom(16)       // Đặt mức zoom
                                .build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                });
            });
        }
    }

    private void moveToLocation(double latitude, double longitude) {
        if (map != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(latitude,longitude)) // Di chuyển đến tọa độ latLng
                    .zoom(15)       // Đặt mức zoom
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            Log.e("MapFragment", "MapboxMap is not initialized");
            Toast.makeText(getContext(), "Map is not ready yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private final List<LatLng> potholeOnRoute = new ArrayList<>();
    private int countPotholesOnRoute(List<LatLng> latlonglist, List<LatLng> potholelist) {
        int count = 0;
        double threshold = 5.0;
        // Duyệt qua từng pothole
        for (LatLng pothole : potholelist) {
            // Duyệt qua từng đoạn trong tuyến đường
            for (int i = 0; i < latlonglist.size() - 1; i++) {
                LatLng start = latlonglist.get(i);
                LatLng end = latlonglist.get(i + 1);

                // Tính khoảng cách từ pothole đến đoạn thẳng
                double distance = calculateDistanceToSegment(pothole, start, end);

                // Kiểm tra nếu khoảng cách nhỏ hơn ngưỡng (threshold)
                if (distance <= threshold) {
                    count++;
                    potholeOnRoute.add(new LatLng(pothole.getLatitude(),pothole.getLongitude()));
                    Log.e("Map Fragment", "Khoảng cách từ pothole tới đoạn thẳng: " + distance + " mét");
                    Log.e("Map Fragment", "Pothole có gần đoạn thẳng không? Có");
                    break; // Đã tìm thấy, không cần kiểm tra các đoạn khác
                }
            }
        }

        return count;
    }
    private final Set<LatLng> warnedPotholes = new HashSet<>();

    private void checkProximityToPothole(LatLng currentLocation, List<LatLng> potholeOnRoute) {
        try {
            double proximityThreshold = 50.0; // Distance in meters
            if (potholeOnRoute != null && !potholeOnRoute.isEmpty()) {
                for (LatLng pothole : potholeOnRoute) {
                    double distance = calculateDistanceBetweenPoints(currentLocation, pothole);
                    if (distance <= proximityThreshold && !warnedPotholes.contains(pothole)) {
                        Log.e("Map Fragment", "Warning: Approaching pothole: " + pothole);
                        warnedPotholes.add(pothole); // Đánh dấu ổ gà đã được cảnh báo
                        showWarningDialog(pothole);
                        break;
                    }
                }
            } else {
            Log.e("ERROR", "Pothole List is null or empty");
        }
        } catch (Exception e) {
            Log.e("Map Error", "Error during proximity detection: " + e.getMessage(), e);
        }
    }
    private double calculateDistanceBetweenPoints(LatLng point1, LatLng point2) {
        final int EARTH_RADIUS = 6371000; // Bán kính Trái Đất tính bằng mét

        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Khoảng cách giữa 2 điểm (đơn vị: mét)
    }
    private void showWarningDialog(LatLng pothole) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_pothole_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Xử lý nút OK
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        // Hiển thị Dialog
        dialog.show();
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 5000);
    }

    private double calculateDistanceToSegment(LatLng point, LatLng start, LatLng end) {
        double A = point.getLatitude() - start.getLatitude();
        double B = point.getLongitude() - start.getLongitude();
        double C = end.getLatitude() - start.getLatitude();
        double D = end.getLongitude() - start.getLongitude();

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq != 0) ? dot / len_sq : -1;

        double xx, yy;
        if (param < 0) {
            xx = start.getLatitude();
            yy = start.getLongitude();
        } else if (param > 1) {
            xx = end.getLatitude();
            yy = end.getLongitude();
        } else {
            xx = start.getLatitude() + param * C;
            yy = start.getLongitude() + param * D;
        }

        double dx = point.getLatitude() - xx;
        double dy = point.getLongitude() - yy;

        // Hệ số 111320 dùng để chuyển đổi tọa độ địa lý sang mét
        return Math.sqrt(dx * dx + dy * dy) * 111320;
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
        public void onLocationChanged(Location location) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .zoom(18) // Giữ mức zoom hiện tại
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    private void getdata(){
        Thread thread = new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String url = "http://diddysfreakoffparty.online:3000/api/map/potholes";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    // Đọc JSON từ response
                    String responseData = response.body().string();

                    // Parse JSON
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    // Lặp qua các phần tử
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);

                        String latitude = item.getString("latitude");
                        String longitude = item.getString("longitude");
                        String severity = item.getString("severity");
                        Double.parseDouble(latitude);
                        Double.parseDouble(longitude);
                        potholePoints.add(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                        // Xử lý dữ liệu ở đây
                        getActivity().runOnUiThread(() -> {
                            addPotholeToGeoJson(Double.parseDouble(latitude), Double.parseDouble(longitude),severity);
                        });
                    }
                } else {
                    System.out.println("Request failed. Code: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    public void postLocation(double latitude, double longitude, String severity) {
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                // 1. Tạo JSON Object với dữ liệu cần gửi
                String postData = "{\n" +
                        "    \"latitude\": " + latitude + ",\n" +
                        "    \"longitude\": " + longitude + ",\n" +
                        "    \"severity\": \"" + severity + "\"\n" +
                        "}";

                // 2. Định nghĩa MediaType cho JSON
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                // 3. Tạo RequestBody từ JSON Object
                RequestBody body = RequestBody.create(postData, JSON);

                // 4. Tạo Request POST
                Request request = new Request.Builder()
                        .url("http://diddysfreakoffparty.online:3000/api/map/potholes")
                        .post(body)
                        .build();

                // 5. Thực hiện Request và nhận Response
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        // Xử lý thành công
                        Log.d("postLocation", "Post successful: " + response.body().string());

                    } else {
                        // Xử lý lỗi
                        Log.e("postLocation", "Post failed with code: " + response.code() + ", message: " + response.message());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void getDirections(double startingLat, double startingLng, double endLat, double endLng) {
        new Thread(() -> {
            try {
                // Tạo URL với query parameters
                String url = "http://diddysfreakoffparty.online:3000/api" + "/map/directions?startingPoint=" + startingLat + "," + startingLng + "&endPoint=" + endLat + "," + endLng;
                // Tạo request
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                // Thực thi request với OkHttp
                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();

                        // Parse JSON response
                        JSONObject responseJson = new JSONObject(responseData);
                        JSONObject dataObject = responseJson.getJSONObject("data");
                        JSONArray geometryArray = dataObject.getJSONArray("geometry");
                        routePoints.clear();
                            for (int i = 0; i < geometryArray.length(); i++) {
                                JSONArray latLngArray = geometryArray.getJSONArray(i);
                                double latitude = latLngArray.getDouble(0);
                                double longitude = latLngArray.getDouble(1);
                                routePoints.add(new LatLng(latitude, longitude));

                            }
                            getActivity().runOnUiThread(() -> {
                                drawPolyline(routePoints);
                                int count = countPotholesOnRoute(routePoints,potholePoints);
                                Log.e("Map Fragment","Số lượng ổ gà: " + count);
                            }); // Hàm vẽ tuyến đường trên bản đồ
                    } else {
                        System.err.println("Request failed: " + response.message());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void showSuggestions(List<LocationPlace> locations) {
        LocationAdapter adapter = new LocationAdapter(locations, location -> {
            // Di chuyển camera đến vị trí người dùng chọn
            moveToLocation(location.getLatitude(), location.getLongitude());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    private void searchplace(String keyword) {
        new Thread(() -> {
            try {
                String url = "http://diddysfreakoffparty.online:3000/api/locations/search?keyword=" + keyword;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).get().build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    List<LocationPlace> newSuggestions = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject locationObj = dataArray.getJSONObject(i);
                        String name = locationObj.getString("name");
                        String address = locationObj.getString("address");
                        double latitude = locationObj.getDouble("latitude");
                        double longitude = locationObj.getDouble("longitude");
                        newSuggestions.add(new LocationPlace(name, address, latitude, longitude));
                    }

                    getActivity().runOnUiThread(() -> {
                        suggestions.clear();
                        suggestions.addAll(newSuggestions);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void calculateAndDisplayRoute(double lat, double lng) {
        getDirections(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lat, lng);
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())) // Di chuyển đến tọa độ latLng
                            .zoom(16)       // Đặt mức zoom
                            .build();
                    map.getUiSettings().setAllGesturesEnabled(true);
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }
    private void startNavigationMode() {
        // Logic để kích hoạt chế độ dẫn đường
    }
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
        handler.removeCallbacks(locationUpdater);
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

