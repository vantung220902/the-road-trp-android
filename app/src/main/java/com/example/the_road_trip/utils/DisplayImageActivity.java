package com.example.the_road_trip.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.animation.TouchImageView;

public class DisplayImageActivity extends AppCompatActivity {
    private TouchImageView imageView;
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        if (getIntent().hasExtra("image")) {
            imageView = findViewById(R.id.img_display_detail);
            btnClose = findViewById(R.id.btn_close_display_image);
            Glide.with(this).load(getIntent().getStringExtra("image"))
                    .into(imageView);
            btnClose.setOnClickListener(view -> {
                finish();
            });
        }
    }
}