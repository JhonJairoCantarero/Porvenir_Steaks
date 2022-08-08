package com.example.home;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.home.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idCliente;

    Hashtable<String, Hashtable<String, Object>> valoresInicialesRepartidores = new Hashtable();
    Hashtable<String, DocumentReference> referenciaRepartidores = new Hashtable<>();
    ArrayList<Hashtable<String, Object>> pedidos = new ArrayList<>();
    Hashtable<String, Object> cliente = new Hashtable<>();

    Hashtable<String, Marker> markersList = new Hashtable<>();

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        idCliente = getIntent().getExtras().getString("cliente");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        obtenerCliente();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double latitud = new Double(cliente.get("lat").toString());
        Double longitud = new Double(cliente.get("long").toString());

        LatLng myLocation = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi Ubicacion"));

        Set<String> virKeys = valoresInicialesRepartidores.keySet();
        for (String key : virKeys) {
            Double la = new Double(valoresInicialesRepartidores.get(key).get("lat").toString());
            Double lo = new Double(valoresInicialesRepartidores.get(key).get("long").toString());
            LatLng l = new LatLng(la, lo);

            String title = "";
            int counter = 0;
            for (Hashtable<String, Object> pedido : pedidos) {
                if (pedido.get("repartidor").toString().equals(key)) {
                    title += (counter == 0) ? pedido.get("titulo").toString() : ", " + pedido.get("titulo").toString();
                    counter++;
                }
            }

            //markersList.put(key, mMap.addMarker(new MarkerOptions().position(l).title(valoresInicialesRepartidores.get(key).get("nombre").toString())));
            markersList.put(key, mMap.addMarker(new MarkerOptions().position(l).title(title)));

            referenciaRepartidores.get(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    Double la = value.getDouble("lat");
                    Double lo = value.getDouble("long");

                    LatLng l = new LatLng(la, lo);
                    markersList.get(value.getString("correo")).setPosition(l);
                }
            });
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitud, longitud))
                .zoom(14)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void obtenerCliente() {
        db.collection("clientes")
                .whereEqualTo("identidad", idCliente)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cliente.putAll(document.getData());
                                break;
                            }

                            if (!cliente.isEmpty()) {
                                obtenerPedidos();
                            }
                            else
                            {
                                Toast.makeText(MapsActivity.this, "No se encontro cliente", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Error al obtener cliente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void obtenerPedidos() {
        ArrayList<String> estados = new ArrayList<>();
        estados.add("pendiente");
        estados.add("entrega");
        db.collection("pedidos")
                .whereEqualTo("cliente", cliente.get("identidad"))
                .whereIn("estado", Arrays.asList(estados.toArray()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Hashtable<String, Object> pedido = new Hashtable<>();
                                pedido.putAll(document.getData());
                                pedidos.add(pedido);
                            }
                            if (!pedidos.isEmpty()) {
                                obtenerRepartidores();
                            }
                            else
                            {
                                Toast.makeText(MapsActivity.this, "No se encontraron pedidos", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Error al obtener pedidos", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void obtenerRepartidores() {
        ArrayList<String> listaIdRepartidores = new ArrayList<>();
        for (Hashtable<String, Object> pedido : pedidos) {
            if (pedido.containsKey("repartidor")) {
                listaIdRepartidores.add(pedido.get("repartidor").toString());
            }
        }

        if (listaIdRepartidores.isEmpty()) {
            Toast.makeText(MapsActivity.this, "Sus pedidos aun no han sido enviados", Toast.LENGTH_LONG).show();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);
        } else {
            db.collection("repartidores")
                    .whereIn("correo", Arrays.asList(listaIdRepartidores.toArray()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Hashtable<String, Object> repartidor = new Hashtable<>();
                                    repartidor.putAll(document.getData());
                                    valoresInicialesRepartidores.put(repartidor.get("correo").toString(), repartidor);
                                    referenciaRepartidores.put(repartidor.get("correo").toString(), document.getReference());
                                }

                                if (!valoresInicialesRepartidores.isEmpty()) {
                                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(MapsActivity.this);
                                }
                                else
                                {
                                    Toast.makeText(MapsActivity.this, "No se encontraron repartidores", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MapsActivity.this, "Error al obtener repartidores", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }

    }
}