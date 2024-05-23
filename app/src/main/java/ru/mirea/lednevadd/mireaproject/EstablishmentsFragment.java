package ru.mirea.lednevadd.mireaproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.mirea.lednevadd.mireaproject.databinding.FragmentEstablishmentsBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;

public class EstablishmentsFragment extends Fragment {
    private MapView mapview = null;
    private FragmentEstablishmentsBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean isWork;
    private MyLocationNewOverlay locationNewOverlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEstablishmentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        mapview = binding.mapView;
        mapview.setZoomRounding(true);
        mapview.setMultiTouchControls(true);

        IMapController mapController = mapview.getController();
        mapController.setZoom(10.0);
        GeoPoint startPoint = new GeoPoint(41.291789, -74.023172);
        mapController.setCenter(startPoint);

        int locationPermissionStatus = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(locationPermissionStatus == PackageManager.PERMISSION_GRANTED){
            isWork = true;
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
        }

        if(isWork){
            locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapview);
            locationNewOverlay.enableMyLocation();
            mapview.getOverlays().add(locationNewOverlay);
        }

        CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mapview);
        compassOverlay.enableCompass();
        mapview.getOverlays().add(compassOverlay);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapview);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapview.getOverlays().add(scaleBarOverlay);

        Marker marker1 = new Marker(mapview);
        marker1.setPosition(new GeoPoint(40.689342,-74.045112));
        marker1.setOnMarkerClickListener((marker, mapView) -> {
            Toast.makeText(context, "Человек-паук: Нет пути домой, 2021. Мстители: финал, 2019",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        mapview.getOverlays().add(marker1);
        marker1.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker1.setTitle("Статуя Свободы");


        Marker marker2 = new Marker(mapview);
        marker2.setPosition(new GeoPoint(41.828678, -73.957519));
        marker2.setOnMarkerClickListener((marker, mapView) -> {
            Toast.makeText(context, "Новая штаб-квартира Мстителей",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        mapview.getOverlays().add(marker2);
        marker2.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker2.setTitle("Штаб-квартира Мстителей ");

        // Third marker with random coordinates
        Marker marker3 = new Marker(mapview);
        marker3.setPosition(new GeoPoint(40.770264, -73.975107));
        marker3.setOnMarkerClickListener((marker, mapView) -> {
            Toast.makeText(context, "Мстители, 2012",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        mapview.getOverlays().add(marker3);
        marker3.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker3.setTitle("Центральный парк Нью-Йорка");

        // Set up a long press listener to add a marker
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                addMarkerAtPoint(p);
                return true;
            }
        };

        MapEventsOverlay overlay = new MapEventsOverlay(mReceive);
        mapview.getOverlays().add(overlay);

        return view;
    }

    // Function to add a marker at a specific point
    private void addMarkerAtPoint(GeoPoint point) {
        Marker marker = new Marker(mapview);
        marker.setPosition(point);
        marker.setOnMarkerClickListener((m, mapView) -> {
            Toast.makeText(getActivity(), "Новый маркер добавлен",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        mapview.getOverlays().add(marker);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setTitle("Новый маркер");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                isWork = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!isWork) getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        if (mapview != null) {
            mapview.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        if (mapview != null) {
            mapview.onPause();
        }
    }
}
