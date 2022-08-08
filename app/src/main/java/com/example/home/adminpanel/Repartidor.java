package com.example.home.adminpanel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.home.R;
import com.example.home.adminpanel.User2;
import com.example.home.databinding.PerfilRBinding;
import com.example.home.login.usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class Repartidor extends Fragment implements View.OnClickListener{
    private TextView registrarUsuario;
    private EditText nombre,correo,contrasena,longitud2,latitud2;
    DatabaseReference mRootReference;
    private Hashtable<String, Object> repartidor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference repartidorRef;
    private String repartidor2;
    Double longitud;
    Double latitud;

    //private Repartidor binding;
    private PerfilRBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RepartidorViewModel galleryViewModel =
                new ViewModelProvider(this).get(RepartidorViewModel.class);

        binding = PerfilRBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        registrarUsuario = (Button) root.findViewById(R.id.buttonRegistro);
        registrarUsuario.setOnClickListener(this);


        nombre = (EditText)  root.findViewById(R.id.NombreR);
        correo = (EditText)  root.findViewById(R.id.CorreoR);
        contrasena = (EditText)  root.findViewById(R.id.ContrasenaR);


        mRootReference = FirebaseDatabase.getInstance().getReference();
        mRootReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    usuario user = dataSnapshot.getValue(usuario.class);
                    String as = user.getAs();
                    Log.e("Datos Roles:", ""+as);
                    Log.e("Datos:", ""+dataSnapshot.getValue());

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.buttonRegistro:
                registrarUsuarios();
                break;
        }
        return ;
    }

    private void registrarUsuarios(){
        String nom=  nombre.getText().toString().trim();
        String email=  correo.getText().toString().trim();
        String clave=  contrasena.getText().toString().trim();
        String as = "repartidor";
        String uid = mAuth.getUid();


        if(nom.isEmpty()){
            nombre.setError("Campo Obligatorio");
            nombre.requestFocus();
            return;
        }

        if(email.isEmpty()){
            correo.setError("Campo Obligatorio");
            correo.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            correo.setError("Correo Incorrecto");
            correo.requestFocus();
            return;
        }

        if(clave.isEmpty()){
            contrasena.setError("Campo Obligatorio");
            contrasena.requestFocus();
            return;
        }

        if(clave.length() < 6){
            contrasena.setError("Contrasena Menor De 6 Caracteres");
            contrasena.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,clave)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User2 user = new User2(nom, email , clave, as, uid, longitud, latitud);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Registro Exitoso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Usuario o Correo Ya Estan Ingresados", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(getActivity(), "Registro Fallo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        repartidor = new Hashtable<>();
        repartidor.put("nombre", binding.NombreR.getText().toString());
        repartidor.put("correo", binding.CorreoR.getText().toString());
        repartidor.put("clave", binding.ContrasenaR.getText().toString());
        repartidor.put("lat", 0.0);
        repartidor.put("long", 0.0);

        if (repartidorRef == null) {
          //  repartidor.put("repartidores", repartidor2);
            db.collection("repartidores")
                    .add(repartidor)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                repartidorRef = task.getResult();
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            repartidorRef.update(repartidor)
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


}