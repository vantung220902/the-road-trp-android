package com.example.the_road_trip.model.Post;

import com.example.the_road_trip.model.User.User;

import java.io.Serializable;

public class Post implements Serializable {
    private String _id;
    private String title;
    private String image;
    private User userId;
    private int rating;
    private int time_created;
    public Post(String _id,String title, String image, int time_created, int rating) {
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.time_created = time_created;
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public int getTime_created() {
        return time_created;
    }

    public void setTime_created(int time_created) {
        this.time_created = time_created;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}
