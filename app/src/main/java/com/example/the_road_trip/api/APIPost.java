package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

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
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIPost {
    //localhost:4000/api/post/
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();
    APIPost apiPOST = new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/post/")
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
