package eu.h2020.sc.domain.report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pietro on 20/07/2017.
 */
public class Location implements Serializable {

    @Expose
    @SerializedName("address")
    private String address;

    @Expose
    @SerializedName("geometry")
    private Geometry geometry;

    public Location(String address, Geometry geometry) {
        this.address = address;
        this.geometry = geometry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return "Location{" +
                "address='" + address + '\'' +
                ", geometry=" + geometry +
                '}';
    }
}
