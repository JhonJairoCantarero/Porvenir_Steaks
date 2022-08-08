package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.home.deliverypanel.DeliveryEnvioFragment;
import com.example.home.deliverypanel.DeliveryOrdenFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Hashtable;
import java.util.List;

public class delivery_panel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 5000;
    private LocationManager locationManager;
    public DocumentReference usuario;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_panel);
        BottomNavigationView navigationView = findViewById(R.id.delivery_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        obtenerRepartidor();
    }

    private void actualizarUbicacion() {
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                if(FirebaseAuth.getInstance().getCurrentUser()==null)
                {
                    if(runnable != null){
                        usuario = null;
                        handler.removeCallbacks(runnable);
                    }
                    return;
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(delivery_panel.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    return;
                }

                Location bestLocation = null;
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = locationManager.getProviders(true);
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }

                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }

                if (bestLocation == null) {
                    Toast.makeText(getApplicationContext(), "Error al obtener la ubicacion actual", Toast.LENGTH_SHORT).show();
                    return;
                }

                Hashtable<String, Object> values = new Hashtable<>();
                values.put("lat",  bestLocation.getLatitude());
                values.put("long", bestLocation.getLongitude());

                usuario.update(values);
            }
        }, delay);
    }

    private void obtenerRepartidor() {
        db.collection("repartidores")
                .whereEqualTo("correo", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                usuario = doc.getReference();
                                break;
                            }

                            if (usuario != null) {
                                actualizarUbicacion();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "ALGO MAL", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.ordenespendientes:
                fragment = new DeliveryOrdenFragment();
                break;
            case R.id.ordenesenviadas:
                fragment = new DeliveryEnvioFragment();
                break;
            case R.id.Cerrar_SesionD:
                cambiarEstado();
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();
                break;

        }
        return loaddeliveryfragment(fragment);
    }

    private boolean loaddeliveryfragment(Fragment fragment) {
        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerd, fragment).commit();
            return true;
        }
        return false;

    }

    public void cambiarEstado() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = false;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_3", estado3);
        editor3.commit();
    }

}