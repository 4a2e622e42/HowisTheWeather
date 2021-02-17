package com.example.weather5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity2 extends AppCompatActivity {
    LottieAnimationView location, check_box;
    TextView found;
    Handler handler = new Handler();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        location = findViewById(R.id.location);
        check_box = findViewById(R.id.check_box);
        found = findViewById(R.id.found);




        location.playAnimation();

        handler.postDelayed(animations,5000);
        handler.postDelayed(nextPagr,7000);




    }

    protected Runnable animations = new Runnable() {
        @Override
        public void run()
        {
            location.setVisibility(View.INVISIBLE);
            check_box.playAnimation();
            found.setText("پیدات کردم :)");

        }
    };
    protected Runnable nextPagr = new Runnable() {
        @Override
        public void run()
        {
            Intent weather_page = new Intent(MainActivity2.this,Weather.class);
            weather_page.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            startActivity(weather_page);
            overridePendingTransition(0, 0);
        }
    };


}