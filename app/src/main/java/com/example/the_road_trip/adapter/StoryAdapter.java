package com.example.the_road_trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.interfaces.IClickStory;
import com.example.the_road_trip.model.Story.Story;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<Story> list;
    private Context context;
    private IClickStory iClickStory;

    public StoryAdapter(List<Story> list, Context context, IClickStory iClickStory) {
        this.list = list;
        this.context = context;
        this.iClickStory = iClickStory;
    }

    public void setData(List<Story> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = list.get(position);
        if (story == null) return;
        Glide.with(context).load(story.getUserId().getAvatar_url())
                .centerCrop()
                .into(holder.circleImageView);
        holder.circleImageView.setOnClickListener(view -> {
            iClickStory.getStory(story.get_id());
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.story_image_item);
        }
    }
}
