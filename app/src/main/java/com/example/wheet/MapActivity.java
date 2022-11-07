package com.example.wheet;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean switcher = true;
    private LatLng p1 = null, p2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if(switcher) {
                    p1 = latLng;
                    switcher = false;
                } else {
                    p2 = latLng;
                    switcher = true;
                }

                if (p1 != null && p2 != null) {
                    int dist = (int)(SphericalUtil.computeDistanceBetween(p1, p2) / 1000);
                    Toast.makeText(getApplicationContext(), dist + "km", Toast.LENGTH_LONG).show();

                    p1 = null;
                    p2 = null;
                }

            }
        });

    }
}
