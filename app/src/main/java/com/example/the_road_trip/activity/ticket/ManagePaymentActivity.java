package com.example.the_road_trip.activity.ticket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.the_road_trip.R;
import com.example.the_road_trip.adapter.PaymentAdapter;
import com.example.the_road_trip.api.ApiComments;
import com.example.the_road_trip.api.ApiPayment;
import com.example.the_road_trip.model.Comment.ResponseComment;
import com.example.the_road_trip.model.Payment.Payment;
import com.example.the_road_trip.model.Payment.ResponsePayments;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagePaymentActivity extends AppCompatActivity {
    private List<Payment> list = new ArrayList<>();
    private RecyclerView rcv;
    private PaymentAdapter paymentAdapter;
    private MaterialButton btnLoadMore, btnToTicket;
    private TextInputEditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payment);
        initUI();
        loadPayments();
    }

    private void initUI() {
        rcv = findViewById(R.id.rcv_manage_payment);
        btnLoadMore = findViewById(R.id.btn_load_payment);
        btnToTicket = findViewById(R.id.btn_to_ticket);
        editSearch = findViewById(R.id.edit_search_manage_payment);
        paymentAdapter = new PaymentAdapter(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        rcv.setAdapter(paymentAdapter);
    }

    public void loadPayments() {
        ApiPayment.apiPayment.gets().enqueue(new Callback<ResponsePayments>() {
            @Override
            public void onResponse(Call<ResponsePayments> call, Response<ResponsePayments> response) {
                try {
                    if (response.code() == 200) {
                        list = response.body().getData();
                        paymentAdapter.setData(list);
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            SnackbarCustomer(internalMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    SnackbarCustomer(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponsePayments> call, Throwable t) {
                SnackbarCustomer(t.getMessage());
                Log.d("Error Call Api", t.getMessage());
            }
        });
    }

    private void SnackbarCustomer(String str) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content).getRootView(),
                        str, Snackbar.LENGTH_LONG)
                .setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(findViewById(android.R.id.content).getRootView(),
                                "Loading...", Snackbar.LENGTH_SHORT);
                        loadPayments();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

}