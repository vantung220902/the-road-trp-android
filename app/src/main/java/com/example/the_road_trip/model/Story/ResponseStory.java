package com.example.the_road_trip.model.Story;

import com.example.the_road_trip.model.ResponseData;

import java.io.Serializable;
import java.util.List;

public class ResponseStory extends ResponseData implements Serializable {
    private List<Story> data;

    public ResponseStory(int code, Boolean successful, String message, List<Story> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Story> getData() {
        return data;
    }

    public void setData(List<Story> data) {
        this.data = data;
    }
}
