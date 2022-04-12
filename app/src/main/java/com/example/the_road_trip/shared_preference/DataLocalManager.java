package com.example.the_road_trip.shared_preference;

import android.content.Context;

import com.example.the_road_trip.model.User.User;
import com.google.gson.Gson;

public class DataLocalManager {
    private static final String KEY_FIRST_INSTALL = "KEY_FIRST_INSTALL";
    private static final String USER_CURRENT = "USER_CURRENT";
    private static final String MY_ACCESS_TOKEN = "MY_ACCESS_TOKEN";
    private static final String REFRESH_ACCESS_TOKEN = "REFRESH_ACCESS_TOKEN";
    private static DataLocalManager instance;
    private MySharedPreference myPreferences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.myPreferences = new MySharedPreference(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setFirstInstalled(boolean isFirst) {
        DataLocalManager.getInstance().myPreferences.putBooleanValue(KEY_FIRST_INSTALL, isFirst);
    }

    public static boolean getFirstInstalled() {
        return DataLocalManager.getInstance().myPreferences.getBooleanValues(KEY_FIRST_INSTALL);

    }

    public static void setAccessToken(String accessToken) {
        DataLocalManager.getInstance().myPreferences.putStringValue(MY_ACCESS_TOKEN, "Bearer "+ accessToken);
    }

    public static String getAccessToken() {
        return DataLocalManager.getInstance().myPreferences.getStringValues(MY_ACCESS_TOKEN);
    }

    public static void setRefreshToken(String refreshToken) {
        DataLocalManager.getInstance().myPreferences.putStringValue(REFRESH_ACCESS_TOKEN, refreshToken);
    }

    public static String getRefreshToken() {
        return DataLocalManager.getInstance().myPreferences.getStringValues(REFRESH_ACCESS_TOKEN);
    }

    public static void setUserCurrent(User user) {
        Gson gson = new Gson();
        String strUser = gson.toJson(user);
        DataLocalManager.getInstance().myPreferences.putStringValue(USER_CURRENT, strUser);
    }

    public static User getUserCurrent() {
        String strUser = DataLocalManager.getInstance().myPreferences.getStringValues(USER_CURRENT);
        Gson gson = new Gson();
        User user = gson.fromJson(strUser, User.class);
        return user;
    }

}
