/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fminori
 */
public class TimeSpacePoint implements Serializable {

    @Expose
    private Point point;
    @Expose
    private Date date;
    @Expose
    private String address;

    public TimeSpacePoint(Point point, Date date) {
        super();
        this.point = point;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "TimeSpacePoint{" +
                "point=" + point +
                ", date=" + date +
                ", address='" + address + '\'' +
                '}';
    }
}
