package com.example.the_road_trip.model.Comment;

import com.example.the_road_trip.model.User.User;

public class Comment {
    private String _id;
    private String body;
    private User userId;
    private String postId;
    private int time_created;

    public Comment(String _id, String body, User userId, String postId, int time_created) {
        this._id = _id;
        this.body = body;
        this.userId = userId;
        this.postId = postId;
        this.time_created = time_created;
    }

    public Comment(String body,String postId) {
        this.body = body;
        this.postId = postId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
