package com.example.the_road_trip.model;

public class ResponseData {
    protected int code;
    protected Boolean successful;
    protected String message;
    public int getCode() {
        return code;
    }

    public ResponseData(int code, Boolean successful, String message) {
        this.code = code;
        this.successful = successful;
        this.message = message;
    }

    public ResponseData() {
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "code=" + code +
                ", successful=" + successful +
                ", message='" + message + '\'' +
                '}';
    }
}
