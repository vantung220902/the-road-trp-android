package com.example.the_road_trip.model.Post;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponsePost extends ResponseData {
    private List<Post> data;

    public ResponsePost(int code, Boolean successful, String message, List<Post> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post>  data) {
        this.data = data;
    }
}
