package eu.h2020.sc.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pietro on 16/09/16.
 */
public class Country {

    private String timeZoneId;
    private LatLng center;

    public Country(String timeZoneId, LatLng center) {
        this.timeZoneId = timeZoneId;
        this.center = center;
    }

    public LatLng getCenter() {
        return center;
    }
}
