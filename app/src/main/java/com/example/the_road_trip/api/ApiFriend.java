package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

import com.example.the_road_trip.model.Friend.ResponseInviting;
import com.example.the_road_trip.model.Friend.StatusFriend;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiFriend {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();

    ApiFriend apiFriend = new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/friend/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiFriend.class);

    @GET("status")
    Call<StatusFriend> status(@Query("receiver") String receiver);

    @GET("action")
    Call<ResponseData> action(@Query("receiver") String receiver, @Query("id") String id, @Query("status") int status);

    @GET("inviting")
    Call<ResponseData> actionInviting(@Query("id") String id, @Query("status") int status);

    @GET("gets")
    Call<ResponseInviting> gets();

    @GET("friends")
    Call<ResponseInviting> friends();
}
