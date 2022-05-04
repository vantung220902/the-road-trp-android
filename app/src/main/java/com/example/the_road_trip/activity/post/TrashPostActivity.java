package com.example.the_road_trip.activity.post;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.adapter.PostManageAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.utils.RecyclerTouchListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrashPostActivity extends AppCompatActivity {
    private RecyclerView rcvPostManage;
    private List<Post> list;
    private PostManageAdapter postManageAdapter;
    private AlertDialog.Builder dialog;
    private int page = 0;
    private String query = "";
    private MaterialButton btnLoadMore;
    private ProgressBar progressBar;
    private TextInputEditText inputSearch;
    private MaterialButton btnToManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_post);
        initUI();
        RecyclerTouchListener touchListener = new RecyclerTouchListener(this, rcvPostManage);
        postManageAdapter = new PostManageAdapter(this, list);
        loadPosts(query);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Intent intent = new Intent(TrashPostActivity.this, PostDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("post_item", list.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_post, R.id.edit_post)
                .setSwipeable(R.id.row_item_post, R.id.row_actions_post, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.delete_post:
                                delete(position);
                                break;
                            case R.id.edit_post:
                                recovery(position);
                                break;
                        }
                    }
                });
        rcvPostManage.addOnItemTouchListener(touchListener);
        rcvPostManage.setAdapter(postManageAdapter);
        btnLoadMore.setOnClickListener(view -> {
            page++;
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMorePosts(query, page);
                }
            }, 500);
        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                query = inputSearch.getText().toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPosts(query);
                    }
                }, 500);
            }
        });
        btnToManage.setOnClickListener(view -> {
            Intent intent = new Intent(this, ManagePostActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUI() {
        rcvPostManage = findViewById(R.id.rcv_manage_post_trash);
        btnLoadMore = findViewById(R.id.btn_load_more_manage_trash);
        progressBar = findViewById(R.id.idPBLoadingManagePostTrash);
        inputSearch = findViewById(R.id.edit_search_manage_post_trash);
        btnToManage = findViewById(R.id.back_to_manage);
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Are you sure, This post will be deleted");
    }

    public void loadPosts(String query) {
        APIPost.apiPOST.getById(query, 0, true).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        postManageAdapter.setData(list);
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
    }

    private void delete(int position) {
        dialog.setPositiveButton("Yes", (dialogInterface, i)
                -> APIPost.apiPOST.recovery(list.get(position).get_id()).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body().getSuccessful()) {
                            list.remove(list.get(position));
                            Snackbar.make(findViewById(android.R.id.content)
                                            .getRootView(),
                                    response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                        postManageAdapter.setData(list);
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
        }));
        dialog.setNegativeButton("No", (dialogInterface, i) -> {
        });
        dialog.show();
    }
    private void recovery(int position) {
       APIPost.apiPOST.recovery(list.get(position).get_id()).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body().getSuccessful()) {
                            list.remove(list.get(position));
                            Snackbar.make(findViewById(android.R.id.content)
                                            .getRootView(),
                                    response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                        postManageAdapter.setData(list);
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
    public void loadMorePosts(String query, int page) {
        APIPost.apiPOST.getById(query, page, true).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        if (list.size() == 0) {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(android.R.id.content).getRootView()
                                                    .getRootView(), "Don't have any pots",
                                            Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        postManageAdapter.loadMore(list);
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
        progressBar.setVisibility(View.GONE);
    }

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content)
                                        .getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPosts(query);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TrashPostActivity.this, SplashActivity.class);
        Bundle bundle = new Bundle();
        ModelLink modelLink = new ModelLink(null, MainActivity.class);
        bundle.putSerializable("screen_next", modelLink);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}