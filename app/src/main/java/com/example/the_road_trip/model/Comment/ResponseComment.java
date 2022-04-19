package com.example.the_road_trip.model.Comment;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponseComment extends ResponseData {
    List<Comment> data;

    public ResponseComment(int code, Boolean successful, String message, List<Comment> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Comment> getData() {
        return data;
    }

    public void setList(List<Comment> data) {
        this.data = data;
    }
}
