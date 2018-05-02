/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author fminori
 */
public class Route implements Serializable {

    @SerializedName(value = "start_point")
    @Expose
    private TimeSpacePoint startPoint;

    @SerializedName(value = "end_point")
    @Expose
    private TimeSpacePoint endPoint;

    public Route(TimeSpacePoint startPoint, TimeSpacePoint endPoint) {
        super();
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public TimeSpacePoint getStartPoint() {
        return startPoint;
    }

    public TimeSpacePoint getEndPoint() {
        return endPoint;
    }

    @Override
    public String toString() {
        return "Route{" +
                "startTimeSpacePoint=" + startPoint +
                ", endTimeSpacePoint=" + endPoint +
                '}';
    }
}