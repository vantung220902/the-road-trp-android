package com.example.the_road_trip.model.Ticket;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponseTicket extends ResponseData {
    private List<Ticket> data;

    public ResponseTicket(int code, Boolean successful, String message, List<Ticket> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Ticket> getData() {
        return data;
    }

    public void setData(List<Ticket> data) {
        this.data = data;
    }
}
