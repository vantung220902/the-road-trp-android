package com.example.the_road_trip.activity.ticket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.PhotoAdapter;
import com.example.the_road_trip.model.Payment.Payment;
import com.example.the_road_trip.model.Slide.Photo;
import com.example.the_road_trip.model.Ticket.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

public class DetailTicketActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private Timer timer;
    private TextView tvName, tvAddress, tvDate, tvPrice, tvRest, tvNameAuthor, tvDescription,
            tvSeeMore, tvAdd, tvNumber, tvMinus, tvSumPrice, tvError;
    private CircleImageView avatar;
    private CardView btnAddToCart;
    private PhotoAdapter photoAdapter;
    private List<Photo> listPhoto = new ArrayList<>();
    private boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        Ticket ticket = (Ticket) getIntent().getExtras().get("ticket");
        String array[] = ticket.getImage().split(";");
        for (String s : array)
            listPhoto.add(new Photo(s));
        initUI();
        Glide.with(DetailTicketActivity.this).load(ticket.getAuthor().getAvatar_url())
                .into(avatar);
        tvAdd.setOnClickListener(view -> {
            setNumber(+1);
        });
        tvMinus.setOnClickListener(view -> {
            setNumber(-1);
        });
        tvSeeMore.setOnClickListener(view -> {
            if (check) {
                tvSeeMore.setText("Collapse");
                tvDescription.setMaxLines(10);
            } else {
                tvSeeMore.setText("...See More");
                tvDescription.setMaxLines(3);
            }
            check = !check;
        });
        setInfo(ticket);
        btnAddToCart.setOnClickListener(view -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            Bundle bundle = new Bundle();
            int number = Integer.parseInt(tvNumber.getText().toString());
            float sum = Float.parseFloat(tvSumPrice.getText().toString());
            Payment payment = new Payment("", ticket, number, sum, null);
            bundle.putSerializable("payment", payment);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void initUI() {
        viewPager = findViewById(R.id.viewpager);
        circleIndicator = findViewById(R.id.circle_indicator);
        tvName = findViewById(R.id.tv_name);
        tvAddress = findViewById(R.id.tv_address);
        tvDate = findViewById(R.id.tv_date);
        tvPrice = findViewById(R.id.tv_price_ticket);
        avatar = findViewById(R.id.img_author_detail);
        tvNameAuthor = findViewById(R.id.tv_name_author);
        tvDescription = findViewById(R.id.tv_description);
        tvSeeMore = findViewById(R.id.tv_see_more);
        tvRest = findViewById(R.id.tv_rest_detail);
        tvMinus = findViewById(R.id.btn_minus);
        tvNumber = findViewById(R.id.tv_number);
        tvAdd = findViewById(R.id.btn_add);
        tvError = findViewById(R.id.tvError);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        tvSumPrice = findViewById(R.id.sum_price);
        photoAdapter = new PhotoAdapter(this, listPhoto);
        viewPager.setAdapter(photoAdapter);
        circleIndicator.setViewPager(viewPager);
        photoAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        autoSlideImage();
    }

    private void setNumber(int value) {
        int number = Integer.parseInt(tvNumber.getText().toString());
        int sum = number + value;
        int rest = Integer.parseInt(tvRest.getText().toString());

        if (number + value < 1 || number + value > rest) {
            if (value > 0 && sum < 1)
                changeNumber(number, value);
            else if (value < 0 && sum > rest)
                changeNumber(number, value);
            else
                tvError.setVisibility(View.VISIBLE);
        } else
            changeNumber(number, value);
    }

    private void changeNumber(int number, int value) {
        tvError.setVisibility(View.GONE);
        float price = Float.parseFloat(tvPrice.getText().toString());
        tvNumber.setText(number + value + "");
        int rest = Integer.parseInt(tvRest.getText().toString());
        tvRest.setText(rest + (-1 * value) + "");
        tvSumPrice.setText(price * (number + value) + "");
    }

    private void setInfo(Ticket ticket) {
        tvName.setText(ticket.getName());
        tvAddress.setText("Address" + ticket.getAddress());
        tvNameAuthor.setText(ticket.getAuthor().getFullName());
        if (ticket.getDescription().length() > 450) tvSeeMore.setVisibility(View.VISIBLE);
        else tvSeeMore.setVisibility(View.GONE);
        tvDescription.setText(ticket.getDescription());
        tvRest.setText(ticket.getRest() + "");
        tvDate.setText("Date" + ticket.getDate());
        tvPrice.setText(ticket.getPrice() + "");
        tvSumPrice.setText(ticket.getPrice() + "");
    }

    private void autoSlideImage() {
        if (listPhoto == null || listPhoto.isEmpty() || viewPager == null) return;

        if (timer == null) timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItem = listPhoto.size() - 1;
                        if (currentItem < totalItem) {
                            currentItem++;
                            viewPager.setCurrentItem(currentItem);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }, 500, 3000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}