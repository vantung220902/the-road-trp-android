package com.example.the_road_trip.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.FirstInstallActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.api.ApiAuth;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.User.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText editGmail, editPassword, editConformPassword, editFullName;
    private MaterialButton btnSubmit;
    private AwesomeValidation awesomeValidation;
    private ImageButton btnBack;
    private long backPressTime;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Init Ui
        initUI();

        //Validate form
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        //Check email validate
        awesomeValidation.addValidation(this, R.id.edit_email, android.util.Patterns.EMAIL_ADDRESS,
                R.string.err_email);
        //if password < 8 characters is error
        awesomeValidation.addValidation(this, R.id.edit_password, s -> {
            if (editPassword.getText().length() < 8) return false;
            return true;
        }, R.string.err_password_length);

        awesomeValidation.addValidation(this, R.id.edit_fullName, s -> {
            if (editFullName.getText().toString().trim().length() < 3) return false;
            return true;
        }, R.string.edit_fullName);

        //check conform password match password
        awesomeValidation.addValidation(this, R.id.edit_conform_password, R.id.edit_password, R.string.edit_conform_password);
        btnSubmit.setOnClickListener(view -> {
            if (awesomeValidation.validate()) {
                register();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content).getRootView(), "Validate Failed...", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, FirstInstallActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUI() {
        editGmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConformPassword = findViewById(R.id.edit_conform_password);
        editFullName = findViewById(R.id.edit_fullName);
        btnSubmit = findViewById(R.id.btn_register_form);
        btnBack = findViewById(R.id.btn_back_register);
    }

    private void register() {
        String strEmail = editGmail.getText().toString();
        String strPassword = editPassword.getText().toString();
        String strFullName = editFullName.getText().toString();
        User user = new User(strEmail, strPassword, strFullName);
        ApiAuth.apiAuth.registerUser(user).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                try {
                    if (response.code() == 200) {
                        if (response.body().getSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                            Bundle bundle = new Bundle();
                            ModelLink modelLink = new ModelLink("Register Successfully...!", LoginActivity.class);
                            bundle.putSerializable("screen_next", modelLink);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
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
                    SnackbarCustomer("Register Failed...");
                    Log.e("Auth Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                SnackbarCustomer("Register Failed...");
                Log.e("Auth Error", t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressTime + 2000 > System.currentTimeMillis()) {
            mToast.cancel();
            super.onBackPressed();
        } else {
            mToast = Toast.makeText(this, "Press Back To Exit", Toast.LENGTH_SHORT);
            mToast.show();
        }
        backPressTime = System.currentTimeMillis();
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
                        register();
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }
}