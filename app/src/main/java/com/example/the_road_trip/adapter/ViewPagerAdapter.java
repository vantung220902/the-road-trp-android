package com.example.the_road_trip.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.the_road_trip.fragment.AnnouncementFragment;
import com.example.the_road_trip.fragment.CreatePostFragment;
import com.example.the_road_trip.fragment.DiscoverFragment;
import com.example.the_road_trip.fragment.ProfileFragment;
import com.example.the_road_trip.fragment.ShoppingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DiscoverFragment();
            case 1:
                return new ShoppingFragment();
            case 2:
                return new CreatePostFragment();
            case 3:
                return new AnnouncementFragment();
            case 4:
                return  new ProfileFragment();

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
