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

public class Transport implements Serializable {

    public static final String TRAVEL_MODE = "travel_mode";
    public static final String CAR_POOLING = "CAR_POOLING";

    @SerializedName(value = TRAVEL_MODE)
    @Expose
    protected TravelMode travelMode;

    public TravelMode getTravelMode() {
        return travelMode;
    }

}