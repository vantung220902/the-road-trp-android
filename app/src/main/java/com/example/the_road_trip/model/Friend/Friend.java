package com.example.the_road_trip.model.Friend;

public class Friend {
    private String _id;
    private String sender;
    private String receiver;
    private int status;

    public Friend(String sender, String receiver, int status,String _id) {
        this._id = _id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
