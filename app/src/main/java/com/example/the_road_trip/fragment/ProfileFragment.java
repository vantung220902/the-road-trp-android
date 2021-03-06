package com.example.the_road_trip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.FirstInstallActivity;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.activity.auth.UpdateUserActivity;
import com.example.the_road_trip.activity.post.ManagePostActivity;
import com.example.the_road_trip.activity.ticket.ManagePaymentActivity;
import com.example.the_road_trip.adapter.ProfilePostAdapter;
import com.example.the_road_trip.adapter.ViewPagerProfileAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.example.the_road_trip.utils.DisplayImageActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private ProfilePostAdapter postAdapter;
    private List<Post> listPost;
    private CircleImageView avatar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerProfileAdapter viewPagerProfileAdapter;
    private TextView txtName, txtAddress, tvNumberPost;
    private ImageButton btnLogout;
    private MaterialButton btnEditProfile,btn_event;
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private MaterialButton btnManagePost;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        setInfo();
        btnLogout.setOnClickListener(view1 -> {
            DataLocalManager.setRefreshToken("");
            DataLocalManager.setAccessToken("");
            DataLocalManager.setUserCurrent(null);
            Intent intent = new Intent(getContext(), SplashActivity.class);
            Bundle bundle = new Bundle();
            ModelLink modelLink = new ModelLink(null, FirstInstallActivity.class);
            bundle.putSerializable("screen_next", modelLink);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().finish();
        });
        btnEditProfile.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), UpdateUserActivity.class);
            getContext().startActivity(intent);
        });
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
        loadPosts();
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
                            loadMorePosts("", page);
                        }
                    }, 1000);

                }
            }
        });
        avatar.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), DisplayImageActivity.class);
            intent.putExtra("image", DataLocalManager.getUserCurrent().getAvatar_url());
            startActivity(intent);
        });
        btnManagePost.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ManagePostActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        btn_event.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), ManagePaymentActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        return view;
    }

    private void initUI(View view) {
        avatar = view.findViewById(R.id.profile_avatar);
        txtAddress = view.findViewById(R.id.profile_address);
        txtName = view.findViewById(R.id.profile_name);
        btnLogout = view.findViewById(R.id.logout_profile);
        btn_event = view.findViewById(R.id.btn_event);
        btnEditProfile = view.findViewById(R.id.edit_profile);
        tabLayout = view.findViewById(R.id.tab_layout_profile);
        viewPager2 = view.findViewById(R.id.view_pager_profile);
        nestedSV = view.findViewById(R.id.idNestedSVProfile);
        loadingPB = view.findViewById(R.id.idPBLoadingProfile);
        tvNumberPost = view.findViewById(R.id.profile_number_pots);
        btnManagePost = view.findViewById(R.id.btn_manage_post);
        postAdapter = new ProfilePostAdapter(getContext(), listPost);
        viewPagerProfileAdapter = new ViewPagerProfileAdapter(getActivity(), postAdapter);
        viewPager2.setAdapter(viewPagerProfileAdapter);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.grid));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.event));
    }

    private void setInfo() {
        Glide.with(getContext()).load(DataLocalManager.getUserCurrent().getAvatar_url())
                .centerCrop()
                .into(avatar);
        txtName.setText(DataLocalManager.getUserCurrent().getFullName());
        txtAddress.setText(DataLocalManager.getUserCurrent().getAddress());
    }

    public void loadPosts() {
        APIPost.apiPOST.getById("", 0, false).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        postAdapter.setData(listPost);
                        tvNumberPost.setText(listPost.size() + "");
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

    public void loadMorePosts(String query, int page) {
        APIPost.apiPOST.gets(query, page).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        if (listPost.size() == 0) {
                            Snackbar snackbar = Snackbar
                                    .make(getView().getRootView(), "Don't have any post",
                                            Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        postAdapter.loadMore(listPost);
                        int number = listPost.size() + Integer
                                .parseInt(tvNumberPost.getText().toString());
                        tvNumberPost.setText(number + "");
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

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(getView().getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(getView().getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPosts();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}