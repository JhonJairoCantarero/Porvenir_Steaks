package com.example.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.home.FragmentIntro.OnBoardingFragment1;
import com.example.home.FragmentIntro.OnBoardingFragment2;
import com.example.home.FragmentIntro.OnBoardingFragment3;

public class Intro extends AppCompatActivity {
    ImageView logo,splashImg;
    TextView appName;
    CardView cardView;
    MediaPlayer mediaPlayer;
    Animation anim;
        private static final int NUM_PAGES = 3;
        private ViewPager viewPager;
        private ScreenSlidePagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Inicializando elementos del Splash
        logo = findViewById(R.id.logo);
        splashImg = findViewById(R.id.img);
        appName = findViewById(R.id.app_name);
        cardView = findViewById(R.id.lotiecontainer);
        mediaPlayer = MediaPlayer.create(this,R.raw.sound2);
        mediaPlayer.start();

        //Cargando los elementos de SlidePager
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //Animacion del ViewPager
        anim = AnimationUtils.loadAnimation(this,R.anim.o_b_anim);
        viewPager.startAnimation(anim);

        //Animacion al Spash
        splashImg.animate().translationY(-2700).setDuration(1000).setStartDelay(5000);
        logo.animate().translationY(2200).setDuration(1000).setStartDelay(5000);
        appName.animate().translationY(2200).setDuration(1000).setStartDelay(5000);
        cardView.animate().translationY(2200).setDuration(1000).setStartDelay(5000);




    }
    private class  ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    OnBoardingFragment1 tab1 = new OnBoardingFragment1();
                    return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}