package com.example.the_road_trip.model.Chat;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponseChat extends ResponseData {
    private List<Chat> data;

    public ResponseChat(int code, Boolean successful, String message, List<Chat> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Chat> getData() {
        return data;
    }

    public void setData(List<Chat> data) {
        this.data = data;
    }
}
