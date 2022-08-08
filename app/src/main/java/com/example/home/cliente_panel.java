package com.example.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.home.adminpanel.AdminOrdersListRealizadosFragment;
import com.example.home.clientepanel.ClienteHomeFragment;
import com.example.home.clientepanel.PerfilFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Hashtable;

public class cliente_panel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    FloatingActionButton map;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_panel);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        map = (FloatingActionButton) findViewById(R.id.geo);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hashtable<String,Object> cliente = new Hashtable<>();
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
                                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                        i.putExtra("cliente", cliente.get("identidad").toString());
                                        startActivity(i);
                                    }
                                }
                            }
                        });

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()){
            case R.id.cliente_home:
                fragment = new ClienteHomeFragment();
                break;
            case R.id.cliente_Menu:
                String cliente = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                fragment = new AdminOrdersListRealizadosFragment();
                Bundle b = new Bundle();
                b.putString("cliente", cliente);
                fragment.setArguments(b);
                break;
            case R.id.cliente_perfil:
                fragment = new PerfilFragment();
                break;
    }
        return loadclientefragment (fragment);
}

    private boolean loadclientefragment(Fragment fragment) {
        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
            return false;
    }
}