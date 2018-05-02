package eu.h2020.sc.map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;


/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class Map implements OnMapReadyCallback {

    private static final float POLYLINE_BORDER_WIDTH = 25;
    private static final float POLYLINE_BODY_WIDTH = 14;

    private static final float EUROPE_LAT = (float) 48.6908333333;
    private static final float EUROPE_LON = (float) 9.14055555556;

    private static final float EUROPE_MAP_ZOOM = (float) 3.5;
    private static final float COUNTRY_MAP_ZOOM = (float) 5;

    protected MapCallback callBack;

    private GoogleMap googleMap;
    private List<Polyline> polylineList;



    public Map(Bundle savedInstanceState, MapView mapView, Context context, MapCallback callBack) {
        this.callBack = callBack;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        this.polylineList = new ArrayList<>();

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.centerMap();
        this.callBack.onMapReady();
    }

    public void clear() {
        this.googleMap.clear();
    }

    public Marker addMarker(double latitude, double longitude, int iconReference) {
        MarkerOptions markerOptions = new MarkerOptions().position(
                new LatLng(latitude, longitude));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(iconReference));

        Marker marker = this.googleMap.addMarker(markerOptions);

        moveCamera(new LatLng(latitude, longitude));
        return marker;
    }

    public void moveCamera(LatLng position) {
        int DEFAULT_MAP_ZOOM = 15;
        int DEFAULT_MAP_TILT = 20;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(DEFAULT_MAP_ZOOM).tilt(DEFAULT_MAP_TILT).build();
        this.googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void zoomOutPlaces(LatLng latLng1, LatLng latLng2) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng1);
        builder.include(latLng2);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
    }

    public Polyline addPolyline(List<LatLng> listLatLng, int colorBody, int colorBorder, float zIndex) {

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(listLatLng);
        polylineOptions.clickable(true);
        polylineOptions.zIndex(zIndex);

        Polyline polylineBorder = this.googleMap.addPolyline(polylineOptions);
        Polyline polylineBody = this.googleMap.addPolyline(polylineOptions);

        polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), colorBorder));
        polylineBody.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), colorBody));

        polylineBorder.setWidth(POLYLINE_BORDER_WIDTH);
        polylineBody.setWidth(POLYLINE_BODY_WIDTH);

        this.polylineList.add(polylineBody);
        this.polylineList.add(polylineBorder);

        return polylineBody;
    }

    public void removeAllPolyline() {
        for (Polyline line : polylineList) {
            line.remove();
        }
        this.polylineList.clear();
    }

    public List<Polyline> getPolylineList() {
        return Collections.unmodifiableList(polylineList);
    }


    public void centerMap() {
        String currentTimeZoneID = TimeZone.getDefault().getID();
        Country currentCountry = AvailableCountriesStore.getInstance().retrieveCountryFromTimeZone(currentTimeZoneID);

        float mapZoom;
        LatLng position;

        if (currentCountry != null) {
            mapZoom = COUNTRY_MAP_ZOOM;
            position = currentCountry.getCenter();
        } else {
            mapZoom = EUROPE_MAP_ZOOM;
            position = new LatLng(EUROPE_LAT, EUROPE_LON);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(mapZoom).build();

        this.googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        googleMap.setOnMapClickListener(onMapClickListener);
    }

    public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener) {
        googleMap.setOnMarkerClickListener(onMarkerClickListener);
    }

}
