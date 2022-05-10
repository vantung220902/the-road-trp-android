package com.example.the_road_trip.model.Friend;

import com.example.the_road_trip.model.User.User;

public class Inviting {
    private String _id;
    private User sender;
    private String receiver;
    private int status;
    private int time_created;
    public Inviting(String _id, User sender, String receiver, int status,int time_created) {
        this._id = _id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.time_created = time_created;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime_created() {
        return time_created;
    }

    public void setTime_created(int time_created) {
        this.time_created = time_created;
    }
}
