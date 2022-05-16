package com.example.the_road_trip.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Chat.MessageModel;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    List<Chat> list;
    private User receiver;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;

    public ChatAdapter(Context context, List<Chat> list, User receiver) { // you can pass other parameters in constructor
        this.context = context;
        this.list = list;
        this.receiver = receiver;
    }

    public void setData(List<Chat> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, dateTV,tvName;
        CircleImageView circleImageView;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            dateTV = itemView.findViewById(R.id.date_text);
            circleImageView = itemView.findViewById(R.id.avatar_message);
            tvName = itemView.findViewById(R.id.text_name);
        }

        void bind(int position) {
            Chat chat = list.get(position);
            messageTV.setText(chat.getBody());
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int house = calendar.get(Calendar.HOUR);
            dateTV.setText(house + "house");
            tvName.setText(receiver.getFullName());
            Glide.with(context).load(receiver.getAvatar_url()).into(circleImageView);

        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV, dateTV;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            dateTV = itemView.findViewById(R.id.date_text);
        }

        void bind(int position) {
            Chat chat = list.get(position);
            messageTV.setText(chat.getBody());
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int house = calendar.get(Calendar.HOUR);
            dateTV.setText(house + " house");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_in, parent, false));
        }
        return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_out, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list.get(position).getMessageType() == MESSAGE_TYPE_IN) {
            ((MessageInViewHolder) holder).bind(position);
        } else {
            ((MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getMessageType();
    }
}
