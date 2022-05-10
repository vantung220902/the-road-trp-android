package com.example.the_road_trip.model.Friend;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponseInviting extends ResponseData {
    private List<Inviting> data;

    public ResponseInviting(int code, Boolean successful, String message, List<Inviting> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Inviting> getData() {
        return data;
    }

    public void setData(List<Inviting> data) {
        this.data = data;
    }
}
