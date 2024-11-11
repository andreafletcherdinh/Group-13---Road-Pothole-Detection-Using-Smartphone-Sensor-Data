package com.example.road_pothole_detection_13.ui.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.road_pothole_detection_13.databinding.FragmentMapBinding;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.FileInputStream;

public class MapFragment extends Fragment {

    private static final LatLong BERLIN = new LatLong(52.5200, 13.4050);
    private static final LatLong ALABAMA = new LatLong(32.3182, 86.9023);

    private FragmentMapBinding b;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout
        b = FragmentMapBinding.inflate(inflater, container, false);

        // Initialize AndroidGraphicFactory
        AndroidGraphicFactory.createInstance(requireActivity().getApplication());

        // Register the result contract for opening the map file
        ActivityResultLauncher<Intent> contract = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                openMap(uri);
                            }
                        }
                    }
                });

        // Set up the button click listener
        b.openmap.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            contract.launch(intent);
        });

        // Return the root view of the fragment
        return b.getRoot();
    }

    // Function to open the map using the selected Uri
    public void openMap(Uri uri) {
        b.map.getMapScaleBar().setVisible(true);
        b.map.setBuiltInZoomControls(true);

        AndroidUtil.TileCache cache = AndroidUtil.createTileCache(
                requireContext(),
                "mycache",
                b.map.getModel().getDisplayModel().getTileSize(),
                1f,
                b.map.getModel().getFrameBufferModel().getOverdrawFactor()
        );

        try (FileInputStream stream = (FileInputStream) requireActivity().getContentResolver().openInputStream(uri)) {
            MapFile mapStore = new MapFile(stream);

            TileRendererLayer renderLayer = new TileRendererLayer(
                    cache,
                    mapStore,
                    b.map.getModel().getMapViewPosition(),
                    AndroidGraphicFactory.INSTANCE
            );

            renderLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

            b.map.getLayerManager().getLayers().add(renderLayer);

            b.map.setCenter(BERLIN);
            b.map.setZoomLevel((byte) 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
