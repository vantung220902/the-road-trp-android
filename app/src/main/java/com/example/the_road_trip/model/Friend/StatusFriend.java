package com.example.the_road_trip.model.Friend;

import com.example.the_road_trip.model.ResponseData;

public class StatusFriend extends ResponseData {
    private Friend data;

    public StatusFriend(int code, Boolean successful, String message, Friend data) {
        super(code, successful, message);
        this.data = data;
    }

    public Friend getData() {
        return data;
    }

    public void setData(Friend data) {
        this.data = data;
    }
}
