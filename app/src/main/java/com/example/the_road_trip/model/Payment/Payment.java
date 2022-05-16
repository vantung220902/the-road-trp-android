package com.example.the_road_trip.model.Payment;

import com.example.the_road_trip.model.Ticket.Ticket;
import com.example.the_road_trip.model.User.User;

import java.io.Serializable;

public class Payment implements Serializable {
    private String _id;
    private Ticket ticket;
    private int number;
    private float sum;
    private User userId;

    public Payment(String _id, Ticket ticket, int number, float sum, User userId) {
        this._id = _id;
        this.ticket = ticket;
        this.number = number;
        this.sum = sum;
        this.userId = userId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}
