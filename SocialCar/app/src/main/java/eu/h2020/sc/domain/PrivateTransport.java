/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author alam
 */
public class PrivateTransport extends Transport {

    public static final String RIDE_ID = "ride_id";
    public static final String CAR = "car";
    public static final String DRIVER = "driver";
    public static final String PUBLIC_URI = "public_uri";

    @SerializedName(value = RIDE_ID)
    @Expose
    private String rideID;
    @Expose
    private Car car;

    @Expose
    private User driver;

    @Expose
    @SerializedName(value = PUBLIC_URI)
    private String publicURI;

    @Deprecated
    public PrivateTransport() {
    }

    public PrivateTransport(TravelMode travelMode, String rideID, Car car, User driver) {
        super();
        this.travelMode = travelMode;
        this.rideID = rideID;
        this.car = car;
        this.driver = driver;
    }


    public Car getCar() {
        return car;
    }

    public User getDriver() {
        return this.driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }


    public String getRideID() {
        return rideID;
    }

    public String getPublicURI() {
        return publicURI;
    }

    public void setPublicURI(String publicURI) {
        this.publicURI = publicURI;
    }

    @Override
    public String toString() {
        return "PrivateTransport{" +
                "rideID='" + rideID + '\'' +
                ", car=" + car +
                ", driver=" + driver +
                ", publicURI='" + publicURI + '\'' +
                '}';
    }
}
