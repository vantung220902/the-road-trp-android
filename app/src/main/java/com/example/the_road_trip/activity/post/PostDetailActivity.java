package com.example.the_road_trip.activity.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.bottomsheet.BottomSheetComment;
import com.example.the_road_trip.interfaces.IClickPostComment;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseComment;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.utils.DisplayImageActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {
    private ImageView imageAuthor, imagePost;
    private ImageButton btnBack;
    private TextView txtName, txtTime, tvTitle;
    private ImageButton btnComment, btnHeart;
    private boolean checked = true;
    private List<Comment> listComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        if (getIntent().hasExtra("post_item")) {
            initUI();
            Post post = (Post) getIntent().getExtras().get("post_item");
            Glide.with(this).load(post.getImage())
                    .centerCrop()
                    .into(imagePost);
            Glide.with(this).load(post.getUserId().getAvatar_url())
                    .centerCrop()
                    .into(imageAuthor);
            txtName.setText(post.getUserId().getFullName());
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int day = calendar.get(Calendar.DATE);
            String strTime = day == post.getTime_created() ? "Today" : day - post.getTime_created() + " ago";
            txtTime.setText(strTime);
            tvTitle.setText(post.getTitle());
            btnComment.setOnClickListener(view -> {
                clickOpenBottomSheetFragment(listComments, post.get_id());
            });
            btnHeart.setOnClickListener(view -> {
                if (checked)
                    btnHeart.setImageResource(R.drawable.heart_checked);
                else
                    btnHeart.setImageResource(R.drawable.heart);
                checked = !checked;
            });
            imagePost.setOnClickListener(view -> {
                Intent intent = new Intent(this, DisplayImageActivity.class);
                intent.putExtra("image", post.getImage());
                startActivity(intent);
            });
            btnBack.setOnClickListener(view -> {
                finish();
            });
        }

    }

    private void initUI() {
        imagePost = findViewById(R.id.img_post_detail);
        imageAuthor = findViewById(R.id.image_author_post_detail);
        btnHeart = findViewById(R.id.heart_post_detail);
        btnComment = findViewById(R.id.comment_post_detail);
        txtName = findViewById(R.id.name_author_post_detail);
        txtTime = findViewById(R.id.time_create_post_detail);
        tvTitle = findViewById(R.id.post_title_detail);
        btnBack = findViewById(R.id.btn_back_post_detail);
    }

    private void clickOpenBottomSheetFragment(List<Comment> list, String postId) {
        BottomSheetComment bottomSheetComment = new BottomSheetComment(list, postId);
        bottomSheetComment.show(getSupportFragmentManager(), bottomSheetComment.getTag());
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
                            SnackbarCustomer(internalMessage, postId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    SnackbarCustomer(e.getMessage(), postId);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseComment> call, Throwable t) {
                SnackbarCustomer(t.getMessage(), postId);
                Log.d("Error Call Api", t.getMessage());
            }
        });
    }

    private void SnackbarCustomer(String str, String postId) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content).getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadComments(postId);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}