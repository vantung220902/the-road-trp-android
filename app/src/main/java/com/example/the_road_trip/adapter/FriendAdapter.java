package com.example.the_road_trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.model.Friend.Inviting;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<Inviting> list;
    private Context mContext;
    private IClickChat iClickChat;

    public interface IClickChat {
        void clickChat(User receiver);
    }

    public FriendAdapter(List<Inviting> list, Context mContext, IClickChat iClickChat) {
        this.list = list;
        this.mContext = mContext;
        this.iClickChat = iClickChat;
    }

    public void setData(List<Inviting> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inviting inviting = list.get(position);
        if (inviting == null) return;
        if (inviting.getSender().get_id().equals(DataLocalManager.getUserCurrent().get_id())) {
            Glide.with(mContext).load(inviting.getReceiver().getAvatar_url())
                    .centerCrop()
                    .into(holder.avatar);
            holder.tvName.setText(inviting.getReceiver().getFullName());
            holder.tvAddress.setText(inviting.getReceiver().getAddress());
            holder.btnMessage.setOnClickListener(view -> {
                iClickChat.clickChat(inviting.getReceiver());
            });
        } else {
            Glide.with(mContext).load(inviting.getSender().getAvatar_url())
                    .centerCrop()
                    .into(holder.avatar);
            holder.tvName.setText(inviting.getSender().getFullName());
            holder.tvAddress.setText(inviting.getSender().getAddress());
            holder.btnMessage.setOnClickListener(view -> {
                iClickChat.clickChat(inviting.getSender());
            });
        }

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView tvName, tvAddress;
        private ImageButton btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.chat_avatar);
            tvName = itemView.findViewById(R.id.tv_name_chat);
            tvAddress = itemView.findViewById(R.id.tv_address_chat);
            btnMessage = itemView.findViewById(R.id.btn_message);
        }
    }
}
