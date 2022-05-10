package com.example.the_road_trip.api;

import static com.example.the_road_trip.api.Constant.URL_SERVER;
import static com.example.the_road_trip.api.Constant.getHeader;

import com.example.the_road_trip.model.Post.ResponsePost;
import com.example.the_road_trip.model.User.ReturnUpdateUser;
import com.example.the_road_trip.shared_preference.DataLocalManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIUser {
    //localhost:4000/api/user/
    Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss").create();
    APIUser apiUser = new Retrofit.Builder()
            .baseUrl(URL_SERVER + "/api/user/")
            .client(getHeader(DataLocalManager.getAccessToken()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIUser.class);

    @Multipart
    @PUT("update")
    Call<ReturnUpdateUser> updateUser(@Part(Constant.KEY_FULL_NAME) RequestBody fullName,
                                      @Part(Constant.KEY_ADDRESS) RequestBody address,
                                      @Part MultipartBody.Part image);


}
