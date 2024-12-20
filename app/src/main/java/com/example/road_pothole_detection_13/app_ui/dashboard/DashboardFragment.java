package com.example.road_pothole_detection_13.app_ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize BarChart
        barChart = rootView.findViewById(R.id.barChartWithData);
        configureBarChart(); // Set up BarChart properties
        setBarChartData();  // Add data to BarChart

        // Initialize PieChart
        pieChart = rootView.findViewById(R.id.pieChartWithData);
        configurePieChart(); // Set up PieChart properties
        setPieChartData();   // Add data to PieChart

        return rootView;
    }

    // Method to configure BarChart
    private void configureBarChart() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.WHITE);

        // Configure X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        final String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setTextSize(12f);
        xAxis.setLabelRotationAngle(-45);

        // Configure Y axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawZeroLine(true);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Configure legend
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
        legend.setYOffset(5f);
    }

    // Method to set BarChart data
    private void setBarChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0, 25f));
        values.add(new BarEntry(1, 30f));
        values.add(new BarEntry(2, 35f));
        values.add(new BarEntry(3, 40f));
        values.add(new BarEntry(4, 45f));
        values.add(new BarEntry(5, 20f));
        values.add(new BarEntry(6, 15f));

        BarDataSet set1 = new BarDataSet(values, "Last 7 days detected potholes");
        List<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        set1.setColors(colors);

        BarData data = new BarData(set1);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        barChart.invalidate();
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
}
