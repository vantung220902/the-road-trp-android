package com.example.the_road_trip;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.activity.auth.LoginActivity;
import com.example.the_road_trip.adapter.PostAdapter;
import com.example.the_road_trip.api.ApiAuth;
import com.example.the_road_trip.fragment.DiscoverFragment;
import com.example.the_road_trip.model.User.RefreshToken;
import com.example.the_road_trip.model.User.User;
import com.example.the_road_trip.shared_preference.DataLocalManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
        refreshData();
    }

    private void refreshData() {
        if (DataLocalManager.getUserCurrent() != null) {
            User user = DataLocalManager.getUserCurrent();
            int timer = user.getExp() - user.getIat();
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getRefreshToken(DataLocalManager.getRefreshToken());
                }
            }, 0,  timer * 1000 - 7000);
        }
    }

    public void getRefreshToken(String refresh) {
        ApiAuth.apiAuth.refreshToken(refresh).enqueue(new Callback<RefreshToken>() {
            @Override
            public void onResponse(Call<RefreshToken> call, Response<RefreshToken> response) {
                try {
                    if (response.code() == 200) {
                        DataLocalManager.setAccessToken(response.body().getAccessToken());
                    } else {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String internalMessage = jsonObject.getString("message");
                            Log.d("Refresh Token Error", internalMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RefreshToken> call, Throwable t) {
                Log.d("Error Call Api", t.getMessage());
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
