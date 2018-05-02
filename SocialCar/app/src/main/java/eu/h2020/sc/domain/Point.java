/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import eu.h2020.sc.SocialCarApplication;

/**
 * @author fminori
 */
public class Point implements Serializable {

    @Expose
    private double lat;
    @Expose
    private double lon;

    private String address;

    public Point() {
    }

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Point(double lat, double lon, String address) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Point fromJson(String jsonPoint) {
        return SocialCarApplication.getGson().fromJson(jsonPoint, Point.class);
    }

    public String toJson() {
        return SocialCarApplication.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", address='" + address + '\'' +
                '}';
    }
}
