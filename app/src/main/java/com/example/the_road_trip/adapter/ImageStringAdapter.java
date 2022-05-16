package com.example.the_road_trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.utils.DisplayImageActivity;

import java.util.List;

public class ImageStringAdapter extends RecyclerView.Adapter<ImageStringAdapter.ViewHolder> {
    private List<String> list;
    private Context mContext;

    public ImageStringAdapter(Context mContext, List<String> list) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setData(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageStringAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageStringAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageStringAdapter.ViewHolder holder, int position) {
        String str = list.get(position);
        if (str == null) return;
        holder.textView.setText(position + 1 + "");
        Glide.with(mContext).load(str)
                .centerCrop()
                .into(holder.image);
        holder.image.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DisplayImageActivity.class);
            intent.putExtra("image", str);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_item);
            textView = itemView.findViewById(R.id.item_number_image);
        }
    }
}
