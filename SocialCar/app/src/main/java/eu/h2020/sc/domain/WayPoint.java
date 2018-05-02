package eu.h2020.sc.domain;

import android.location.Address;

import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class WayPoint {

    private Address address;

    public WayPoint(Address address) {
        this.address = address;
    }

    public String getAddress() {
        return OsmGeocodingUtils.convertAddressToString(this.address);
    }

    public double getLatitude() {
        return this.address.getLatitude();
    }

    public double getLongitude() {
        return this.address.getLongitude();
    }
}
