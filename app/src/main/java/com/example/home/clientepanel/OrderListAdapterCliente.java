package com.example.home.clientepanel;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class OrderListAdapterCliente extends RecyclerView.Adapter<OrderListAdapterCliente.MyViewHolder>{

    Context context;
    ArrayList<Platillo> orderArrayList;
    private StorageReference storageReference;

    public OrderListAdapterCliente(Context context, ArrayList<Platillo> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cliente_orders_item,parent,false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Platillo order = orderArrayList.get(position);
        holder.titulo.setText(order.titulo);
        holder.precio.setText("s/. "+order.precio);
        holder.descripcion.setText(order.descripcion);
        holder.img = order.img;

        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+order.img);
        try {
            final File localFile = File.createTempFile("img_plat_" +order.img,"jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.imagen.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        String img;
        TextView titulo,precio,descripcion;
        ImageView imagen;
        Button añadir;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitle);
            precio = itemView.findViewById(R.id.txtPrice);
            descripcion = itemView.findViewById(R.id.txtdescripcionD);
            imagen = itemView.findViewById(R.id.imgItem);
            añadir = itemView.findViewById(R.id.btnañadirorden);
            añadir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hashtable<String,Object> pedido = new Hashtable<>();
                    Hashtable<String,Object> cliente = new Hashtable<>();
                    pedido.put("titulo", titulo.getText().toString());
                    pedido.put("estado", "pendiente");
                    pedido.put("fecha", new Timestamp(new Date()));
                    pedido.put("precio", precio.getText().toString());
                    pedido.put("descripcion", descripcion.getText().toString());
                    pedido.put("img", img);



                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Confirmación del Pedido")
                            .setMessage("¿Esta seguro de realizar este pedido?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("clientes").whereEqualTo("usuario", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        for(QueryDocumentSnapshot doc: task.getResult())
                                                        {
                                                            cliente.putAll(doc.getData());
                                                            break;
                                                        }
                                                        if(!cliente.isEmpty())
                                                        {
                                                            pedido.put("cliente", cliente.get("identidad").toString());
                                                            db.collection("pedidos").add(pedido).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Toast.makeText(itemView.getContext(), "Pedido Realizado Correctamente", Toast.LENGTH_LONG).show();
                                                                    }
                                                                    else
                                                                    {
                                                                        Toast.makeText(itemView.getContext(), "Error al realizar el pedido", Toast.LENGTH_LONG).show();
                                                                    }

                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(itemView.getContext(), "Pedido cancelado", Toast.LENGTH_LONG).show();
                                }
                            }).show();
                }


            });
        }
    }
}
