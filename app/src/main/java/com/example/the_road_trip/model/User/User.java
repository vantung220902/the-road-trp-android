package com.example.the_road_trip.model.User;

import com.example.the_road_trip.model.ResponseData;

import java.io.Serializable;

public class User extends ResponseData implements Serializable {

    private String _id;
    private String email;
    private String password;
    private String fullName;
    private String address;
    private String avatar_url;
    private String accessToken;
    private String refreshToken;
    private int exp;
    private int iat;

    public User(String _id, String email, String fullName, String address, String avatar_url, String
            accessToken, String refreshToken, int code, Boolean successful, String message, int exp,
                int iat) {
        super(code, successful, message);
        this._id = _id;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.avatar_url = avatar_url;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.exp = exp;
        this.iat = iat;
    }

    public User(String _id,String email, String fullName, String avatar_url,String address) {
        this._id = _id;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.avatar_url = avatar_url;
    }

    public User(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getIat() {
        return iat;
    }

    public void setIat(int iat) {
        this.iat = iat;
    }

    @Override
    public String toString() {
        return "User{" +
                "code=" + code +
                ", successful=" + successful +
                ", message='" + message + '\'' +
                ", _id='" + _id + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
