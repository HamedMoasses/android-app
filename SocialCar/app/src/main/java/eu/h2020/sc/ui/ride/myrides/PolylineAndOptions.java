package eu.h2020.sc.ui.ride.myrides;

import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;

import java.util.List;

public class PolylineAndOptions {

    public static final float POLYLINE_BORDER_WIDTH = 18;
    public static final float POLYLINE_BODY_WIDTH = 7;

    private GoogleMap googleMap;

    public Polyline addPolyline(List<LatLng> latLngs, int colorBody, int colorBorder, float zIndex) {

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs);
        polylineOptions.clickable(true);
        polylineOptions.zIndex(zIndex);

        Polyline polylineBorder = this.googleMap.addPolyline(polylineOptions);
        Polyline polylineBody = this.googleMap.addPolyline(polylineOptions);

        polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), colorBorder));
        polylineBody.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), colorBody));

        polylineBorder.setWidth(PolylineAndOptions.POLYLINE_BORDER_WIDTH);
        polylineBody.setWidth(PolylineAndOptions.POLYLINE_BODY_WIDTH);

        return polylineBody;
    }

    public void addMarker(LatLng point, int imageRes, int zIndex) {
        this.googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(imageRes)).zIndex(zIndex));
    }

    public void addPolylineEdge(Polyline polyline, int zIndex) {
        this.googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.img_polyline_edge_card))
                .position(polyline.getPoints().get(0))
                .zIndex(zIndex)
                .anchor((float) 0.5, (float) 0.5));

        this.googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.img_polyline_edge_card))
                .position(polyline.getPoints().get(polyline.getPoints().size() - 1))
                .zIndex(0)
                .anchor((float) 0.5, (float) 0.5));
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
