package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

import com.example.the_road_trip.model.Payment.ResponseInsertPayment;
import com.example.the_road_trip.model.Payment.ResponsePayments;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiPayment {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();

    ApiPayment apiPayment =  new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/payment/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiPayment.class);

    @FormUrlEncoded
    @POST("create")
    Call<ResponseInsertPayment> insertPayment(@Field("sum") float sum, @Field("number") int number,
                                              @Field("ticket") String ticket, @Field("password") String password );

    @GET("gets")
    Call<ResponsePayments> gets();
}
