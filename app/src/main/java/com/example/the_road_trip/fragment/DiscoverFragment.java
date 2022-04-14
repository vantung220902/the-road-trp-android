package com.example.the_road_trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.story.CreateStoryActivity;
import com.example.the_road_trip.activity.story.DetailStoryActivity;
import com.example.the_road_trip.adapter.PostAdapter;
import com.example.the_road_trip.adapter.StoryAdapter;
import com.example.the_road_trip.animation.ExpandedCollapse;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.APIStory;
import com.example.the_road_trip.interfaces.IClickStory;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.Story.ResponseStory;
import com.example.the_road_trip.model.Story.Story;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private StoryAdapter storyAdapter;
    private List<Post> listPost;
    private List<Story> listStory;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout containerStory, headingDiscovery;
    private TextView tvLogo, tvName;
    private ImageButton addStory;
    private static int y;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initUI(view);
        tvName.setText(DataLocalManager.getUserCurrent().getFullName());
        swipeRefreshLayout.setOnRefreshListener(this);
        postAdapter = new PostAdapter(listPost, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewPost.setLayoutManager(linearLayoutManager);
        recyclerViewPost.setAdapter(postAdapter);
        storyAdapter = new StoryAdapter(listStory, getContext(), new IClickStory() {
            @Override
            public void getStory(String id) {
                Intent intent = new Intent(getContext(), DetailStoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LIST_STORY", (Serializable) listStory);
                bundle.putString("ID_STORY", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager2);
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setAdapter(storyAdapter);
        loadPosts();
        loadStories();
        recyclerViewPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // super.onScrolled(recyclerView, dx, dy);
                y = dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerViewPost.SCROLL_STATE_DRAGGING == newState) {
                    headingDiscovery.setVisibility(View.GONE);
                    ExpandedCollapse.collapse(containerStory);
                    tvLogo.setVisibility(View.VISIBLE);
                } else if (recyclerViewPost.SCROLL_STATE_IDLE == newState) {
                    if (y <= -5) {
                        ExpandedCollapse.expand(containerStory);
                        headingDiscovery.setVisibility(View.VISIBLE);
                        tvLogo.setVisibility(View.GONE);
                    } else {
                        y = 0;
                        ExpandedCollapse.collapse(containerStory);
                        headingDiscovery.setVisibility(View.GONE);
                        tvLogo.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        addStory.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), CreateStoryActivity.class);
            startActivity(intent);
        });
        return view;

    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rcv_story);
        recyclerViewPost = view.findViewById(R.id.rcv_posts);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        containerStory = view.findViewById(R.id.container_story);
        headingDiscovery = view.findViewById(R.id.heading_discovery);
        tvLogo = view.findViewById(R.id.tv_logo);
        tvName = view.findViewById(R.id.tv_display_name);
        addStory = view.findViewById(R.id.add_story);
    }

    public void loadPosts() {
        APIPost.apiPOST.gets("", 0).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        postAdapter.setData(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            Toast.makeText(getContext(), internalMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
            }
        });
    }

    public void loadStories() {
        APIStory.apiStory.gets().enqueue(new Callback<ResponseStory>() {
            @Override
            public void onResponse(Call<ResponseStory> call, Response<ResponseStory> response) {
                try {
                    if (response.code() == 200) {
                        listStory = response.body().getData();
                        storyAdapter.setData(listStory);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            Toast.makeText(getContext(), internalMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseStory> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        loadPosts();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExpandedCollapse.expand(containerStory);
                headingDiscovery.setVisibility(View.VISIBLE);
                tvLogo.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

}
