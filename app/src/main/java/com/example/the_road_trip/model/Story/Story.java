package com.example.the_road_trip.model.Story;
import com.example.the_road_trip.model.User.User;

import java.io.Serializable;

public class Story implements Serializable {
    private String _id;
    private String image;
    private User userId;
    private String title;
    private int time_created;

    public Story(String _id,String image, User userId, String title, int time_created) {
        this.image = image;
        this.userId = userId;
        this.title = title;
        this.time_created = time_created;
        this._id = _id;
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

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userID) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTime_created() {
        return time_created;
    }

    public void setTime_created(int time_created) {
        this.time_created = time_created;
    }
}
