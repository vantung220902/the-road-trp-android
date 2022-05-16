package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

import com.example.the_road_trip.model.Ticket.ResponseTicket;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiTicket {
    //localhost:4000/api/ticket/
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();
    ApiTicket apiTicket = new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/ticket/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiTicket.class);
    @GET("gets")
    Call<ResponseTicket> gets(@Query("query") String query, @Query("paging") int paging);
}
