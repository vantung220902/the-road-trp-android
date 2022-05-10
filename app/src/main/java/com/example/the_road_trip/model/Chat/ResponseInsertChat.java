package com.example.the_road_trip.model.Chat;

import com.example.the_road_trip.model.Comment.Comment;
import com.example.the_road_trip.model.ResponseData;

public class ResponseInsertChat extends ResponseData {
    private Chat data;

    public ResponseInsertChat(int code, Boolean successful, String message, Chat data) {
        super(code, successful, message);
        this.data = data;
    }

    public Chat getData() {
        return data;
    }

    public void setData(Chat data) {
        this.data = data;
    }
}
