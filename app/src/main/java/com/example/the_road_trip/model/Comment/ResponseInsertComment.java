package com.example.the_road_trip.model.Comment;

import com.example.the_road_trip.model.ResponseData;

public class ResponseInsertComment extends ResponseData {
    private Comment data;

    public ResponseInsertComment(int code, Boolean successful, String message, Comment data) {
        super(code, successful, message);
        this.data = data;
    }

    public Comment getData() {
        return data;
    }

    public void setData(Comment data) {
        this.data = data;
    }
}
