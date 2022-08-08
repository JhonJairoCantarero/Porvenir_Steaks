package com.example.home.adminpanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminOrdersListRealizadosFragment extends Fragment {

    FirebaseFirestore mFireStore;
    RecyclerView recyclerView;
    ArrayList<OrderListModel> orderArrayList;
    OrderListAdapter myAdapter;
    TextView txtP, txtE, estado, pedidos;
    Button btnpendiente, btnentregado;
    String cliente = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_admin_orders_list_fragment, container, false);


        pedidos = root.findViewById(R.id.textView11);
        estado = root.findViewById(R.id.txtdescripcionD);
        mFireStore = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        cliente = getArguments().getString("cliente");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderArrayList = new ArrayList<OrderListModel>();
        myAdapter = new OrderListAdapter(getContext(), orderArrayList, cliente);
        recyclerView.setAdapter(myAdapter);


        if(cliente == null){
            EventChangeListener();
        }
        else{
            ObtenerCliente();
        }

        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(300);
        recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);

        btnpendiente = root.findViewById(R.id.btnpendiente);
        btnentregado = root.findViewById(R.id.btnentregado);

        txtE = root.findViewById(R.id.textE);
        txtP = root.findViewById(R.id.textP);

        txtE.setAlpha(0);
        txtE.setTranslationX(300);
        txtE.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        txtP.setAlpha(0);
        txtP.setTranslationX(300);
        txtP.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);

        btnpendiente.setAlpha(0);
        btnpendiente.setTranslationX(300);
        btnpendiente.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        btnentregado.setAlpha(0);
        btnentregado.setTranslationX(300);
        btnentregado.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);


        pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                if(cliente == null){
                    EventChangeListener();
                }
                else{
                    ObtenerCliente();
                }
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });

        btnpendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                if(cliente == null){
                    EventPendiente();
                }
                else{
                    ObtenerCliente();
                }
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });

        TextView estado;
        estado = root.findViewById(R.id.txtestadoD);

        btnentregado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                if(cliente == null){
                    EventEntregado();
                }
                else{
                    ObtenerCliente();
                }
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });

        return root;

    }

    private void ObtenerCliente() {
        mFireStore.collection("clientes")
                .whereEqualTo("usuario", cliente)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String c = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                c = document.getString("identidad");
                                break;
                            }

                            if (c != null) {
                                llenarPedidosClientes(c);
                            }
                            else
                            {
                                Toast.makeText(getContext(), "No se encontro cliente", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error al obtener cliente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void EventChangeListener() {

        //Query query = mFireStore.whereEqualTo("doc_telefono",Phone).orderBy("doc_fecha",Query.Direction.DESCENDING);
        mFireStore.collection("pedidos").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                    Log.e("FireStore error:",error.getMessage());
                    return;
                }
                if (error == null){
                }

                for (DocumentChange dc : value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        orderArrayList.add(dc.getDocument().toObject(OrderListModel.class));
                        Collections.sort(orderArrayList, new Comparator<OrderListModel>() {
                            @Override
                            public int compare(OrderListModel orderListModel, OrderListModel t1) {
                                return t1.getTitulo().compareTo(orderListModel.getTitulo());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    private void llenarPedidosClientes(String c) {

        btnentregado.setVisibility(View.GONE);
        btnpendiente.setVisibility(View.GONE);
        txtE.setVisibility(View.GONE);
        txtP.setVisibility(View.GONE);

        //Query query = mFireStore.whereEqualTo("doc_telefono",Phone).orderBy("doc_fecha",Query.Direction.DESCENDING);
        mFireStore.collection("pedidos")
                .whereEqualTo("cliente", c)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                    Log.e("FireStore error:",error.getMessage());
                    return;
                }
                if (error == null){
                }

                for (DocumentChange dc : value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        orderArrayList.add(dc.getDocument().toObject(OrderListModel.class));
                        Collections.sort(orderArrayList, new Comparator<OrderListModel>() {
                            @Override
                            public int compare(OrderListModel orderListModel, OrderListModel t1) {
                                return t1.getTitulo().compareTo(orderListModel.getTitulo());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void EventPendiente() {

        mFireStore.collection("pedidos").whereEqualTo("estado", "pendiente").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                    Log.e("FireStore error:",error.getMessage());
                    return;
                }
                if (error == null){
                }

                for (DocumentChange dc : value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        orderArrayList.add(dc.getDocument().toObject(OrderListModel.class));
                        Collections.sort(orderArrayList, new Comparator<OrderListModel>() {
                            @Override
                            public int compare(OrderListModel orderListModel, OrderListModel t1) {
                                return t1.getTitulo().compareTo(orderListModel.getTitulo());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void EventEntregado() {

        //Query query = mFireStore.whereEqualTo("doc_telefono",Phone).orderBy("doc_fecha",Query.Direction.DESCENDING);
        mFireStore.collection("pedidos").whereEqualTo("estado", "entregado").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                    Log.e("FireStore error:",error.getMessage());
                    return;
                }
                if (error == null){
                }

                for (DocumentChange dc : value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        orderArrayList.add(dc.getDocument().toObject(OrderListModel.class));
                        Collections.sort(orderArrayList, new Comparator<OrderListModel>() {
                            @Override
                            public int compare(OrderListModel orderListModel, OrderListModel t1) {
                                return t1.getTitulo().compareTo(orderListModel.getTitulo());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}