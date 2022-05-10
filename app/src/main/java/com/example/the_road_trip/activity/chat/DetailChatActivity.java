package com.example.the_road_trip.activity.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.ChatAdapter;
import com.example.the_road_trip.api.ApiChat;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Chat.MessageModel;
import com.example.the_road_trip.model.Chat.ResponseInsertChat;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseInsertComment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> messagesList;
    private EditText editMessage;
    private MaterialButton btnSubmit;
    private ProgressDialog progressDialog;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);
//        for (int i = 0; i < 10; i++) {
//            if (i % 2 == 0)
//                messagesList.add(new MessageModel("Hi", ChatAdapter.MESSAGE_TYPE_IN,
//                        "https://res.cloudinary.com/the-roap-trip/image/upload/v1649301005/zvdubobjy4pmieee5w0s.jpg"));
//            else
//                messagesList.add(new MessageModel("Hello", ChatAdapter.MESSAGE_TYPE_OUT, ""));
//
//        }
        initUI();
        receiver = getIntent().getStringExtra("receiver");
        btnSubmit.setOnClickListener(view -> {
            String body = editMessage.getText().toString();
            addMessage(body, receiver);
        });
    }

    private void initUI() {
        adapter = new ChatAdapter(this, messagesList);
        recyclerView = findViewById(R.id.rcv_messages);
        editMessage = findViewById(R.id.edit_add_message);
        btnSubmit = findViewById(R.id.btn_submit_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
    }

    public void addMessage(String body, String receiver) {
        ApiChat.apiChat.insertChat(body, receiver).enqueue(new Callback<ResponseInsertChat>() {
            @Override
            public void onResponse(Call<ResponseInsertChat> call, Response<ResponseInsertChat> response) {
                if (response.code() == 200) {
                    if (response.body().getSuccessful()) {
                        Chat chat = new Chat(response.body().getData().get_id(),
                                response.body().getData().getTime_created(), response.body().getData().getSender(),
                                response.body().getData().getReceiver(), body, ChatAdapter.MESSAGE_TYPE_OUT);
                        messagesList.add(chat);
                        adapter.setData(messagesList);
                        editMessage.setText("");
                    }
                } else {
                    Log.d("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertChat> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
        progressDialog.dismiss();
    }
}