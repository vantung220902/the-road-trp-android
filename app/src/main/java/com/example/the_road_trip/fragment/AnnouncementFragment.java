package com.example.the_road_trip.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ChatAdapter;
import com.example.the_road_trip.adapter.InvitingAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.ApiFriend;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Friend.Inviting;
import com.example.the_road_trip.model.Friend.ResponseInviting;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnnouncementFragment extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView rcvInviting;
    private ProgressBar progressBar;
    private InvitingAdapter invitingAdapter;
    private List<Inviting> list = new ArrayList<>();
    private TextView tvEmpty;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constant.URL_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragement_annoucement, container, false);
        initUI(view);
        mSocket.connect();
        mSocket.emit("join_room", "Inviting");
        mSocket.on("receive_inviting", onNewInviting);
        loadInviting();
        return view;
    }

    private Emitter.Listener onNewInviting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadInviting();
                    tvEmpty.setVisibility(View.GONE);
                }
            });
        }
    };

    private void initUI(View view) {
        nestedScrollView = view.findViewById(R.id.idNestedSVAnnouncement);
        rcvInviting = view.findViewById(R.id.list_inviting_friend);
        progressBar = view.findViewById(R.id.idPBLoadingAnnouncement);
        tvEmpty = view.findViewById(R.id.announcement_empty);
        invitingAdapter = new InvitingAdapter(list, getContext(), new InvitingAdapter.ICLickInviting() {
            @Override
            public void actions(String _id, int status) {
                Actions(_id, status);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvInviting.setLayoutManager(linearLayoutManager);
        rcvInviting.setAdapter(invitingAdapter);
    }

    public void loadInviting() {
        ApiFriend.apiFriend.gets().enqueue(new Callback<ResponseInviting>() {
            @Override
            public void onResponse(Call<ResponseInviting> call, Response<ResponseInviting> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        invitingAdapter.setData(list);
                        if (list.size() == 0 || list == null) tvEmpty.setVisibility(View.VISIBLE);
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
            public void onFailure(Call<ResponseInviting> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                SnackbarCustomer(t.getMessage());
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void Actions(String _id, int status) {
        ApiFriend.apiFriend.actionInviting(_id, status).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                try {
                    if (response.code() == 200) {
                        for (Inviting inviting : list) {
                            if (inviting.get_id().equals(_id)) {
                                list.remove(inviting);
                                invitingAdapter.setData(list);
                            }
                        }

                        Snackbar.make(getView().getRootView(),
                                response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
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

            }
        });
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
                        loadInviting();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}