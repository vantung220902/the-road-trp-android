package com.example.the_road_trip.model;

import java.io.Serializable;

public class ModelLink implements Serializable {
    private String announcement;
    private Class<?> cls;

    public ModelLink(String announcement, Class<?> cls) {
        this.announcement = announcement;
        this.cls = cls;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }
}
