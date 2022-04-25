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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.story.CreateStoryActivity;
import com.example.the_road_trip.activity.story.DetailStoryActivity;
import com.example.the_road_trip.adapter.CommentAdapter;
import com.example.the_road_trip.adapter.PostAdapter;
import com.example.the_road_trip.adapter.StoryAdapter;
import com.example.the_road_trip.animation.ExpandedCollapse;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.APIStory;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.bottomsheet.BottomSheetComment;
import com.example.the_road_trip.interfaces.IClickPostComment;
import com.example.the_road_trip.interfaces.IClickStory;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseComment;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.Story.ResponseStory;
import com.example.the_road_trip.model.Story.Story;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
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
    private List<Comment> listComments;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout containerStory, headingDiscovery;
    private TextView tvLogo, tvName;
    private ImageButton addStory;
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private static int y;
    private int page = 0;
    private String postPre = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initUI(view);
        tvName.setText(DataLocalManager.getUserCurrent().getFullName());
        swipeRefreshLayout.setOnRefreshListener(this);
        postAdapter = new PostAdapter(listPost, getContext(), new IClickPostComment() {
            @Override
            public void clickComment(String postId) {
                loadComments(postId);
                postPre = postId;
            }
        });
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
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++;
                    // on below line we are making our progress bar visible.
                    loadingPB.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMorePosts();
                        }
                    }, 500);

                }
            }
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
        nestedSV = view.findViewById(R.id.idNestedSVPost);
        loadingPB = view.findViewById(R.id.idPBLoading);
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
                            SnackbarCustomer(internalMessage,0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage(),0);
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage(),0);
            }
        });
    }

    public void loadMorePosts() {
        APIPost.apiPOST.gets("", page).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        if (listPost.size() == 0) {
                            loadingPB.setVisibility(View.GONE);
                            Snackbar snackbar = Snackbar
                                    .make(getView().getRootView(), "Don't have any post",
                                            Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        postAdapter.loadMore(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage,0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage(),0);
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage(),0);
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
                            SnackbarCustomer(internalMessage,2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    SnackbarCustomer(e.getMessage(),1);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseStory> call, Throwable t) {
                SnackbarCustomer(t.getMessage(),1);
                Log.d("Error Call Api", t.getMessage());
            }
        });
    }

    public void loadComments(String postId) {
        ApiComments.apiComment.gets(postId).enqueue(new Callback<ResponseComment>() {
            @Override
            public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                try {
                    if (response.code() == 200) {
                        listComments = response.body().getData();
                        clickOpenBottomSheetFragment(listComments, postId);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage,2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    SnackbarCustomer(e.getMessage(),2);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseComment> call, Throwable t) {
                SnackbarCustomer(t.getMessage(),2);
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

    private void clickOpenBottomSheetFragment(List<Comment> list, String postId) {
        BottomSheetComment bottomSheetComment = new BottomSheetComment(list, postId);
        bottomSheetComment.show(getActivity().getSupportFragmentManager(), bottomSheetComment.getTag());
    }

    private void SnackbarCustomer(String str, int funcCall) {
        Snackbar snackbar = Snackbar
                .make(getView().getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(getView().getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        switch (funcCall) {
                            case 0:
                                loadPosts();
                                break;
                            case 1:
                                loadStories();
                                break;
                            case 2:
                                loadComments(postPre);
                                break;
                        }
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}
