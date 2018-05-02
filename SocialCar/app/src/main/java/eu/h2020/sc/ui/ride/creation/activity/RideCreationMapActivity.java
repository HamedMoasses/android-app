package eu.h2020.sc.ui.ride.creation.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.TripEdge;
import eu.h2020.sc.domain.WayPoint;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.ui.ride.creation.fragment.RideCreationMapFragment;
import eu.h2020.sc.utils.Utils;

import static eu.h2020.sc.SocialCarApplication.getContext;

public class RideCreationMapActivity extends GeneralActivity {

    protected List<Road> roads;
    protected OsmMaps osmMaps;

    protected TripEdge startTripEdge;
    protected TripEdge destinationTripEdge;

    protected Marker infoPolylineMarker;

    protected List<WayPoint> wayPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_search);

        initUI();
    }

    private void initUI() {
        initToolBar(true);
        initBack();

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FrameLayout container = (FrameLayout) findViewById(R.id.container);

        RideCreationMapFragment rideCreationMapFragment = new RideCreationMapFragment();
        this.replaceFragment(container.getId(), rideCreationMapFragment, true);
    }


    public List<Road> getRoads() {
        return roads;
    }

    public void setRoads(List<Road> roads) {
        this.roads = roads;
    }

    public void setOsmMaps(OsmMaps osmMaps) {
        this.osmMaps = osmMaps;
    }

    public OsmMaps getOsmMaps() {
        return osmMaps;
    }

    public TripEdge getStartTripEdge() {
        return startTripEdge;
    }

    public void setStartTripEdge(TripEdge startTripEdge) {
        this.startTripEdge = startTripEdge;
    }

    public TripEdge getDestinationTripEdge() {
        return destinationTripEdge;
    }

    public void setDestinationTripEdge(TripEdge destinationTripEdge) {
        this.destinationTripEdge = destinationTripEdge;
    }

    public List<WayPoint> getWayPoints() {
        if (this.wayPoints != null)
            return wayPoints;
        else
            return null;
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public void removeInfoMarker() {
        this.infoPolylineMarker.closeInfoWindow();
        this.osmMaps.removeMarker(this.infoPolylineMarker);
    }

    /**
     * This method allow to show roads on map (N.B. no polyline click supported)
     */
    public void showRoads() {

        Polyline polylineBodySelected = null;
        Polyline polylineBodyUnSelected;

        for (int i = this.roads.size() - 1; i >= 0; i--) {

            Road road = this.roads.get(i);

            if (i == 0) {
                Polyline polylineBorder = this.osmMaps.addBorderPolyline(RoadManager.buildRoadOverlay(this.roads.get(i)).getPoints());
                polylineBorder.setColor(ContextCompat.getColor(getContext(), R.color.polyline_selected_border));

                polylineBodySelected = this.osmMaps.addBodyPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBodySelected.setColor(ContextCompat.getColor(getContext(), R.color.polyline_selected_body));

                GeoPoint polyLineCenter = new GeoPoint(polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLatitude(), polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLongitude());

                this.infoPolylineMarker = this.osmMaps.addMarker(polyLineCenter.getLatitude(), polyLineCenter.getLongitude(), false, Road.getLengthDurationText(SocialCarApplication.getContext(), road.mLength, road.mDuration), R.layout.custom_info_adapter);
                this.infoPolylineMarker.setInfoWindowAnchor((float) 0.5, 1);
                this.infoPolylineMarker.showInfoWindow();

            } else {
                Polyline polylineBorder = this.osmMaps.addBorderPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBorder.setColor(ContextCompat.getColor(getContext(), R.color.polyline_unselected_border));

                polylineBodyUnSelected = this.osmMaps.addBodyPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBodyUnSelected.setColor(ContextCompat.getColor(getContext(), R.color.polyline_unselected_body));

            }
        }

        this.osmMaps.addPolylineEdge(polylineBodySelected);

        if (this.startTripEdge != null) {
            this.osmMaps.removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(this.startTripEdge.getGeoPoint(), this.startTripEdge.getAddress());
            Marker startMarker = this.osmMaps.addMarker(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_starting_point, false);
            this.startTripEdge.setMarker(startMarker);
        }

        if (this.destinationTripEdge != null) {
            this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());

            this.destinationTripEdge = new TripEdge(this.destinationTripEdge.getGeoPoint(), this.destinationTripEdge.getAddress());
            Marker destinationMarker = this.osmMaps.addMarker(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_destination_point, false);
            this.destinationTripEdge.setMarker(destinationMarker);
        }
    }

    public void addWayPointsOnMap() {
        if (this.wayPoints != null) {

            for (WayPoint wayPoint : this.wayPoints) {
                this.osmMaps.addMarker(wayPoint.getLatitude(), wayPoint.getLongitude(), R.mipmap.ic_pin_waypoint, false);
            }
        }
    }


    public void showProgressDialog() {
        super.showProgressDialog();
    }

    public void dismissDialog() {
        super.dismissDialog();
    }
}
