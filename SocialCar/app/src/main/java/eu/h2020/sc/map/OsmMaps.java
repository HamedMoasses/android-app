package eu.h2020.sc.map;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Country;
import eu.h2020.sc.domain.WayPoint;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.utils.Utils;


/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class OsmMaps {

    private static final String TAG = OsmMaps.class.getCanonicalName();

    private static final float POLYLINE_BORDER_WIDTH = 16;
    private static final float POLYLINE_BODY_WIDTH = 5;

    private static final float EUROPE_LAT = (float) 48.6908333333;
    private static final float EUROPE_LON = (float) 9.14055555556;

    private static final int EUROPE_MAP_ZOOM = 4;
    private static final int COUNTRY_MAP_ZOOM = 8;

    private static final int DEFAULT_MAP_ZOOM = 18;

    private Context context;
    private MapView map;
    private IMapController mapController;
    private List<Polyline> polylineList;

    protected MapCallback mapCallback;

    public OsmMaps(MapView mapView, Context context, MapCallback mapCallback) {
        this.map = mapView;
        this.context = context;
        this.mapCallback = mapCallback;

        this.polylineList = new ArrayList<>();

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMaxZoomLevel(20);
        mapView.setClickable(true);
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        this.centerMap();
        this.mapCallback.onMapReady();
    }

    public OsmMaps(MapView mapView, Context context) {
        this.map = mapView;
        this.context = context;
        this.polylineList = new ArrayList<>();

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMaxZoomLevel(DEFAULT_MAP_ZOOM);
        mapView.setMinZoomLevel(13);
        mapView.setClickable(false);
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(false);
        mapView.setBuiltInZoomControls(false);

        this.mapController = this.map.getController();
        this.mapController.setZoom(DEFAULT_MAP_ZOOM);

        GeoPoint position = new GeoPoint(EUROPE_LAT, EUROPE_LON);
        this.mapController.animateTo(position);
    }

    private void centerMap() {
        String currentTimeZoneID = TimeZone.getDefault().getID();
        Country currentCountry = AvailableCountriesStore.getInstance().retrieveCountryFromTimeZone(currentTimeZoneID);

        int mapZoom;
        GeoPoint position;

        if (currentCountry != null) {
            mapZoom = COUNTRY_MAP_ZOOM;
            position = new GeoPoint(currentCountry.getCenter().latitude, currentCountry.getCenter().longitude);
        } else {
            mapZoom = EUROPE_MAP_ZOOM;
            position = new GeoPoint(EUROPE_LAT, EUROPE_LON);
        }

        this.mapController = this.map.getController();
        this.mapController.setZoom(mapZoom);
        this.mapController.animateTo(position);
    }

    public void moveCamera(GeoPoint position) {
        this.mapController.setZoom(DEFAULT_MAP_ZOOM);
        this.mapController.animateTo(position);
    }

    public Marker addMarker(double latitude, double longitude, boolean moveCamera, String title, Integer layoutResID) {

        GeoPoint position = new GeoPoint(latitude, longitude);

        Marker marker = new Marker(this.map, this.context);
        marker.setDraggable(false);
        marker.setTitle(title);
        marker.setPosition(position);
        marker.setAlpha(0);
        marker.setInfoWindow(new MarkerInfoWindow(layoutResID, this.map));

        this.map.getOverlays().add(marker);

        if (moveCamera)
            this.moveCamera(position);

        return marker;
    }

    public Marker addMarker(double latitude, double longitude, Integer iconReference) {
        return this.addMarker(latitude, longitude, iconReference, true, null);
    }

    public Marker addMarker(double latitude, double longitude, Integer iconReference, boolean moveCamera) {
        return this.addMarker(latitude, longitude, iconReference, moveCamera, null);
    }

    public Marker addMarker(double latitude, double longitude, Integer iconReference, Marker.OnMarkerClickListener onMarkerClickListener) {
        return this.addMarker(latitude, longitude, iconReference, true, null, onMarkerClickListener);
    }

    public Marker addMarker(double latitude, double longitude, Integer iconReference, boolean moveCamera, Marker.OnMarkerClickListener onMarkerClickListener) {
        return this.addMarker(latitude, longitude, iconReference, moveCamera, null, onMarkerClickListener);
    }

    public Marker addMarker(double latitude, double longitude, Integer iconReference, boolean moveCamera, String snippet, Marker.OnMarkerClickListener onMarkerClickListener) {

        GeoPoint position = new GeoPoint(latitude, longitude);

        Marker marker = new Marker(this.map, this.context);
        marker.setDraggable(false);
        marker.setSnippet(snippet);
        marker.setIcon(ContextCompat.getDrawable(this.context, iconReference));
        marker.setPosition(position);
        marker.setInfoWindow(null);
        marker.setOnMarkerClickListener(onMarkerClickListener);

        this.map.getOverlays().add(marker);

        if (moveCamera)
            this.moveCamera(position);

        return marker;
    }

    public Polyline addBodyPolyline(List<GeoPoint> points) {

        Polyline polylineBody = new Polyline();
        polylineBody.setPoints(points);
        polylineBody.setWidth(POLYLINE_BODY_WIDTH);

        this.map.getOverlays().add(polylineBody);
        this.polylineList.add(polylineBody);

        this.map.invalidate();

        return polylineBody;
    }

    public Polyline addBorderPolyline(List<GeoPoint> points) {

        Polyline polylineBorder = new Polyline();
        polylineBorder.setPoints(points);
        polylineBorder.setWidth(POLYLINE_BORDER_WIDTH);

        this.map.getOverlays().add(polylineBorder);
        this.polylineList.add(polylineBorder);

        this.map.invalidate();

        return polylineBorder;
    }

    public void addPolylineEdge(Polyline polyline) {
        Marker markerStartPoint = addMarker(polyline.getPoints().get(0).getLatitude(), polyline.getPoints().get(0).getLongitude(), R.mipmap.img_polyline_edge_card, false, null, null);
        markerStartPoint.setAnchor((float) 0.5, (float) 0.5);

        Marker markerArrivingPoint = addMarker(polyline.getPoints().get(polyline.getPoints().size() - 1).getLatitude(), polyline.getPoints().get(polyline.getPoints().size() - 1).getLongitude(), R.mipmap.img_polyline_edge_card, false, null, null);
        markerArrivingPoint.setAnchor((float) 0.5, (float) 0.5);
    }

    public void clear() {
        if (this.map != null)
            this.map.getOverlays().clear();
    }

    public void zoomOutPlaces(GeoPoint pointA, GeoPoint pointB) {
        this.zoomOutPlaces(pointA, pointB, null, null);
    }

    public void zoomOutPlaces(GeoPoint pointA, GeoPoint pointB, List<WayPoint> additionalsWayPoints) {
        this.zoomOutPlaces(pointA, pointB, additionalsWayPoints, null);
    }

    public void zoomOutPlaces(GeoPoint pointA, GeoPoint pointB, List<WayPoint> additionalsWayPoints, List<GeoPoint> polylinePoints) {
        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(pointA);

        if (additionalsWayPoints != null) {
            for (WayPoint wayPoint : additionalsWayPoints) {
                wayPoints.add(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));
            }
        }

        if (polylinePoints != null) {
            wayPoints.addAll(polylinePoints);
        }

        wayPoints.add(pointB);

        BoundingBox boundingBox = BoundingBox.fromGeoPoints(wayPoints);
        this.map.zoomToBoundingBox(boundingBox, false);
        this.map.invalidate();
    }


    public List<Polyline> getPolylineList() {
        return Collections.unmodifiableList(this.polylineList);
    }

    public List<Overlay> getOverlays() {
        return this.map.getOverlays();
    }

    public void removeAllPolyline() {
        this.polylineList.clear();
    }

    public MapView getMap() {
        return map;
    }

    public void setOnMapClickListener(MapEventsReceiver onMapClickListener) {

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(onMapClickListener);

        this.map.getOverlays().remove(mapEventsOverlay);

        this.map.getOverlays().add(0, mapEventsOverlay);
        this.map.invalidate();
    }

    public void removeMarker(Marker marker) {
        marker.remove(this.map);
        this.map.invalidate();
    }

    public List<Road> getDirections(GeoPoint startPoint, GeoPoint destinationPoint, List<GeoPoint> additionalWayPoints) throws ServerException, NotFoundException, ConnectionException {

        if (!Utils.hasInternetConnection(SocialCarApplication.getContext())) {
            throw new ConnectionException();
        }

        RoadManager roadManager = new MapQuestRoadManager(SocialCarApplication.getContext().getString(R.string.mapquest_api_key));

        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(startPoint);

        if (additionalWayPoints != null) {
            wayPoints.addAll(additionalWayPoints);
        }
        wayPoints.add(destinationPoint);

        List<Road> roads = Arrays.asList(roadManager.getRoads(wayPoints));

        if (roads.size() == 0) {
            throw new NotFoundException("Error during OSM get directions.... No routes found!");
        }

        if (roads.get(0).mStatus != Road.STATUS_OK)
            throw new ServerException("Error during OSM get directions....");

        return roads;
    }

    public void setOnPolylineClickListener(Polyline polyline, Polyline.OnClickListener polylineListener) {
        polyline.setOnClickListener(polylineListener);
    }
}
