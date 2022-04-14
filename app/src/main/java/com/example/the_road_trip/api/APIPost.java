package com.example.the_road_trip.api;

import android.util.Log;

import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.ResponseData;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIPost {
    //localhost:4000/api/post/
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

    APIPost apiPOST = new Retrofit.Builder()
            .baseUrl("http://192.168.1.6:4000/api/post/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIPost.class);

    @Multipart
    @POST("create")
    Call<ResponseData> insertPost(@Part(Constant.KEY_TITLE) RequestBody title,
                                  @Part MultipartBody.Part image);

    @GET("gets")
    Call<ResponsePost> gets(@Query("query") String query, @Query("paging") int paging);

    @GET("getById")
    Call<ResponsePost> getById(@Query("_userId") String _userID);

}
