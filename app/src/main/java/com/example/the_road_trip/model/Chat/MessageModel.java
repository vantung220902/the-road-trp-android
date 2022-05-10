package com.example.the_road_trip.model.Chat;

import java.util.Date;

public class MessageModel  {
    public String message;
    public int messageType;
    public Date messageTime = new Date();
    public String avatar_url;
    // Constructor
    public MessageModel(String message, int messageType,String avatar_url) {
        this.message = message;
        this.messageType = messageType;
        this.avatar_url = avatar_url;
    }

    public MessageModel(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
    }
}
