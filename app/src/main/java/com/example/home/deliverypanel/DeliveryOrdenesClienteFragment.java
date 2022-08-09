package com.example.home.deliverypanel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.home.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DeliveryOrdenesClienteFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore mFireStore;
    EditText nombrecliente, telefono, titulo, precio,description;
    Button ubicacion, tomarpedido, entregar;
    private String cliente, estado;
    private Long fecha;
    DocumentReference pedido;
    String email;

    ArrayList<OrdenesDelivery> orderArrayList;
    OrderListAdapterDelivery myAdapter;
    Hashtable<String, Object> Clienteorden = new Hashtable<>();
    Hashtable<String, Object>  orden = new Hashtable<>();


    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "key=AAAAg5BBu6U:APA91bHvg_nO2fxmQ0nv7FTojd7Hw6nvXxjFT4K2X4opbiHgzYo5vqG5X4Xu7zF8u_vf8a2IDxKaaYaxVj8URYVkjTHQJnxFSmMuBePQ9Naaof3si6uEbDNXDnLZq6RATuL3PZiCelsX";
    private String contentType = "application/json";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_deliveryordenescliente,container,false);
        getActivity().setTitle("Ordenes de clienetes");

        ubicacion = root.findViewById(R.id.btnUbicacion);
        tomarpedido = root.findViewById(R.id.btntomarpedido);
        entregar = root.findViewById(R.id.btnentregar);
        nombrecliente = root.findViewById(R.id.txtNombreCliente);
        telefono = root.findViewById(R.id.txtTelefono);

        titulo = root.findViewById(R.id.txtNombrePlato);
        precio = root.findViewById(R.id.txtPrecio);
        description = root.findViewById(R.id.txtDescription);

        cliente = getArguments().getString("cliente");
        estado = getArguments().getString("estado");
        fecha = getArguments().getLong("fecha");

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Clienteorden.isEmpty()){
                    //ubicacionContacto();

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+Clienteorden.get("lat").toString()+","+Clienteorden.get("long").toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }

                }else{
                    //mostrarDialogoSeleccion();
                }
            }
        });

        tomarpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orden.isEmpty()) {
                    return;
                }
                Hashtable<String, Object> newData = new Hashtable<>();
                newData.put("repartidor", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                newData.put("estado", "entrega");
                pedido.update(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tomarpedido.setEnabled(false);
                            Toast.makeText(getContext(), "Orden asignada correctamente", Toast.LENGTH_LONG).show();

                            String topic = "/topics/"+email;
                            JSONObject notification = new JSONObject();
                            JSONObject notifcationBody = new JSONObject();

                            try {
                                notifcationBody.put("title", "Porvenir Steaks");
                                notifcationBody.put("message", "Tu Pedido Esta En Camino") ;  //Enter your notification message
                                notification.putOpt("to", topic);
                                notification.put("data", notifcationBody);
                                Log.e("TAG", "try");
                            } catch (JSONException e) {
                                Log.e("TAG", "onCreate: " + e.getMessage());
                            }

                            sendNotification(notification,v);
                        } else {
                            Toast.makeText(getContext(), "No se puedo asignar la orden", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        entregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orden.isEmpty()) {
                    return;
                }
                Hashtable<String, Object> newData = new Hashtable<>();
                newData.put("estado", "entregado");
                pedido.update(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            entregar.setEnabled(false);
                            Toast.makeText(getContext(), "Orden se ha entregado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "No se puedo asignar la orden", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        getOrden();


        return root;
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext().getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    private void getOrden(){
        db.collection("pedidos")
                .whereEqualTo("estado", estado)
                .whereEqualTo("cliente", cliente).whereEqualTo("fecha", new Timestamp(new Date(fecha)))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                orden.putAll(doc.getData());
                                pedido = doc.getReference();
                                break;
                            }
                            if(!orden.isEmpty()){
                                obtenercliente();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "No se puedo obtener la orden", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void obtenercliente() {
        String curUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("clientes")
                .whereEqualTo("identidad", orden.get("cliente").toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Clienteorden.putAll(doc.getData());
                                break;
                            }

                            if(!Clienteorden.isEmpty()){
                                //Inicialiazar valores
                                email = Clienteorden.get("usuario").toString();
                                nombrecliente.setText(Clienteorden.get("nombre").toString());
                                telefono.setText(Clienteorden.get("telefono").toString());

                                titulo.setText(orden.get("titulo").toString());
                                precio.setText(orden.get("precio").toString());
                                description.setText(orden.get("descripcion").toString());

                                if (orden.containsKey("repartidor")) {
                                    if (!orden.get("repartidor").toString().trim().isEmpty()) {
                                        tomarpedido.setEnabled(false);
                                        entregar.setEnabled(true);
                                    } else {
                                        tomarpedido.setEnabled(true);
                                    }
                                } else {
                                    tomarpedido.setEnabled(true);
                                }
                            }

                        }
                        else{
                            Toast.makeText(getContext(), "No se puedo obtener el cliente", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

}