package com.example.the_road_trip.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.FriendAdapter;
import com.example.the_road_trip.api.ApiFriend;
import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Friend.Inviting;
import com.example.the_road_trip.model.Friend.ResponseInviting;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private NestedScrollView nestedSV;
    private ProgressBar loadingPB;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private int page = 0;
    private FriendAdapter chatAdapter;
    private List<Inviting> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();
        loadChats();
    }

    private void initUI() {
        nestedSV = findViewById(R.id.idNestedSVChat);
        loadingPB = findViewById(R.id.idPBLoadingRcvChat);
        recyclerView = findViewById(R.id.rcv_chat);
        tvEmpty = findViewById(R.id.chats_empty);
        chatAdapter = new FriendAdapter(list, this, new FriendAdapter.IClickChat() {
            @Override
            public void clickChat(String receiver) {
                Intent intent = new Intent(ChatActivity.this,DetailChatActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    public void loadChats() {
        ApiFriend.apiFriend.friends().enqueue(new Callback<ResponseInviting>() {
            @Override
            public void onResponse(Call<ResponseInviting> call, Response<ResponseInviting> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        chatAdapter.setData(list);
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
        loadingPB.setVisibility(View.GONE);
    }

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content).getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadChats();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}