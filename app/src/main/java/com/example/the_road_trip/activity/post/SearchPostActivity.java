package com.example.the_road_trip.activity.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class SearchPostActivity extends AppCompatActivity {
    private EditText editSearch;
    private RecyclerView rcvResult;
    private List<Post> listPost;
    private ProfilePostAdapter profilePostAdapter;
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);
        initUI();
        profilePostAdapter = new ProfilePostAdapter(this, listPost);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcvResult.setLayoutManager(staggeredGridLayoutManager);
        rcvResult.setAdapter(profilePostAdapter);
        loadPosts("", 0);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPosts(editable.toString(), 0);
                    }
                }, 1000);
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
                            String str = editSearch.getText().toString() != ""
                                    ? editSearch.getText().toString() : "";
                            loadMorePosts(str, page);
                        }
                    }, 1000);

                }
            }
        });
    }
    private void initUI() {
        editSearch = findViewById(R.id.edit_search_post);
        rcvResult = findViewById(R.id.rcv_search_post);
        nestedSV = findViewById(R.id.idNestedSVSearch);
        loadingPB = findViewById(R.id.idPBLoadingSearch);
    }
    public void loadPosts(String query, int paging) {
        APIPost.apiPOST.gets(query, paging).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {

                        listPost = response.body().getData();
                        profilePostAdapter.setData(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage, query, page);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage(), query, page);
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage(), query, page);
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
                                    .make(findViewById(android.R.id.content).getRootView(), "Load More",
                                            Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        profilePostAdapter.loadMore(listPost);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage, query, page);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarCustomer(e.getMessage(), query, page);

                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage(), query, page);
            }
        });
        loadingPB.setVisibility(View.GONE);
    }

    private void SnackbarCustomer(String str, String query, int page) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content).getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPosts(query, page);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

}