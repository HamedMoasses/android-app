/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author fminori
 */
public class PublicTransport extends Transport {

    public static final String SHORT_NAME = "short_name";
    public static final String LONG_NAME = "long_name";

    @SerializedName(value = SHORT_NAME)
    @Expose
    private String shortName;
    @SerializedName(value = LONG_NAME)
    @Expose
    private String longName;

    public PublicTransport(TravelMode travelMode, String shortName, String longName) {
        super();
        this.travelMode = travelMode;
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    @Override
    public String toString() {
        return "PublicTransport{" +
                "travelMode=" + travelMode +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                '}';
    }
}
