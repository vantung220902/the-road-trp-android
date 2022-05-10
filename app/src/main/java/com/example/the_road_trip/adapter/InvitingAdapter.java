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
import com.example.the_road_trip.model.Friend.Inviting;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitingAdapter extends RecyclerView.Adapter<InvitingAdapter.ViewHolder> {
    private List<Inviting> list;
    private Context mContext;
    private ICLickInviting icLickInviting;
    public interface ICLickInviting{
        void actions(String _id,int status);
    }

    public InvitingAdapter(List<Inviting> list, Context mContext, ICLickInviting icLickInviting) {
        this.list = list;
        this.mContext = mContext;
        this.icLickInviting = icLickInviting;
    }

    public void setData(List<Inviting> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inviting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inviting inviting = list.get(position);
        if (inviting == null) return;
        Glide.with(mContext).load(inviting.getSender().getAvatar_url())
                .into(holder.avatar);
        holder.tvName.setText(inviting.getSender().getFullName());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int day = calendar.get(Calendar.DATE);
        String strTime = day == inviting.getTime_created() ? "Today" :
                (day - inviting.getTime_created()) * -1 + " ago";
        holder.tvTime.setText(strTime);
        holder.btnAccept.setOnClickListener(view -> {
            icLickInviting.actions(inviting.get_id(),1);
        });
        holder.btnDecline.setOnClickListener(view -> {
            icLickInviting.actions(inviting.get_id(),0);
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView tvName, tvTime;
        private MaterialButton btnAccept;
        private MaterialButton btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.inviting_avatar);
            tvName = itemView.findViewById(R.id.tv_name_inviting);
            tvTime = itemView.findViewById(R.id.tv_time_inviting);
            btnAccept = itemView.findViewById(R.id.btn_accept_inviting);
            btnDecline = itemView.findViewById(R.id.btn_decline_inviting);
        }
    }
}
