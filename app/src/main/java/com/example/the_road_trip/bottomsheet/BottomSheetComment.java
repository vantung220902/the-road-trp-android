package com.example.the_road_trip.bottomsheet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.CommentAdapter;
import com.example.the_road_trip.api.APIPost;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseInsertComment;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetComment extends BottomSheetDialogFragment {
    private List<Comment> list;
    private MaterialButton btnAdd;
    private EditText editComment;
    private CircleImageView avatar;
    private String postId;
    private CommentAdapter commentAdapter;
    private ProgressDialog progressDialog;

    public BottomSheetComment(List<Comment> list, String postId) {
        this.list = list;
        this.postId = postId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.comments_layout_sheet, null, false);
        bottomSheetDialog.setContentView(view);
        RecyclerView rcvData = view.findViewById(R.id.rcv_comments);
        btnAdd = view.findViewById(R.id.btn_submit_comment);
        editComment = view.findViewById(R.id.edit_add_comment);
        avatar = view.findViewById(R.id.comment_avatar_add);
        Glide.with(view).
                load(DataLocalManager.getUserCurrent().getAvatar_url())
                .into(avatar);
        btnAdd.setOnClickListener(view1 -> {
            addComment(editComment.getText().toString(), postId);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvData.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(getContext(), list);
        rcvData.setAdapter(commentAdapter);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        return bottomSheetDialog;
    }

    public void addComment(String body, String postId) {
        Comment comment = new Comment(body,postId);
        ApiComments.apiComment.insertComment(comment).
                enqueue(new Callback<ResponseInsertComment>() {
                    @Override
                    public void onResponse(Call<ResponseInsertComment> call, Response<ResponseInsertComment> response) {
                        Log.d("Response",response.toString());
                        progressDialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().getSuccessful()) {
                                list.add(response.body().getData());
                                commentAdapter.setData(list);
                                editComment.setText("");
                            }
                        } else {
                            Log.d("Error", response.toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseInsertComment> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Call Api Fail", Toast.LENGTH_SHORT).show();
                        Log.d("Error",t.getMessage());
                    }
                });
    }
}
