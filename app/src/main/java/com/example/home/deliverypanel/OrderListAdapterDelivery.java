package com.example.home.deliverypanel;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;

public class OrderListAdapterDelivery extends RecyclerView.Adapter<OrderListAdapterDelivery.MyViewHolder> {

    Context context;
    private Context com;
    ArrayList<OrdenesDelivery> orderArrayList;
    private StorageReference storageReference;
    private FragmentManager fragmentManager;


    public OrderListAdapterDelivery(Context context, ArrayList<OrdenesDelivery> orderArrayList, FragmentManager manager) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.fragmentManager = manager;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.delivery_orders_item,parent,false);
        return new MyViewHolder(v, this.fragmentManager);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        OrdenesDelivery order = orderArrayList.get(position);
        holder.titulo.setText(order.titulo);
        holder.precio.setText("s/. "+order.precio);
        holder.descripcion.setText(order.descripcion);
        holder.estado.setText(order.estado);
        holder.cliente.setText(order.cliente);
        // holder.fecha.setText(order.getfecha2().toString());


        holder.fecha = order.fecha;
        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+order.img);
        try {
            final File localFile = File.createTempFile("img_plat_"+order.img,"jpeg");
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        FragmentManager fragmentManager;
        Timestamp fecha;
        TextView titulo,precio,descripcion,estado,cliente;
        ImageView imagen;
        Button detalle;

        public MyViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            this.fragmentManager = fragmentManager;
            titulo = itemView.findViewById(R.id.txtTitle);
            precio = itemView.findViewById(R.id.txtPrice);
            descripcion = itemView.findViewById(R.id.txtdescripcionD);
            estado = itemView.findViewById(R.id.txtestadoD);
            cliente = itemView.findViewById(R.id.txtCliente);
            imagen = itemView.findViewById(R.id.imgItem);
            detalle = itemView.findViewById(R.id.btndetalle);
            // fecha = itemView.findViewById(R.id.txtfecha);

            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = null;

                    fragment = new DeliveryOrdenesClienteFragment();
                    Bundle b = new Bundle();
                    b.putString("cliente",cliente.getText().toString());
                    b.putLong("fecha", fecha.toDate().getTime());
                    b.putString("estado",estado.getText().toString());
                    fragment.setArguments(b);
                    fragmentManager.beginTransaction().replace(R.id.id_ordenes,fragment).commit();
                }
            });
        }
    }
}
