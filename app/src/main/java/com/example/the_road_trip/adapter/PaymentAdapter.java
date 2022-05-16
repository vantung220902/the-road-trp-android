package com.example.the_road_trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.ticket.QRActivity;
import com.example.the_road_trip.model.Payment.Payment;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private List<Payment> list;
    private Context mContext;

    public PaymentAdapter(List<Payment> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setData(List<Payment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = list.get(position);
        if (payment == null) return;
        String image = payment.getTicket().getImage().split(";")[0];
        Glide.with(mContext).load(image).centerCrop().into(holder.img);
        Glide.with(mContext).load(payment.getUserId().getAvatar_url()).into(holder.avatar);
        holder.tvName.setText("Name: " + payment.getTicket().getName());
        holder.tvNumber.setText("Number: " + payment.getNumber());
        holder.tvAddress.setText("Address: " + payment.getTicket().getAddress());
        holder.tvDate.setText("Date: " + payment.getTicket().getDate());
        holder.tvSum.setText("Sum: " + payment.getSum());
        holder.tvAuthor.setText(payment.getUserId().getFullName());
        holder.imageButton.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, QRActivity.class);
            intent.putExtra("qr", "Payment Id: " + payment.get_id() + ". Event ID: " +
                    payment.getTicket().get_id() + ". UserId: " +
                    DataLocalManager.getUserCurrent().get_id() +
                    ". Name: "
                    + DataLocalManager.getUserCurrent().getFullName() + ". Sum: " + payment.getSum()
                    + ". Number:" + payment.getNumber())
            ;
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private CircleImageView avatar;
        private TextView tvName, tvAddress, tvDate, tvSum, tvNumber, tvAuthor;
        private ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_payment);
            avatar = itemView.findViewById(R.id.payment_avatar);
            tvName = itemView.findViewById(R.id.name_payment);
            tvAddress = itemView.findViewById(R.id.payment_address);
            tvDate = itemView.findViewById(R.id.payment_date);
            tvSum = itemView.findViewById(R.id.payment_sum_manage);
            tvNumber = itemView.findViewById(R.id.payment_number);
            tvAuthor = itemView.findViewById(R.id.payment_author);
            imageButton = itemView.findViewById(R.id.img_qr_code);
        }
    }
}
