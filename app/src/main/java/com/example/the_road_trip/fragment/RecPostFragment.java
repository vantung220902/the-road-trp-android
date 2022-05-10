package com.example.the_road_trip.fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ProfilePostAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecPostFragment extends Fragment {
    private RecyclerView rcvPots;
    private ProfilePostAdapter postAdapter;

    public RecPostFragment(ProfilePostAdapter postAdapter) {
        this.postAdapter = postAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rec_post, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        rcvPots = view.findViewById(R.id.rcv_post_images);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcvPots.setLayoutManager(staggeredGridLayoutManager);
        rcvPots.setAdapter(postAdapter);
    }


}