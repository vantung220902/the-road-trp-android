package com.example.the_road_trip.model.Payment;

import com.example.the_road_trip.model.ResponseData;

public class ResponseInsertPayment extends ResponseData {
    private Payment data;

    public ResponseInsertPayment(int code, Boolean successful, String message, Payment data) {
        super(code, successful, message);
        this.data = data;
    }

    public Payment getData() {
        return data;
    }

    public void setData(Payment data) {
        this.data = data;
    }
}
