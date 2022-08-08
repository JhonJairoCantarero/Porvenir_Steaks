package com.example.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Time;
import java.util.Date;
import java.util.Hashtable;

public class ratingbar extends AppCompatActivity {

    TextView tvFeedback;
    EditText comentario;
    RatingBar rbStars;
    Button calificar;
    String cliente, estado;
    Long fecha;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratingbar);

        tvFeedback = findViewById(R.id.tvFeedback);
        comentario = findViewById(R.id.txtComentario);
        rbStars = (RatingBar) findViewById(R.id.rbStars);
        calificar = findViewById(R.id.btnSend);
        Bundle b = getIntent().getExtras();
        cliente = b.getString("cliente");
        estado = b.getString("estado");
        fecha = b.getLong("fecha");
        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==0)
                {
                    tvFeedback.setText("Mal Servicio");
                }
                else if(rating==1)
                {
                    tvFeedback.setText("Mas o menos");
                }
                else if(rating==2 || rating==3)
                {
                    tvFeedback.setText("Bueno");
                }
                else if(rating==4)
                {
                    tvFeedback.setText("Muy Bueno");
                }
                else if(rating==5)
                {
                    tvFeedback.setText("Exelente Servicio");
                }
                else
                {

                }
            }
        });

        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerpedido();
            }
        });
    }

    private void obtenerpedido() {
        db.collection("pedidos")
                .whereEqualTo("cliente", cliente)
                .whereEqualTo("estado", estado)
                .whereEqualTo("fecha", new Timestamp(new Date(fecha)))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc: task.getResult())
                            {
                                Hashtable<String, Object> val = new Hashtable<>();
                                val.put("calificacion", tvFeedback.getText().toString());
                                val.put("comentario", comentario.getText().toString());
                                doc.getReference().update(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(ratingbar.this, "Pedido Calificado Correctamente", Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(ratingbar.this, "No se pudo calificar el pedido", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                break;
                            }
                        }
                        else
                        {
                            Toast.makeText(ratingbar.this, "No se pudo obtener el pedido", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}