package com.example.the_road_trip.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.auth.LoginActivity;
import com.example.the_road_trip.adapter.PostAdapter;
import com.example.the_road_trip.adapter.ViewPagerAdapter;
import com.example.the_road_trip.fragment.CreatePostFragment;
import com.example.the_road_trip.fragment.DiscoverFragment;
import com.example.the_road_trip.model.ModelLink;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements CreatePostFragment.IUpdatePosts {
    private final int ID_DISCOVER = 0;
    private final int ID_STORE = 1;
    private final int ID_ADD_NEWS = 2;
    private final int ID_MESSAGE = 3;
    private final int ID_PERSON = 4;
    private MeowBottomNavigation meowBottomNavigation;
    private ViewPager2 viewPager;
    private long backPressTime;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String announcement = getIntent().getStringExtra("announcement");
        if (announcement != null) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content).getRootView(), announcement, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        initUI();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showBottomNavigation(position);
            }
        });
        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                viewPager.setCurrentItem(item.getId());
            }
        });
        meowBottomNavigation.setOnShowListener(item -> {
            viewPager.setCurrentItem(item.getId());
        });

    }

    private void initUI() {
        meowBottomNavigation = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewPagerMain);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        if (getIntent().hasExtra("location")) {
            int location = Integer.parseInt(getIntent().getStringExtra("location"));
            viewPager.setCurrentItem(location);
            meowBottomNavigation.show(location, true);
        } else {
            viewPager.setCurrentItem(0);
            meowBottomNavigation.show(0, true);
        }
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_DISCOVER, R.drawable.home));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_STORE, R.drawable.shopping));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_ADD_NEWS, R.drawable.add_news));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.bell));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_PERSON, R.drawable.person));
    }


    private void showBottomNavigation(int id) {
        meowBottomNavigation.show(id, true);
    }

    public ViewPager2 getViewPager() {
        return this.viewPager;
    }

    @Override
    public void updateData() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        Bundle bundle = new Bundle();
        ModelLink modelLink = new ModelLink(null, MainActivity.class);
        bundle.putSerializable("screen_next", modelLink);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            mToast.cancel();
            super.onBackPressed();
        } else {
            mToast = Toast.makeText(MainActivity.this, "Press Back To Exit", Toast.LENGTH_SHORT);
            mToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }

}