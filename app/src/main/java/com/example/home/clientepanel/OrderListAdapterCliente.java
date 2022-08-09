package com.example.home.clientepanel;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class OrderListAdapterCliente extends RecyclerView.Adapter<OrderListAdapterCliente.MyViewHolder>{


    private EditText msg;
    FirebaseMessaging mss;

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
        FirebaseMessaging msss;
        private String FCM_API = "https://fcm.googleapis.com/fcm/send";
        private String serverKey = "key=AAAAg5BBu6U:APA91bHvg_nO2fxmQ0nv7FTojd7Hw6nvXxjFT4K2X4opbiHgzYo5vqG5X4Xu7zF8u_vf8a2IDxKaaYaxVj8URYVkjTHQJnxFSmMuBePQ9Naaof3si6uEbDNXDnLZq6RATuL3PZiCelsX";
        private String contentType = "application/json";
        //Content-Type:application/json

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitle);
            precio = itemView.findViewById(R.id.txtPrice);
            descripcion = itemView.findViewById(R.id.txtdescripcionD);
            imagen = itemView.findViewById(R.id.imgItem);
            añadir = itemView.findViewById(R.id.btnañadirorden);

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/Enter_your_topic_name");


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
                                                                        String topic = "/topics/Enter_your_topic_name";
                                                                        JSONObject notification = new JSONObject();
                                                                        JSONObject notifcationBody = new JSONObject();

                                                                        try {
                                                                            notifcationBody.put("title", "Porvenir Steaks");
                                                                            notifcationBody.put("message", "Tu Pedido Esta En Cocina") ;  //Enter your notification message
                                                                            notification.putOpt("to", topic);
                                                                            notification.put("data", notifcationBody);
                                                                            Log.e("TAG", "try");
                                                                        } catch (JSONException e) {
                                                                            Log.e("TAG", "onCreate: " + e.getMessage());
                                                                        }

                                                                        sendNotification(notification,itemView);

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

        private void sendNotification(JSONObject notification, View itemView) {
            Log.e("TAG", "sendNotification");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    FCM_API,
                    notification,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("TAG","onResponse:"+response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(itemView.getContext(), "Error Request", Toast.LENGTH_SHORT).show();
                            Log.i("TAG", "onErrorResponse: Didn't work");
                        }

                        //public Map<String, String> getHeaders() throws AuthFailureError{

                    }
            ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext().getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        }
/*


        private void sendNotification(JSONObject notification, View itemView) {
            Log.e("TAG", "sendNotification");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        Log.i("TAG","onResponse:"+response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(itemView.getContext(), "Error Request", Toast.LENGTH_SHORT).show();
                    Log.i("TAG", "onErrorResponse: Didn't work");
                }
            }
            );
        }



        private void sendNotification(JSONObject notification, View itemView) {
            Log.e("TAG", "sendNotification");
            JsonObjectRequest jsonObjectRequest =  JsonObjectRequest(FCM_API, notification,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("TAG", "onResponse: "+response);
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(itemView.getContext(), "Error Request", Toast.LENGTH_SHORT).show();
                            Log.i("TAG", "onErrorResponse: Didn't work");
                        }) {

                        @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("Authorization",serverKey);
                                params.put("Content-Type",contentType);
                                return params;

                        }
                            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext().getApplicationContext());
                            requestQueue.add(jsonObjectRequest);


                        }
        //RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext().getApplicationContext());
            //requestQueue.add(jsonObjectRequest);
        }

            );
    }*/


}
}
