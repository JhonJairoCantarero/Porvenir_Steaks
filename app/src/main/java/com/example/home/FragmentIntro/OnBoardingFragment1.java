package com.example.home.FragmentIntro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.home.Admin_Panel;
import com.example.home.MainActivity;
import com.example.home.R;
import com.example.home.cliente_panel;
import com.example.home.delivery_panel;


public class OnBoardingFragment1 extends Fragment {

    TextView skip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding1,container,false);
        skip = root.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences3 = getContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                if (preferences3.getBoolean("estado_1",false)){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent login = new Intent(getContext(), Admin_Panel.class);
                            startActivity(login);
                        }
                    }, 4000);
                }else if(preferences3.getBoolean("estado_2",false)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent login = new Intent(getContext(), cliente_panel.class);
                            startActivity(login);
                        }
                    }, 4000);

                } else if(preferences3.getBoolean("estado_3",false)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent login = new Intent(getContext(), delivery_panel.class);
                            startActivity(login);
                        }
                    }, 4000);

                } else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent inicio = new Intent(getContext(), MainActivity.class);
                            startActivity(inicio);
                        }
                    }, 4000);
                }
            }
        });

        return root;
    }
}
