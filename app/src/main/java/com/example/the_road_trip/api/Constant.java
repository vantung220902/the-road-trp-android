package com.example.the_road_trip.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class Constant {
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String URL_SERVER = "http://172.25.240.1:4000";
    public static final String KEY_BODY = "body";
    public static final String KEY_POST_ID = "postId";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_ADDRESS = "address";
    public static OkHttpClient getHeader(final String authorizationValue) {
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

}
