package com.example.home.clientepanel;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.home.MainActivity;
import com.example.home.MapaPerfilActivity;
import com.example.home.R;
import com.example.home.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class PerfilFragment extends Fragment {

    private Hashtable<String, Object> cliente;
    private DocumentReference clienteRef;
    private String usuario;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentPerfilBinding binding;
    private LocationManager locationManager;
    private Button cerrarSesion1;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("clientes")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                byte[] b = document.getBlob("foto").toBytes();
                                Bitmap bi = BitmapFactory.decodeByteArray( b,0, b.length);
                                binding.ivPhotoPerfil.setImageBitmap(bi);
                                binding.etIdentidadPerfil.setText(document.getString("identidad"));
                                binding.etNombrePerfil.setText(document.getString("nombre"));
                                binding.etTelefonoPerfil.setText(document.getString("telefono"));

                                Integer y = document.getTimestamp("nacimiento").toDate().getYear() + 1900;
                                Integer m = document.getTimestamp("nacimiento").toDate().getMonth() + 1;
                                Integer d = document.getTimestamp("nacimiento").toDate().getDate();

                                String fecha = "";
                                fecha += y.toString() + "/";
                                fecha += ((m < 10) ? "0" + m.toString() : m.toString()) + "/";
                                fecha += (d < 10) ? "0" + d.toString() : d.toString();
                                binding.etNacimientoPerfil.setText(fecha);

                                binding.etLatitudPerfil.setText(document.getDouble("lat").toString());
                                binding.etLongitudPerfil.setText(document.getDouble("long").toString());
                                clienteRef = document.getReference();
                                break;
                            }
                        } else {
                            Toast.makeText(getContext(), "ALGUN ERROR", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        cerrarSesion1 = (Button) root.findViewById(R.id.btnCerrarSesion);

        cerrarSesion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado();
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(getContext(), MainActivity.class);
                startActivity(in);
                getActivity().finish();
            }
        });

        binding.btnLocacionActualPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    return;
                }

                Location bestLocation = null;
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
                    Toast.makeText(getContext(), "Error al obtener la ubicacion actual", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.etLatitudPerfil.setText(Double.toString(bestLocation.getLatitude()));
                binding.etLongitudPerfil.setText(Double.toString(bestLocation.getLongitude()));
            }
        });

        binding.btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !binding.etIdentidadPerfil.getText().toString().trim().isEmpty()
                        && !binding.etNombrePerfil.getText().toString().trim().isEmpty()
                        && !binding.etTelefonoPerfil.getText().toString().trim().isEmpty()
                        && !binding.etNacimientoPerfil.getText().toString().trim().isEmpty()
                        && !binding.etLatitudPerfil.getText().toString().trim().isEmpty()
                        && !binding.etLongitudPerfil.getText().toString().trim().isEmpty()) {
                    subirDatos();
                } else {
                    Toast.makeText(getContext(), "No puede dejar campos vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.etNacimientoPerfil.setFocusableInTouchMode(false);
        binding.etNacimientoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date fecha;
                try {
                    fecha = new SimpleDateFormat("yyyy/MM/dd").parse("2000/01/01");

                    if (!binding.etNacimientoPerfil.toString().isEmpty()) {
                        fecha = new SimpleDateFormat("yyyy/MM/dd").parse(binding.etNacimientoPerfil.getText().toString());
                    }
                } catch (Exception ex) {
                    fecha = new Date();
                }

                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String anio = Integer.toString(i);
                        String mes = ((i1+1) < 10) ? "0"+Integer.toString(i1+1): Integer.toString(i1);
                        String dia = (i2 < 10) ? "0"+Integer.toString(i2): Integer.toString(i2);
                        binding.etNacimientoPerfil.setText(anio+"/"+mes+"/"+dia);
                    }
                }, fecha.getYear()+1900, fecha.getMonth(), fecha.getDate()).show();
            }
        });

        binding.btnMapaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    return;
                }

                Location l = null;
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                Intent i = new Intent(getActivity().getApplicationContext(), MapaPerfilActivity.class);

                Double la = 0.0;
                Double lo = 0.0;

                if (!binding.etLatitudPerfil.getText().toString().isEmpty() && !binding.etLongitudPerfil.getText().toString().isEmpty()) {
                    la = Double.parseDouble(binding.etLatitudPerfil.getText().toString());
                    lo = Double.parseDouble(binding.etLongitudPerfil.getText().toString());
                } else if (l != null) {
                        la = l.getLatitude();
                        lo = l.getLongitude();
                }

                i.putExtra("lat", la);
                i.putExtra("long", lo);
                startActivityForResult(i, 1010);
            }
        });

        binding.ivPhotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                    return;
                }

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                    return;
                }

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 104);
                    return;
                }

                dispatchTakePictureIntent();
            }
        });

        return root;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error al abrir la camara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1010) : {
                if (resultCode == Activity.RESULT_OK) {
                    binding.etLatitudPerfil.setText(Double.toString(data.getExtras().getDouble("lat")));
                    binding.etLongitudPerfil.setText(Double.toString(data.getExtras().getDouble("long")));
                }
                break;
            }
            case (REQUEST_IMAGE_CAPTURE) : {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    binding.ivPhotoPerfil.setImageBitmap(imageBitmap);
                }
                break;
            }
        }
    }

    public void subirDatos() {
        cliente = new Hashtable<>();

        Bitmap foto = ((BitmapDrawable) binding.ivPhotoPerfil.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        cliente.put("foto", Blob.fromBytes(data));
        cliente.put("identidad", binding.etIdentidadPerfil.getText().toString());
        cliente.put("nombre", binding.etNombrePerfil.getText().toString());
        cliente.put("telefono", binding.etTelefonoPerfil.getText().toString());
        try {
            cliente.put("nacimiento", new Timestamp(new SimpleDateFormat("yyyy/MM/dd").parse(binding.etNacimientoPerfil.getText().toString())));
        } catch (Exception ex) {
            cliente.put("nacimiento", new Timestamp(new Date()));
        }
        cliente.put("lat", Double.parseDouble(binding.etLatitudPerfil.getText().toString()));
        cliente.put("long", Double.parseDouble(binding.etLongitudPerfil.getText().toString()));

        if (clienteRef == null) {
            cliente.put("usuario", usuario);
            db.collection("clientes")
                    .add(cliente)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                clienteRef = task.getResult();
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            clienteRef.update(cliente)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void cambiarEstado() {
        SharedPreferences preferences3 = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = false;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_2", estado3);
        editor3.commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}