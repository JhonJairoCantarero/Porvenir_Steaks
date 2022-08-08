package com.example.home.deliverypanel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.home.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class DeliveryOrdenesClienteFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore mFireStore;
    EditText nombrecliente, telefono, titulo, precio,description;
    Button ubicacion, tomarpedido, entregar;
    private String cliente, estado;
    private Long fecha;
    DocumentReference pedido;

    ArrayList<OrdenesDelivery> orderArrayList;
    OrderListAdapterDelivery myAdapter;
    Hashtable<String, Object> Clienteorden = new Hashtable<>();
    Hashtable<String, Object>  orden = new Hashtable<>();



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