package eu.h2020.sc.ui.trip.recording;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.Serializable;
import java.util.HashMap;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.map.MapCallback;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationFormActivity;
import eu.h2020.sc.ui.trip.recording.task.AddressPositionTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.PolylineEncoding;

import static eu.h2020.sc.SocialCarApplication.getContext;

public class TripRecordedMapActivity extends GeneralActivity implements MapCallback {

    protected static final String RECORDED_TRIP = "RECORDED_TRIP";

    private Road road;
    private OsmMaps osmMaps;
    private GeoPoint startPoint;
    private GeoPoint destinationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_recorded_map);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        this.osmMaps = new OsmMaps((MapView) this.findViewById(R.id.mapView), this, this);
        this.road = (Road) ActivityUtils.getParcelableFromIntent(this, RECORDED_TRIP);

        initUI();
    }

    private void initUI() {
        setTitle(getString(R.string.trip_preview));
        initToolBar(false);
        initBack();
        drawOnOsmMap();
    }

    private void drawOnOsmMap() {
        Polyline polylineBorder = this.osmMaps.addBorderPolyline(RoadManager.buildRoadOverlay(this.road).getPoints());
        polylineBorder.setColor(ContextCompat.getColor(getContext(), R.color.polyline_selected_border));

        Polyline polylineBodySelected = this.osmMaps.addBodyPolyline(RoadManager.buildRoadOverlay(this.road).getPoints());
        polylineBodySelected.setColor(ContextCompat.getColor(getContext(), R.color.polyline_selected_body));

        GeoPoint polyLineCenter = new GeoPoint(polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLatitude(), polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLongitude());
        Marker infoPolylineMarker = this.osmMaps.addMarker(polyLineCenter.getLatitude(), polyLineCenter.getLongitude(), false, Road.getLengthDurationText(getContext(), road.mLength, road.mDuration), R.layout.custom_info_adapter);
        infoPolylineMarker.setInfoWindowAnchor((float) 0.5, 1);
        infoPolylineMarker.showInfoWindow();

        this.startPoint = polylineBodySelected.getPoints().get(0);
        this.destinationPoint = polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() - 1);

        this.osmMaps.addMarker(startPoint.getLatitude(), startPoint.getLongitude(), R.mipmap.ic_starting_point, null);
        this.osmMaps.addMarker(destinationPoint.getLatitude(), destinationPoint.getLongitude(), R.mipmap.ic_destination_point, null);
    }


    @Override
    public void onMapReady() {
    }

    public void actionGoToRideCreationFormActivity(View view) {
        showProgressDialog();
        AddressPositionTask addressPositionTask = new AddressPositionTask(this, this.startPoint, this.destinationPoint);
        addressPositionTask.execute();
    }

    public void goToRideCreationFormActivity(String startAddressName, String destinationAddressName) {
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(RideCreationFormActivity.KEY_START_POINT, new Point(this.startPoint.getLatitude(), this.startPoint.getLongitude(), startAddressName));
        params.put(RideCreationFormActivity.KEY_END_POINT, new Point(this.destinationPoint.getLatitude(), this.destinationPoint.getLongitude(), destinationAddressName));
        params.put(RideCreationFormActivity.KEY_POLYLINE, PolylineEncoding.encodeOSM(RoadManager.buildRoadOverlay(this.road).getPoints()));
        ActivityUtils.openActivityWithObjectParams(this, RideCreationFormActivity.class, params);
    }
}