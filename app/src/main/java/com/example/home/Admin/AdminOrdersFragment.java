package com.example.home.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

//import com.example.demoapp.Items_Food.AddFood;
import com.example.home.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AdminOrdersFragment extends Fragment {

    FirebaseFirestore mFireStore;
    StorageReference storageReference;
    EditText title,description,price,imgName;
    Button add,upload;
    ImageView imgPrev;
    CardView cardView;
    Uri imageUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null && data.getData() !=null){
            imageUri = data.getData();
            imgPrev.setImageURI(imageUri);
        }
    }

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
                intent.setType("images/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,100);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //==================================================================================
                //SUBIDA DE LA IMAGEN
                String img = imgName.getText().toString();

                storageReference = FirebaseStorage.getInstance().getReference("images/"+img);
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imgPrev.setImageURI(null);
                        UploadAll();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al intentar subir la Imagen", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void UploadAll() {
        //SUBIDA AL FIRESTORE
        String titulo = title.getText().toString();
        String descripcion = description.getText().toString();
        String precio = price.getText().toString();
        String img = imgName.getText().toString();

        Map<String,Object> map = new HashMap<>();
        map.put("titulo",titulo);
        map.put("descripcion",descripcion);
        map.put("precio",precio);
        map.put("img",img);

        mFireStore.collection("food").document().set(map);
        dashboard();
    }

    private void dashboard() {
        mFireStore.collection("dashboard").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){

                        String user = document.getData().get("nUsers").toString();
                        String foods = document.getData().get("nPlatos").toString();
                        String total = document.getData().get("total").toString();
                        String order = document.getData().get("nPedidos").toString();

                        int nfood = Integer.parseInt(foods);
                        nfood = nfood+1;

                        Map<String,Object> map2 = new HashMap<>();
                        map2.put("nUsers",user);
                        map2.put("nPlatos",nfood);
                        map2.put("total",total);
                        map2.put("nPedidos",order);

                        mFireStore.collection("dashboard").document("dashboardinfo").set(map2);


                    }
                } else {
                    Log.w("users","Error", task.getException());
                }
            }
        });

        //nextActivity();
    }

   /* private void nextActivity() {
        String email = getActivity().getIntent().getStringExtra("n1");
        Intent intent = new Intent(getActivity(), AddFood.class);
        intent.putExtra("n1",email);
        intent.putExtra("item",title.getText().toString());
        startActivity(intent);
    }*/

}
