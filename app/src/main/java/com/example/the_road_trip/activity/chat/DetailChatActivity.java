package com.example.the_road_trip.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ChatAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.ApiChat;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.api.Constant;
import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Chat.MessageModel;
import com.example.the_road_trip.model.Chat.ResponseChat;
import com.example.the_road_trip.model.Chat.ResponseInsertChat;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseInsertComment;
import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

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

public class DetailChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> messagesList = new ArrayList<>();
    private EditText editMessage;
    private MaterialButton btnSubmit;
    private ProgressBar loadingPB;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constant.URL_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);
        User receiver = (User) getIntent().getExtras().get("obj_receiver");
        adapter = new ChatAdapter(this, messagesList,receiver);
        initUI();
        loadChats(receiver.get_id());
        mSocket.connect();
        mSocket.emit("join_room","Message");
        mSocket.on("receive_message", onNewMessage);
        btnSubmit.setOnClickListener(view -> {
            String body = editMessage.getText().toString();
            addMessage(body, receiver.get_id());
            mSocket.emit("send_message", body);
        });

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message = data.optString("data");
                    User receiver = new User(DataLocalManager.getUserCurrent().get_id()
                            , DataLocalManager.getUserCurrent().getFullName(),
                            DataLocalManager.getUserCurrent().getAvatar_url());
                    Chat chat = new Chat(message, receiver, 12);
                    chat.setMessageType(ChatAdapter.MESSAGE_TYPE_IN);
                    messagesList.add(chat);
                    adapter.setData(messagesList);
                }
            });
        }
    };

    private void initUI() {
        recyclerView = findViewById(R.id.rcv_messages);
        editMessage = findViewById(R.id.edit_add_message);
        btnSubmit = findViewById(R.id.btn_submit_message);
        loadingPB = findViewById(R.id.idPBLoadingProfileChats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void addMessage(String body, String receiver) {
        ApiChat.apiChat.insertChat(body, receiver).enqueue(new Callback<ResponseInsertChat>() {
            @Override
            public void onResponse(Call<ResponseInsertChat> call, Response<ResponseInsertChat> response) {
                if (response.code() == 200) {
                    User sender = response.body().getData().getSender();
                    User receiver = response.body().getData().getReceiver();
                    Chat chat = new Chat(response.body().getData().get_id(),
                            response.body().getData().getTime_created(), sender,
                            receiver, body, ChatAdapter.MESSAGE_TYPE_OUT);
                    messagesList.add(chat);
                    adapter.setData(messagesList);
                    editMessage.setText("");
                } else {
                    Log.d("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertChat> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }

    public void loadChats(String receiver) {
        ApiChat.apiChat.gets(receiver).enqueue(new Callback<ResponseChat>() {
            @Override
            public void onResponse(Call<ResponseChat> call, Response<ResponseChat> response) {
                try {
                    if (response.code() == 200) {
                        List<Chat> list = response.body().getData();
                        for (int i = 0; i < list.size(); i++) {
                            Log.d("Item", i + "");
                            if (list.get(i).getSender().get_id().equals(DataLocalManager.getUserCurrent().get_id())) {
                                list.get(i).setMessageType(ChatAdapter.MESSAGE_TYPE_OUT);
                            } else {
                                list.get(i).setMessageType(ChatAdapter.MESSAGE_TYPE_IN);
                            }
                            messagesList.add(list.get(i));
                        }
                        adapter.setData(messagesList);
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
            public void onFailure(Call<ResponseChat> call, Throwable t) {
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
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

}