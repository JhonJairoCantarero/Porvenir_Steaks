package com.example.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.home.databinding.ActivityMapaPerfilBinding;
import com.example.home.databinding.ActivityMapaPerfilBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaPerfilActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapaPerfilBinding binding;
    private Marker mMarker;
    private double la, lo;

    Button btnEstablecerMapaPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        la = getIntent().getExtras().getDouble("lat");
        lo = getIntent().getExtras().getDouble("long");

        btnEstablecerMapaPerfil = (Button) findViewById(R.id.btnEstablecerMapaPerfil);

        btnEstablecerMapaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                result.putExtra("lat", mMarker.getPosition().latitude);
                result.putExtra("long", mMarker.getPosition().longitude);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ubicacion = new LatLng(0, 0);
        if (la != 0 && lo != 0) {
            ubicacion = new LatLng(la, lo);
        }

        mMarker = mMap.addMarker(new MarkerOptions().position(ubicacion).title("Mi ubicación"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(la, lo))
                .zoom(14)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMarker.setPosition(latLng);
            }
        });
    }
}