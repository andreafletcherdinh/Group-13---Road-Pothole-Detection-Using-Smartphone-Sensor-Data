package com.example.road_pothole_detection_13.app_ui.map;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.road_pothole_detection_13.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private final List<LocationPlace> locations;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(LocationPlace location);
    }

    public LocationAdapter(List<LocationPlace> locations, OnItemClickListener onItemClickListener) {
        this.locations = locations;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationPlace location = locations.get(position);
        holder.bind(location, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
        }

        public void bind(LocationPlace locationPlaces, OnItemClickListener listener) {
            nameTextView.setText(locationPlaces.getName());
            addressTextView.setText(locationPlaces.getAddress());
            itemView.setOnClickListener(v -> listener.onItemClick(locationPlaces));
        }
    }
}
