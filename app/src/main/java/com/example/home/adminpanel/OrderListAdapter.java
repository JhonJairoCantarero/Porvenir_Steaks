package com.example.home.adminpanel;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.R;
import com.example.home.deliverypanel.DeliveryOrdenesClienteFragment;
import com.example.home.ratingbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class
OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder>{

    Context context;
    String c;
    ArrayList<OrderListModel> orderArrayList;
    private StorageReference storageReference;

    public OrderListAdapter(Context context, ArrayList<OrderListModel> orderArrayList, String cliente) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.c = cliente;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.orders_item_admin,parent,false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        OrderListModel order = orderArrayList.get(position);
        holder.titulo.setText(order.titulo);
        holder.precio.setText(order.precio+".Lps");
        holder.estado.setText(order.estado);
        holder.cliente.setText(order.cliente);
        holder.descripcion.setText(order.descripcion);
        holder.repartidor.setText(order.repartidor);
        holder.fecha = order.fecha;
        holder.c = this.c;

        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+order.img);
        try {
            final File localFile = File.createTempFile(""+order.img,"jpeg");
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

        TextView titulo,precio,cliente,estado, descripcion, repartidor;
        ImageView imagen;
        Button calificar;
        Timestamp fecha;
        String c;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitle);
            precio = itemView.findViewById(R.id.txtPrice);
            cliente = itemView.findViewById(R.id.txtCliente);
            estado = itemView.findViewById(R.id.txtestadoD);
            imagen = itemView.findViewById(R.id.imgItem);
            descripcion = itemView.findViewById(R.id.txtdescripcion);
            repartidor = itemView.findViewById(R.id.txtRepartidor);
            calificar = itemView.findViewById(R.id.btnCalificar);
            if(c!=null)
            {

            }
            calificar.setVisibility(View.VISIBLE);
            calificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("cliente",cliente.getText().toString());
                    b.putLong("fecha", fecha.toDate().getTime());
                    b.putString("estado",estado.getText().toString());

                    Intent i = new Intent(itemView.getContext().getApplicationContext(), ratingbar.class);
                    i.putExtras(b);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
