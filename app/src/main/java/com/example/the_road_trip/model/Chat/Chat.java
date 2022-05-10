package com.example.the_road_trip.model.Chat;

import com.example.the_road_trip.model.User.User;

public class Chat {
    private String _id;
    private String sender;
    private User receiver;
    private int messageType;
    private String body;
    private int time_created;

    public Chat(String _id, int time_created, String sender, User receiver,String body,int messageType) {
        this._id = _id;
        this.time_created = time_created;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
        this.messageType = messageType;
    }

    public Chat(String body,User receiver) {
        this.body = body;
        this.receiver = receiver;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getTime_created() {
        return time_created;
    }

    public void setTime_created(int time_created) {
        this.time_created = time_created;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
