package com.example.the_road_trip.model.User;

import com.example.the_road_trip.model.ResponseData;

public class ReturnUpdateUser extends ResponseData {
    User data;

    public ReturnUpdateUser(int code, Boolean successful, String message, User data) {
        super(code, successful, message);
        this.data = data;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
