package com.example.the_road_trip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.auth.LoginActivity;
import com.example.the_road_trip.activity.auth.RegisterActivity;
import com.google.android.material.button.MaterialButton;

public class FirstInstallActivity extends AppCompatActivity {
    private MaterialButton btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first_install);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(FirstInstallActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(FirstInstallActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}