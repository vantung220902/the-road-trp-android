package com.example.the_road_trip.model.Ticket;

import com.example.the_road_trip.model.User.User;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String _id;
    private String image;
    private String address;
    private String name;
    private String date;
    private float price;
    private String description;
    private int rest;
    private User author;

    public Ticket(String _id, String image, String address, String name, String date, float price, String description, int rest, User author) {
        this._id = _id;
        this.image = image;
        this.address = address;
        this.name = name;
        this.date = date;
        this.price = price;
        this.description = description;
        this.rest = rest;
        this.author = author;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
