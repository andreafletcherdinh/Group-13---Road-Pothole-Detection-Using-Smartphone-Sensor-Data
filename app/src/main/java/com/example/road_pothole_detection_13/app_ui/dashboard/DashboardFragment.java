package com.example.road_pothole_detection_13.app_ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.road_pothole_detection_13.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/*Retrofit là một HTTP client type-safe cho Android, Java và kotlin được phát triển bởi Square. Retrofit giúp dễ dàng kết nối đến một dịch vụ REST trên web bằng cách chyển đổi API thành Java Interface.*/
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardFragment extends Fragment {
    private BarChart barChart;
    private PieChart pieChart;
    private ApiService apiService; // Khởi tạo API service
    private View rootView;
    private Call<PotholeResponse> currentCall; // Quản lý API call

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Retrofit và ApiService trong onCreate
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://diddysfreakoffparty.online:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        /*// Initialize BarChart
        barChart = rootView.findViewById(R.id.barChartWithData);
        configureBarChart(); // Set up BarChart properties

        // Initialize PieChart
        pieChart = rootView.findViewById(R.id.pieChartWithData);
        configurePieChart(); // Set up PieChart properties
        setPieChartData();   // Add data to PieChart

        // Load data
        loadPotholeData();*/

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize charts sau khi view đã được tạo
        initializeCharts();

        // Add data to PieChart
        if (pieChart != null) {
            setPieChartData();
        }

        // Load data
        loadPotholeData();
    }

    private void initializeCharts() {
        if (getView() == null) return;

        // Initialize charts
        barChart = getView().findViewById(R.id.barChartWithData);
        pieChart = getView().findViewById(R.id.pieChartWithData);

        if (barChart != null) {
            configureBarChart();
        }

        if (pieChart != null) {
            configurePieChart();
        }
    }

    // Gọi API và lấy dữ liệu
    private void loadPotholeData() {
        if (!isAdded() || getContext() == null) return;

        // Cancel any existing call
        if (currentCall != null) {
            currentCall.cancel();
        }

        // Make new API call
        currentCall = apiService.getPotholes();
        currentCall.enqueue(new Callback<PotholeResponse>() {
            @Override
            public void onResponse(Call<PotholeResponse> call, Response<PotholeResponse> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Pothole> potholes = response.body().getData();
                    if (potholes != null) {
                        updateBarChartWithData(potholes);
                    }
                } else {
                    Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PotholeResponse> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }

    // Method to configure BarChart
    private void configureBarChart() {
        if (barChart == null) return;

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setScaleEnabled(false);

        // Configure X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setLabelRotationAngle(-45);

        // Configure Y axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawZeroLine(true);

        // Ensure Y-axis shows integer values only
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Convert float to integer
            }
        });
        leftAxis.setGranularity(1f); // Ensure step size of 1
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
    }

    // Method to configure PieChart
    private void configurePieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setBackgroundColor(Color.WHITE);


        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
    }

    // Method to set PieChart data
    private void setPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Light"));
        entries.add(new PieEntry(35f, "Moderate"));
        entries.add(new PieEntry(25f, "Heavy"));

        PieDataSet dataSet = new PieDataSet(entries, "Pothole Severity");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    // Xử lý dữ liệu và cập nhật biểu đồ
    private void updateBarChartWithData(List<Pothole> potholes) {
        if (barChart == null || !isAdded()) return;

        try {
            // Tạo SimpleDateFormat để parse ngày
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Tạo map để đếm số lượng ổ gà theo ngày
            Map<String, Integer> dailyCounts = new TreeMap<>();

            // Lấy ngày hiện tại
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -6); // Lùi về 6 ngày trước

            // Format để hiển thị ngày
            SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd", Locale.US);

            // Khởi tạo map với 7 ngày gần nhất
            for (int i = 0; i < 7; i++) {
                String dateKey = displayFormat.format(calendar.getTime());
                dailyCounts.put(dateKey, 0);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            // Đếm số lượng ổ gà cho mỗi ngày
            for (Pothole pothole : potholes) {
                try {
                    Date date = sdf.parse(pothole.getCreatedAt());
                    String dateKey = displayFormat.format(date);

                    if (dailyCounts.containsKey(dateKey)) {
                        dailyCounts.put(dateKey, dailyCounts.get(dateKey) + 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // Tạo entries cho BarChart
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            int index = 0;

            for (Map.Entry<String, Integer> entry : dailyCounts.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                labels.add(entry.getKey());
                index++;
            }

            // Cập nhật BarChart
            BarDataSet dataSet = new BarDataSet(entries, "Số lượng ổ gà mỗi ngày");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.9f);
            barData.setValueTextSize(10f);

            // Cập nhật labels cho trục X
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

            barChart.setData(barData);
            barChart.invalidate();

        } catch (Exception e) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error updating chart: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
