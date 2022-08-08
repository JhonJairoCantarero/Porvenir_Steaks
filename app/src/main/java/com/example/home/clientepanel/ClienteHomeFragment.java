package com.example.home.clientepanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home.R;
import com.example.home.adminpanel.OrderListAdapter;
import com.example.home.adminpanel.OrderListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClienteHomeFragment extends Fragment {

    Button añadir;
    FirebaseFirestore mFireStore;
    RecyclerView recyclerView;
    ArrayList<Platillo> orderArrayList;
    OrderListAdapterCliente myAdapter;
    TextView txtP, txtE, estado, pedidos;
    Button btnpendiente, btnentregado;
    private String usuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cliente_home, container, false);

        usuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        añadir = root.findViewById(R.id.btnañadirorden);
        pedidos = root.findViewById(R.id.textView11);
        estado = root.findViewById(R.id.txtDescription);
        mFireStore = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderArrayList = new ArrayList<Platillo>();
        myAdapter = new OrderListAdapterCliente(getContext(), orderArrayList);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(300);
        recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);

        btnpendiente = root.findViewById(R.id.btnpendiente);
        btnentregado = root.findViewById(R.id.btnentregado);
        txtE = root.findViewById(R.id.textE);
        txtP = root.findViewById(R.id.textP);

//        añadir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
      /* txtE.setAlpha(0);
        txtE.setTranslationX(300);
        txtE.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        txtP.setAlpha(0);
        txtP.setTranslationX(300);
        txtP.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);*/

       /* btnpendiente.setAlpha(0);
        btnpendiente.setTranslationX(300);
        btnpendiente.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);
        btnentregado.setAlpha(0);
        btnentregado.setTranslationX(300);
        btnentregado.animate().alpha(1).translationX(0).setDuration(1000).setStartDelay(500);*/



        pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                EventChangeListener();
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });

        /*btnpendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                EventPendiente();
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });*/

       /* TextView estado;
        estado = root.findViewById(R.id.txtDescripcion);
        btnentregado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderArrayList.clear();
                myAdapter.notifyDataSetChanged();
                EventEntregado();
                recyclerView.setAlpha(0);
                recyclerView.setTranslationY(300);
                recyclerView.animate().alpha(1).translationY(0).setDuration(1000).setStartDelay(500);
            }
        });*/

        return root;

    }



    private void EventChangeListener() {

        //Query query = mFireStore.whereEqualTo("doc_telefono",Phone).orderBy("doc_fecha",Query.Direction.DESCENDING);
        mFireStore.collection("platillo").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        orderArrayList.add(dc.getDocument().toObject(Platillo.class));
                        Collections.sort(orderArrayList, new Comparator<Platillo>() {
                            @Override
                            public int compare(Platillo platillo, Platillo t1) {
                                return t1.getTitulo().compareTo(platillo.getTitulo());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

   /* private void EventPendiente() {

        mFireStore.collection("orders").whereEqualTo("estado", "pendiente").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                return t1.getTime().compareTo(orderListModel.getTime());
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
        mFireStore.collection("orders").whereEqualTo("estado", "entregado").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                return t1.getTime().compareTo(orderListModel.getTime());
                            }
                        });
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }*/
}
