package com.example.the_road_trip.model.User;

public class RefreshToken {
    private boolean success;
    private String accessToken;

    public RefreshToken(boolean success, String accessToken) {
        this.success = success;
        this.accessToken = accessToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
