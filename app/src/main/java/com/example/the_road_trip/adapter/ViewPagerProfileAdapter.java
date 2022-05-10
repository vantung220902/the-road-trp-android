package com.example.the_road_trip.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.the_road_trip.fragment.RecPostFragment;
import com.example.the_road_trip.fragment.ShoppingFragment;

public class ViewPagerProfileAdapter extends FragmentStateAdapter {
    private ProfilePostAdapter postAdapter;
    public ViewPagerProfileAdapter(@NonNull FragmentActivity fragmentActivity,ProfilePostAdapter postAdapter) {
        super(fragmentActivity);
        this.postAdapter = postAdapter;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecPostFragment(postAdapter);
            case 1:
                return new ShoppingFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
