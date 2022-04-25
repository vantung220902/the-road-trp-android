package com.example.the_road_trip.fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ProfilePostAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private EditText editSearch;
    private RecyclerView rcvResult;
    private List<Post> listPost;
    private ProfilePostAdapter profilePostAdapter;
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initUI(view);
        profilePostAdapter = new ProfilePostAdapter(getContext(), listPost);
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
                    }, 500);

                }
            }
        });
        return view;
    }

    private void initUI(View view) {
        editSearch = view.findViewById(R.id.edit_search_post);
        rcvResult = view.findViewById(R.id.rcv_search_post);
        nestedSV = view.findViewById(R.id.idNestedSVSearch);
        loadingPB = view.findViewById(R.id.idPBLoadingSearch);
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
    }

    public void loadMorePosts(String query, int page) {
        APIPost.apiPOST.gets(query, page).enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                try {
                    if (response.code() == 200) {
                        listPost = response.body().getData();
                        if (listPost.size() == 0) {
                            loadingPB.setVisibility(View.GONE);
                            Snackbar snackbar = Snackbar
                                    .make(getView().getRootView(), "Load More",
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
    }

    private void SnackbarCustomer(String str, String query, int page) {
        Snackbar snackbar = Snackbar
                .make(getView().getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(getView().getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPosts(query, page);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

}