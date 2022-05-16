package com.example.the_road_trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.ticket.DetailTicketActivity;
import com.example.the_road_trip.model.Post.Post;
import com.example.the_road_trip.model.Ticket.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
    private List<Ticket> list;
    private Context mContext;

    public TicketAdapter(List<Ticket> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setData(List<Ticket> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void loadMore(List<Ticket> list) {
        for (Ticket ticket : list)
                this.list.add(ticket);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = list.get(position);
        if (ticket == null) return;
        holder.tvName.setText(ticket.getName());
        holder.tvAddress.setText(ticket.getAddress());
        holder.tvDate.setText(ticket.getDate());
        holder.tvPrice.setText(ticket.getPrice() + "");
        holder.tvRest.setText(ticket.getRest() + "");
        String image = ticket.getImage().split(";")[0];
        Glide.with(mContext).load(image).into(holder.imageView);
        holder.container.setOnClickListener(view -> {
           Intent intent = new Intent(mContext, DetailTicketActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ticket",ticket);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvName, tvPrice, tvDate, tvAddress, tvRest;
        private LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_ticket);
            tvName = itemView.findViewById(R.id.tv_name_ticket);
            tvPrice = itemView.findViewById(R.id.tv_price_ticket);
            tvDate = itemView.findViewById(R.id.tv_time_ticket);
            tvAddress = itemView.findViewById(R.id.tv_address_ticket);
            tvRest = itemView.findViewById(R.id.tv_rest);
            container = itemView.findViewById(R.id.container_ticket);
        }
    }
}
