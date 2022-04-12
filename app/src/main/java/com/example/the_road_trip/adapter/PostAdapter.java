package com.example.the_road_trip.adapter;

import android.content.Context;
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

import java.util.List;
import java.util.Calendar;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> list;
    private Context context;


    public PostAdapter(List<Post> list, Context context) {
        this.list = list;
        this.context = context;

    }

    public void setData(List<Post> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = list.get(position);
        Glide.with(context).load(post.getImage())
                .centerCrop()
                .into(holder.imagePost);
        Glide.with(context).load(post.getUserId().getAvatar_url())
                .centerCrop()
                .into(holder.imageAuthor);

        holder.txtName.setText(post.getUserId().getFullName());

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DATE);
        String strTime = day == post.getTime_created() ? "Today" : day - post.getTime_created() + " ago";
        holder.txtTime.setText(strTime);
        holder.tvTitle.setText(post.getTitle());

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePost, imageAuthor, imageHeart, imageComment;
        private TextView txtName, txtTime, tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePost = itemView.findViewById(R.id.img_post_item);
            imageAuthor = itemView.findViewById(R.id.image_author_post);
            imageHeart = itemView.findViewById(R.id.heart_post);
            imageComment = itemView.findViewById(R.id.comment_post);
            txtName = itemView.findViewById(R.id.name_author_post);
            txtTime = itemView.findViewById(R.id.time_create_post);
            tvTitle = itemView.findViewById(R.id.post_title);
        }
    }
}