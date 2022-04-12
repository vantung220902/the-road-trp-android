package com.example.the_road_trip.api;

import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.model.User.RefreshToken;
import com.example.the_road_trip.model.User.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiAuth {
//localhost:4000/api/user/
Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();

    ApiAuth apiAuth =new Retrofit.Builder()
            .baseUrl("http://192.168.1.2:4000/api/user/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiAuth.class);

    @POST("register")
    Call<ResponseData> registerUser(@Body User User);

    @POST("login")
    Call<User> loginUser(@Body User user);

    @GET("refresh")
    Call<RefreshToken> refreshToken(@Query("refreshToken") String refreshToken);
}
