package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;

import android.util.Log;

import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.Comment.ResponseComment;
import com.example.the_road_trip.model.Comment.ResponseInsertComment;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiComments {
    //localhost:4000/api/comment/
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();

    static OkHttpClient getHeader(final String authorizationValue) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(
                        chain -> {
                            Request request = null;
                            if (authorizationValue != null) {
                                Log.d("--Authorization-- ", authorizationValue);
                                Request original = chain.request();
                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", authorizationValue);
                                request = requestBuilder.build();
                            }
                            return chain.proceed(request);
                        })
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        return okClient;

    }

    ApiComments apiComment = new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/comments/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiComments.class);

    @POST("create")
    Call<ResponseInsertComment> insertComment(@Body Comment comment);

    @GET("gets")
    Call<ResponseComment> gets(@Query("postId")String postId);
}
