package com.example.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.home.databinding.ActivityMainBinding;
import com.example.home.databinding.PerfilRBinding;
import com.example.home.login.Recuperar_Clave;
import com.example.home.login.RegistroActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //public class MainActivity extends AppCompatActivity {
    private TextView recuperarClave, ver;
    private EditText correo, contrasena;
    private FirebaseAuth mAuth;
    private Button registrarUsuario, LoginUsuario;
    private ProgressDialog mDialog;
    boolean passwordvisible;
    //final public static String REFERENCE_1 = "Users";
    DatabaseReference mRootReference;
    String as = "";
    String us = "";
    String rol = "";
    String uid = "";
    ImageView logo,splashImg;
    TextView appName;
    CardView cardView;
    MediaPlayer mediaPlayer;
    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;
   // private MainActivity.ScreenSlidePagerAdapter pagerAdapter;
    Animation anim;
    private com.example.home.databinding.ActivityMainBinding binding;


    com.example.home.databinding.ActivityMainBinding ActivityMainBinding ;

    /*
    private RequestQueue requestQueue(){
        Volley.newRequestQueue(this,getApplicationContext());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        correo = (EditText) findViewById(R.id.NombreLogin);
        contrasena = (EditText) findViewById(R.id.ClaveLogin);

        //msg = (EditText)  findViewById(R.id.msg);

        contrasena.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Rigth=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=contrasena.getRight()-contrasena.getCompoundDrawables()[Rigth].getBounds().width()){
                        int selection=contrasena.getSelectionEnd();
                        if(passwordvisible){
                            contrasena.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24, 0);
                            contrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordvisible=false;
                        }else{
                            contrasena.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24, 0);
                            contrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordvisible=true;
                        }
                        contrasena.setSelection(selection);
                        return  true;
                    }
                }

                return false;
            }
        });

        registrarUsuario = (Button) findViewById(R.id.buttonRegistroLogin);
        registrarUsuario.setOnClickListener(this);

        LoginUsuario = (Button) findViewById(R.id.buttonIngresar);
        LoginUsuario.setOnClickListener(this);

        recuperarClave = (TextView) findViewById(R.id.Recuperar_Clave);
        recuperarClave.setOnClickListener(this);

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


//        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);




       }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonIngresar:
                mDialog.setMessage("Espere Un Momento...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                userLogin();
                //mDialog.dismiss();
                break;
            case R.id.buttonRegistroLogin:
                startActivity(new Intent(this, RegistroActivity.class));
                break;
            case R.id.Recuperar_Clave:
                startActivity(new Intent(this, Recuperar_Clave.class));
                break;
        }
    }

    private void userLogin() {
        String email = correo.getText().toString().trim();
        String clave = contrasena.getText().toString().trim();
        if (email.isEmpty()) {
            correo.setError("Campo Obligatorio");
            correo.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mDialog.dismiss();
            correo.setError("Correo Incorrecto");
            correo.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            mDialog.dismiss();
            contrasena.setError("Campo Obligatorio");
            contrasena.requestFocus();
            return;
        }
        if (clave.length() < 6) {
            mDialog.dismiss();
            contrasena.setError("Contrasena Menor De 6 Caracteres");
            contrasena.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                    //FirebaseUser User = firebaseAuth.getCurrentUser();
                    if (User.isEmailVerified()) {
                        uid = task.getResult().getUser().getUid();
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference().child("Users").child(uid).child("as").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                rol = snapshot.getValue(String.class);
                                if (rol.equals("admin")) {
                                    //guardarEstado();
                                    finish();
                                    Intent in = new Intent(MainActivity.this, com.example.home.Admin_Panel.class);
                                    startActivity(in);
                                    mDialog.dismiss();
                                    guardarEstado();

                                }
                                if (rol.equals("usuario")) {
                                    Intent in = new Intent(MainActivity.this, com.example.home.cliente_panel.class);
                                    startActivity(in);
                                    mDialog.dismiss();
                                    guardarEstadoUser();
                                }
                                if (rol.equals("repartidor")) {
                                    Intent in = new Intent(MainActivity.this, delivery_panel.class);
                                    startActivity(in);
                                    mDialog.dismiss();
                                    guardarEstadoRepartidor();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        User.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Verifica Tu Correo", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Usuario o Contrasena Incorrecta", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    public void guardarEstado() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = true;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_1", estado3);
        editor3.commit();

    }

    public void guardarEstadoUser() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = true;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_2", estado3);
        editor3.commit();
    }

    public void guardarEstadoRepartidor() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = true;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_3", estado3);
        editor3.commit();

    }



}