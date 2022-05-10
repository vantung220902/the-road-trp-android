package com.example.the_road_trip.activity.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ProfilePostAdapter;
import com.example.the_road_trip.adapter.ViewPagerProfileAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.ApiFriend;
import com.example.the_road_trip.model.Friend.Friend;
import com.example.the_road_trip.model.Friend.StatusFriend;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.Story.ResponseStory;
import com.example.the_road_trip.model.User.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnotherActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerProfileAdapter viewPagerProfileAdapter;
    private CircleImageView avatar;
    private TextView tvAddress, tvFullName, tvPosts;
    private ProfilePostAdapter postAdapter;
    private MaterialButton btnAddFriend;
    private List<Post> listPost;
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private int page = 0;
    private User user;
    private int status = -1;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        user = (User) getIntent().getExtras().get("user_item");
        initUI();
        getStatus();
        loadPosts();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
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
                            loadMorePosts(page);
                        }
                    }, 1000);

                }
            }
        });
        btnAddFriend.setOnClickListener(view -> {
            actionInviting(user.get_id(), id);
        });
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tab_layout_another);
        viewPager2 = findViewById(R.id.view_pager_another);
        avatar = findViewById(R.id.another_avatar);
        nestedSV = findViewById(R.id.idNestedSVPost);
        loadingPB = findViewById(R.id.idPBLoadingRcvPost);
        tvAddress = findViewById(R.id.another_address);
        tvFullName = findViewById(R.id.another_name);
        tvPosts = findViewById(R.id.another_number_pots);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        Glide.with(this).load(user.getAvatar_url())
                .centerCrop()
                .into(avatar);
        tvAddress.setText(user.getAddress());
        tvFullName.setText(user.getFullName());
        postAdapter = new ProfilePostAdapter(this, listPost);
        viewPagerProfileAdapter = new ViewPagerProfileAdapter(this, postAdapter);
        viewPager2.setAdapter(viewPagerProfileAdapter);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.grid));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.event));

    }

    public void loadPosts() {
        APIPost.apiPOST.getPostUser(user.get_id(), 0).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        tvPosts.setText(listPost.size() + "");
                        postAdapter.setData(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
        loadingPB.setVisibility(View.GONE);
    }

    public void loadMorePosts(int page) {
        APIPost.apiPOST.getPostUser(user.get_id(), page).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        if (listPost.size() == 0) {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(android.R.id.content), "Load More",
                                            Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        postAdapter.loadMore(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
        loadingPB.setVisibility(View.GONE);
    }

    private void getStatus() {
        ApiFriend.apiFriend.status(user.get_id()).enqueue(new Callback<StatusFriend>() {
            @Override
            public void onResponse(Call<StatusFriend> call, Response<StatusFriend> response) {
                try {
                    if (response.code() == 200) {
                        Friend friend = response.body().getData();
                        if (friend != null) {
                            status = friend.getStatus();
                            changeBtnFriend(status);
                            id = friend.get_id();
                        }
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<StatusFriend> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
    }

    private void actionInviting(String receiver, String id) {
        ApiFriend.apiFriend.action(receiver, id, status).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                try {
                    if (response.code() == 200) {
                        status++;
                        if (status >= 1) status = -1;
                        changeBtnFriend(status);
                        SnackbarCustomer(response.body().getMessage());
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
    }

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPosts();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    private void changeBtnFriend(int status) {
        switch (status) {
            case -1:
                btnAddFriend.setText(R.string.add_friend);
                btnAddFriend.setIcon(getDrawable(R.drawable.add_friend));
                break;
            case 0:
                btnAddFriend.setText(R.string.remove_invite);
                btnAddFriend.setIcon(getDrawable(R.drawable.invite));
                break;
            case 1:
                btnAddFriend.setText(R.string.remove_friend);
                btnAddFriend.setIcon(getDrawable(R.drawable.close));
                break;
        }
    }
}