package com.example.the_road_trip.activity.ticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.FirstInstallActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.activity.auth.LoginActivity;
import com.example.the_road_trip.activity.auth.RegisterActivity;
import com.example.the_road_trip.api.ApiAuth;
import com.example.the_road_trip.api.ApiPayment;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.Payment.Payment;
import com.example.the_road_trip.model.Payment.ResponseInsertPayment;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.Ticket.Ticket;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private EditText editGmail, editPassword, editPhone, editFullName;
    private MaterialButton btnSubmit;
    private AwesomeValidation awesomeValidation;
    private ImageButton btnBack;
    private TextView tvName, tvSum;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //Init Ui
        initUI();
        Payment payment = (Payment) getIntent().getExtras().get("payment");
        //Validate form

        checkValid();
        setInfo(payment);
        btnSubmit.setOnClickListener(view -> {
            if (awesomeValidation.validate()) {
                insert(payment);
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content).getRootView(), "Validate Failed...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void setInfo(Payment payment) {

        editGmail.setText(DataLocalManager.getUserCurrent().getEmail());
        editFullName.setText(DataLocalManager.getUserCurrent().getFullName());
        tvSum.setText(payment.getSum() + "");
        tvName.setText(payment.getTicket().getName());
    }

    private void initUI() {
        editGmail = findViewById(R.id.payment_email);
        editPassword = findViewById(R.id.payment_password);
        editPhone = findViewById(R.id.payment_phone);
        editFullName = findViewById(R.id.payment_fullName);
        btnSubmit = findViewById(R.id.btn_payment);
        tvName = findViewById(R.id.payment_name);
        tvSum = findViewById(R.id.payment_sum);
        btnBack = findViewById(R.id.btn_back_payment);
        progressBar = findViewById(R.id.idPBLoadingPayment);
    }

    private void checkValid() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        //Check email validate
        awesomeValidation.addValidation(this, R.id.payment_email, android.util.Patterns.EMAIL_ADDRESS,
                R.string.err_email);
        //if password < 8 characters is error
        awesomeValidation.addValidation(this, R.id.payment_password, s -> {
            if (editPassword.getText().length() < 8) return false;
            return true;
        }, R.string.err_password_length);
        awesomeValidation.addValidation(this, R.id.payment_phone, s -> {
            if (editPhone.getText().length() < 8) return false;
            return true;
        }, R.string.error_tel);
        awesomeValidation.addValidation(this, R.id.payment_phone, RegexTemplate.TELEPHONE, R.string.error_tel);
        awesomeValidation.addValidation(this, R.id.payment_fullName, s -> {
            if (editFullName.getText().toString().trim().length() < 3) return false;
            return true;
        }, R.string.edit_fullName);
    }

    private void insert(Payment payment) {
        String password = editPassword.getText().toString();
        ApiPayment.apiPayment.insertPayment(payment.getSum(), payment.getNumber()
                , payment.getTicket().get_id(), password).enqueue(new Callback<ResponseInsertPayment>() {
            @Override
            public void onResponse(Call<ResponseInsertPayment> call, Response<ResponseInsertPayment> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body().getSuccessful()) {
                            Intent intent = new Intent(PaymentActivity.this,ManagePaymentActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            SnackbarCustomer(response.body().getMessage());
                        }
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
                    SnackbarCustomer("Paying Failed...");
                    Log.e("Payment Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseInsertPayment> call, Throwable t) {
                SnackbarCustomer("Paying Failed...");
                Log.e("Auth Error", t.getMessage());
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

                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}