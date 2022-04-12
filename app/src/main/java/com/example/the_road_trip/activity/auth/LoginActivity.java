package com.example.the_road_trip.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.FirstInstallActivity;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.SplashActivity;
import com.example.the_road_trip.api.ApiAuth;
import com.example.the_road_trip.model.ModelLink;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private MaterialButton btnLogin;
    private ImageButton btnBack;
    private long backPressTime;
    private Toast mToast;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init UI
        if(isCurrentUser()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        initUI();

        //validate
        //Validate form
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        //Check email validate
        awesomeValidation.addValidation(this, R.id.login_email, android.util.Patterns.EMAIL_ADDRESS,
                R.string.err_email);
        //if password < 8 characters is error
        awesomeValidation.addValidation(this, R.id.login_password, s -> {
            if (editPassword.getText().length() < 8) return false;
            return true;
        }, R.string.err_password_length);

        //check register successfully
        String announcement = getIntent().getStringExtra("announcement");
        if (announcement != null) {
            Toast.makeText(this, announcement, Toast.LENGTH_SHORT).show();
        }
        btnLogin.setOnClickListener(view -> {
            if (awesomeValidation.validate()) {
                login();
            } else {
                Toast.makeText(this, "Validate Failed...", Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, FirstInstallActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUI() {
        editEmail = findViewById(R.id.login_email);
        editPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login_form);
        btnBack = findViewById(R.id.btn_back_login);
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

    private void login() {
        String strEmail = editEmail.getText().toString();
        String strPassword = editPassword.getText().toString();
        User user = new User(strEmail, strPassword);
        ApiAuth.apiAuth.loginUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body().getSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            Bundle bundle = new Bundle();
                            ModelLink modelLink = new ModelLink("Login Successfully...!",MainActivity.class);
                            bundle.putSerializable("screen_next", modelLink);
                            intent.putExtras(bundle);
                            String accessToken = response.body().getAccessToken()!=null ?
                                   response.body().getAccessToken() :"";
                            String refreshToken = response.body().getRefreshToken()!=null ?
                                    response.body().getRefreshToken():"";
                            User currUser = response.body();
                            DataLocalManager.setUserCurrent(currUser);
                            DataLocalManager.setAccessToken(accessToken);
                            DataLocalManager.setRefreshToken(refreshToken);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string()) ;
                            String internalMessage = jsonObject.getString("message") ;
                            Toast.makeText(LoginActivity.this, internalMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    Log.e("Auth Error", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login Failed...", Toast.LENGTH_SHORT).show();
                Log.e("Auth Error", t.getMessage());
            }
        });
    }
    private boolean isCurrentUser(){
        User user  = DataLocalManager.getUserCurrent();
        return user!=null;
    }
}