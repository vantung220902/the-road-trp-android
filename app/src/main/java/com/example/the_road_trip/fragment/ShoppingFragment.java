package com.example.the_road_trip.fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.TicketAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.ApiTicket;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.Ticket.ResponseTicket;
import com.example.the_road_trip.model.Ticket.Ticket;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShoppingFragment extends Fragment {
    private RecyclerView rcv;
    private TicketAdapter ticketAdapter;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private List<Ticket> list = new ArrayList<>();
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        initUI(view);
        loadTickets();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++;
                    // on below line we are making our progress bar visible.
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreTickets("", page);
                        }
                    }, 1000);

                }
            }
        });
        return view;
    }

    private void loadTickets() {
        ApiTicket.apiTicket.gets("", 0).enqueue(new Callback<ResponseTicket>() {
            @Override
            public void onResponse(Call<ResponseTicket> call, Response<ResponseTicket> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        ticketAdapter.setData(list);
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
            public void onFailure(Call<ResponseTicket> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public void loadMoreTickets(String query, int page) {
        ApiTicket.apiTicket.gets(query, page).enqueue(new Callback<ResponseTicket>() {
            @Override
            public void onResponse(Call<ResponseTicket> call, Response<ResponseTicket> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        if (list.size() == 0) {
                            Snackbar snackbar = Snackbar
                                    .make(getView().getRootView(), "Don't have any ticket",
                                            Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        ticketAdapter.loadMore(list);
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
            public void onFailure(Call<ResponseTicket> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
        progressBar.setVisibility(View.GONE);
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
                        loadTickets();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    private void initUI(View view) {
        rcv = view.findViewById(R.id.rcv_shopping);
        nestedScrollView = view.findViewById(R.id.idNestedSVShopping);
        progressBar = view.findViewById(R.id.idPBLoadingTicket);
        ticketAdapter = new TicketAdapter(list, getContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        rcv.setLayoutManager(mLayoutManager);
        rcv.setAdapter(ticketAdapter);
    }
}