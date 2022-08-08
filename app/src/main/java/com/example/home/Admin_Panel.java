package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.home.adminpanel.AdminOrdersListRealizadosFragment;
import com.example.home.adminpanel.AdminPerfilFragment;
import com.example.home.adminpanel.Repartidor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Admin_Panel extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        BottomNavigationView navigationView = findViewById(R.id.admin_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()){
            case R.id.Ordenes:
                fragment = new AdminOrdersListRealizadosFragment();
                Bundle b = new Bundle();
                fragment.setArguments(b);
                break;
            case R.id.Platos:
                fragment = new AdminPerfilFragment();
                break;
            case R.id.Crear_Repartidor:
                fragment = new Repartidor();
                break;
            case R.id.Cerrar_Sesion1:
                cambiarEstado();
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();
                break;
        }
        return loadadminfragment (fragment);
    }


    private boolean loadadminfragment(Fragment fragment) {

        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
        return false;
    }

    public void cambiarEstado() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = false;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_1", estado3);
        editor3.commit();

    }
}