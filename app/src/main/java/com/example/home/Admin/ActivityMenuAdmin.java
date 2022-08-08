package com.example.home.Admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.home.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ActivityMenuAdmin extends Fragment {

    private static final int RESULT_OK = 0;
    //inicializacion de variables
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore mFireStore;
    StorageReference storageReference;
    EditText title,description,price,imgName;
    Button add,upload, limpiar;
    ImageView imgPrev;
    CardView cardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_admin_perfil,container,false);

        title = root.findViewById(R.id.txtNombre);
        description = root.findViewById(R.id.txtDescription);
        price = root.findViewById(R.id.txtPrecio);
        imgName = root.findViewById(R.id.txtImgName);
        add = root.findViewById(R.id.btnAdd);
        upload = root.findViewById(R.id.btnUpload);
        cardView = root.findViewById(R.id.cardView);
        imgPrev = root.findViewById(R.id.imgPreview);
        mFireStore = FirebaseFirestore.getInstance();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,100);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadAll();
                AgregarImg();
            }
        });

        title.setAlpha(0);
        description.setAlpha(0);
        price.setAlpha(0);
        imgName.setAlpha(0);
        upload.setAlpha(0);
        cardView.setAlpha(0);
        add.setAlpha(0);

        title.setTranslationX(300);
        description.setTranslationX(300);
        price.setTranslationX(300);
        imgName.setTranslationX(300);
        upload.setTranslationX(300);
        cardView.setTranslationY(300);
        add.setTranslationY(300);

        title.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        description.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        price.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        imgName.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        upload.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        cardView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
        add.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);

        return root;
    }

    //Validación de la imagen
    private void mostrarDialogoImagenNoTomada() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Alerta De Imagen De Menu")
                .setMessage("No se ha agregado ninguna fotografía, Añadir Imagen")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione la apliaccione"),10);
                    }
                }).show();
    }

    //Agregar imagen desde galeria
    private void AgregarImg(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la apliaccione"),10);

    }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      //==================================================================================
      //SUBIDA DE LA IMAGEN
      String img = imgName.getText().toString();
            if(resultCode == RESULT_OK){
                Uri path = data.getData();
                imgPrev.setImageURI(path);
                storageReference = FirebaseStorage.getInstance().getReference("images/"+img);
                storageReference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "*Error Al Agregar Menu*", Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    private void UploadAll() {
        //igualando variables
        String titulo = title.getText().toString();
        String descripcion = description.getText().toString();
        String precio = price.getText().toString()+".Lps";
        String img = imgName.getText().toString();

        //Validaciones del menu
        if (imgPrev.getDrawable() == null) {
            mostrarDialogoImagenNoTomada();
        }else if (titulo.isEmpty()){
            title.setError("Este campo no puede estar vacio.");
        } else if (descripcion.isEmpty()) {
            description.setError("Este campo no puede estar vacio.");
        } else if (precio.isEmpty()) {
            price.setError("Este campo no puede estar vacio");
        } else if (img.isEmpty()){
            imgName.setError("Campo vacio, ingrese el nombre de la imagen del plato");
        }else{
            //Guardar datos en Firebase
            Map<String,Object> map = new HashMap<>();
            map.put("titulo",titulo);
            map.put("descripcion",descripcion);
            map.put("precio",precio);
            map.put("img",img);

            mFireStore.collection("platillo").document().set(map);
            AgregarImg();
            Toast.makeText(getActivity(), "¡¡Menu Añadido Con Exito!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), ActivityMenuAdmin.class));
        }
    }

    private void ClearScreen() {
        title.setText("");
        description.setText("");
        price.setText("");
        imgName.setText("");
        imgPrev.setImageBitmap(null);
    }

}