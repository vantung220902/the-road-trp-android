package com.example.the_road_trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> list;
    private Context mContext;

    public CommentAdapter(Context mContext, List<Comment> list) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setData(List<Comment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = list.get(position);
        if (comment == null) return;
        Glide.with(mContext).load(comment.getUserId().getAvatar_url())
                .centerCrop()
                .into(holder.avatar);
        holder.tvName.setText(comment.getUserId().getFullName());
        holder.tvBody.setText(comment.getBody());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DATE);
        String strTime = day == comment.getTime_created() ? "Today" : day - comment.getTime_created() + "day ago";
        holder.tvTime.setText(strTime);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView tvName, tvBody, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_comment);
            tvName = itemView.findViewById(R.id.tv_name_comments);
            tvBody = itemView.findViewById(R.id.tv_body_comment);
            tvTime = itemView.findViewById(R.id.tv_time_comment);

        }
    }
}
