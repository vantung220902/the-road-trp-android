package com.example.the_road_trip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.auth.LoginActivity;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.shared_preference.DataLocalManager;


public class SplashActivity extends AppCompatActivity {

    private ImageView splash_img;
    private TextView app_name;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initUI();
        splash_img.animate().translationY(-2500).setDuration(1000).setStartDelay(2000);
        app_name.animate().translationY(2000).setDuration(1000).setStartDelay(2000);
        lottieAnimationView.animate().translationY(1500).setDuration(1000).setStartDelay(2000);
        if (getIntent().hasExtra("screen_next")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ModelLink modelLink = (ModelLink) getIntent().getExtras().get("screen_next");
                    Intent intent = new Intent(SplashActivity.this, modelLink.getCls());
                    intent.putExtra("announcement", modelLink.getAnnouncement());
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (DataLocalManager.getFirstInstalled()) {
                        startActivity(LoginActivity.class);
                    } else {
                        startActivity(FirstInstallActivity.class);
                        DataLocalManager.setFirstInstalled(true);
                    }
                }
            }, 2000);
        }
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }

    private void initUI() {
        app_name = findViewById(R.id.app_name);
        splash_img = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);
    }
}