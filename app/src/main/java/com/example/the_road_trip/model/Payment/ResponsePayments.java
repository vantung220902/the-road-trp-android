package com.example.the_road_trip.model.Payment;

import com.example.the_road_trip.model.ResponseData;

import java.util.List;

public class ResponsePayments extends ResponseData {
    private List<Payment> data;

    public ResponsePayments(int code, Boolean successful, String message, List<Payment> data) {
        super(code, successful, message);
        this.data = data;
    }

    public List<Payment> getData() {
        return data;
    }

    public void setData(List<Payment> data) {
        this.data = data;
    }
}
