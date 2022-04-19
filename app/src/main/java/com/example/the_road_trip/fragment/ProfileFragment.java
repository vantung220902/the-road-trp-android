package com.example.the_road_trip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.FirstInstallActivity;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.adapter.ProfilePostAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private RecyclerView rcvPots;
    private ProfilePostAdapter postAdapter;
    private List<Post> listPost;
    private CircleImageView avatar;
    private TextView txtName, txtAddress;
private ImageButton btnLogout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        setInfo();
        postAdapter = new ProfilePostAdapter(getContext(), listPost);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcvPots.setLayoutManager(staggeredGridLayoutManager);
        rcvPots.setAdapter(postAdapter);
        loadPosts();
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
        return view;
    }

    private void initUI(View view) {
        rcvPots = view.findViewById(R.id.rcv_profile_post_lists);
        avatar = view.findViewById(R.id.profile_avatar);
        txtAddress = view.findViewById(R.id.profile_address);
        txtName = view.findViewById(R.id.profile_name);
        btnLogout = view.findViewById(R.id.logout_profile);
    }

    private void setInfo() {
        Glide.with(getContext()).load(DataLocalManager.getUserCurrent().getAvatar_url())
                .centerCrop()
                .into(avatar);
        txtName.setText(DataLocalManager.getUserCurrent().getFullName());
        txtAddress.setText(DataLocalManager.getUserCurrent().getAddress());
    }

    public void loadPosts() {
        APIPost.apiPOST.getById(DataLocalManager.getUserCurrent().get_id()).enqueue(new Callback<ResponsePost>() {
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
}