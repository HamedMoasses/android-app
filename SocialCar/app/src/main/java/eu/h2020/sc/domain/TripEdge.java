package eu.h2020.sc.domain;


import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 *         Questo classe descrive un'estremità di un trip
 */
public class TripEdge {

    private GeoPoint geoPoint;
    private String address;
    private Marker marker;

    public TripEdge(GeoPoint geoPoint, String address) {
        this.geoPoint = geoPoint;
        this.address = address;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public static boolean validateTripEdges(TripEdge startEdge, TripEdge destinationEdge) {
        return !((startEdge == null) || (startEdge.getGeoPoint() == null) || (startEdge.getGeoPoint().equals(destinationEdge.getGeoPoint())));
    }
}
