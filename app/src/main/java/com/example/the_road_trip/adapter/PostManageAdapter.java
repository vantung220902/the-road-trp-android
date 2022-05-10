package com.example.the_road_trip.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.model.Post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostManageAdapter extends RecyclerView.Adapter<PostManageAdapter.MyViewHolder> {
    private Context mContext;
    private List<Post> list;

    public PostManageAdapter(Context context, List<Post> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setData(List<Post> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void loadMore(List<Post> list) {
        for (Post post : list)
            this.list.add(post);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = list.get(position);
        holder.tvPostTitle.setText(post.getTitle());
        holder.tvNameAuthor.setText(post.getUserId().getFullName());
        String image = post.getImage().split(";")[0];
        Glide.with(mContext).load(image)
                .centerCrop()
                .into(holder.imagePost);
        Glide.with(mContext).load(post.getUserId().getAvatar_url())
                .centerCrop()
                .into(holder.imageAuthor);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPostTitle;
        private TextView tvNameAuthor;
        private ImageView imagePost, imageAuthor;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPostTitle = itemView.findViewById(R.id.post_manage_title);
            tvNameAuthor = itemView.findViewById(R.id.post_manage_author);
            imagePost = itemView.findViewById(R.id.image_manage_post);
            imageAuthor = itemView.findViewById(R.id.img_author_manage_post);
        }
    }
}