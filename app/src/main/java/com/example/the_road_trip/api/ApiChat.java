package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

import com.example.the_road_trip.model.Chat.Chat;
import com.example.the_road_trip.model.Chat.ResponseChat;
import com.example.the_road_trip.model.Chat.ResponseInsertChat;
import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseComment;
import com.example.the_road_trip.model.Comment.ResponseInsertComment;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiChat {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();

    ApiChat apiChat =  new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/friend/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiChat.class);

    @POST("create")
    Call<ResponseInsertChat> insertChat(@Field("body") String body, @Field("receiver") String receiver);

//    @GET("gets")
//    Call<ResponseComment> gets(@Query("postId")String postId);
}
