package eu.h2020.sc.domain.google.direction;


import com.google.android.gms.maps.model.LatLng;
import eu.h2020.sc.utils.PolylineEncoding;

import java.util.List;


/**
 * Created by Pietro on 01/09/16.
 */
public class EncodedPolyline {

    private String points;

    public EncodedPolyline(String encodedPoints) {
        this.points = encodedPoints;
    }

    public EncodedPolyline(List<LatLng> points) {
        this.points = PolylineEncoding.encode(points);
    }

    public String getEncodedPath() {
        return points;
    }

    public List<LatLng> decodePath() {
        return PolylineEncoding.decode(points);
    }

}
