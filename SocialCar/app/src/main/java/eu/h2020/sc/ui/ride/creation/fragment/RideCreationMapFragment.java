package eu.h2020.sc.ui.ride.creation.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.TripEdge;
import eu.h2020.sc.domain.WayPoint;
import eu.h2020.sc.location.PositionManager;
import eu.h2020.sc.location.PositionManagerCallback;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.map.MapCallback;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.tasks.AddressGeocodingTask;
import eu.h2020.sc.tasks.PositionGeocodingTask;
import eu.h2020.sc.ui.home.trip.search.GoogleLatLngBoundsTask;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationFormActivity;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationMapActivity;
import eu.h2020.sc.ui.ride.creation.task.OSMDirectionTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.PolylineEncoding;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;
import eu.h2020.sc.utils.WidgetHelper;


public class RideCreationMapFragment extends Fragment implements MapCallback, PositionManagerCallback, View.OnClickListener, ResultCallback<LocationSettingsResult>, Polyline.OnClickListener {

    public static final int ID_REQ_POSITION_MARK_A = 1;
    public static final int ID_REQ_POSITION_MY = 2;

    private static final String TAG = RideCreationMapFragment.class.getSimpleName();

    private final int REQUEST_CODE_ADDRESS_START = 1;
    private final int REQUEST_CODE_ADDRESS_DESTINATION = 2;
    private final int REQUEST_CODE_ASK_LOCATION = 3;

    private final int ID_PHASE_REVIEW = 3;
    private final int ID_REQUEST_PERMISSION = 1;

    protected TripEdge startTripEdge;
    protected TripEdge destinationTripEdge;

    private View fragmentView;
    private Button goNextStepButton;
    private ImageView myLocationImageView;
    private LinearLayout searchFormLinearLayout;

    //start point elements
    private RelativeLayout startAddressRelativeLayout;
    private TextViewCustom textViewStartPoint;
    private ImageView searchStartImageView;
    private ImageView cancelStartImageView;
    private MaterialRippleLayout cancelStartRippleLayout;
    private View viewSearchDividerStart;
    private MaterialRippleLayout textStartRippleView;

    //destination point elements
    private FrameLayout destinationPointLayout;
    private RelativeLayout destinationAddressRelativeLayout;
    private TextViewCustom textViewDestinationPoint;
    private ImageView searchDestinationImageView;
    private ImageView cancelDestinationImageView;
    private MaterialRippleLayout cancelDestinationRippleLayout;
    private View viewSearchDividerDestination;
    private MaterialRippleLayout textDestinationRippleView;


    //First waypoint elements
    private FrameLayout waypointsFrameLayout;
    private TextViewCustom textViewWaypoints;
    private MaterialRippleLayout textWayPointsRippleView;
    private MaterialRippleLayout rippleLayoutGoToWayPoints;

    private ImageView imageViewAddWayPoints;

    private Menu addWayPointsMenu;

    private Marker myPositionMarker;
    private Marker infoPolylineMarker;

    private PositionManager positionManager;

    private int actualPhase;
    private long timestampClicked = 0;
    private int selectedPathIndex = -1;

    protected OsmMaps osmMaps;
    private List<Road> roads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.fragmentView = inflater.inflate(R.layout.fragment_ride_creation, container, false);

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        this.osmMaps = new OsmMaps((MapView) fragmentView.findViewById(R.id.mapView), getActivity(), this);
        this.roads = new ArrayList<>();

        initUI();
        initEvents();

        setHasOptionsMenu(true);

        return this.fragmentView;
    }

    private void initUI() {

        this.getActivity().setTitle(R.string.path);

        myLocationImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_my_location);
        searchFormLinearLayout = (LinearLayout) this.fragmentView.findViewById(R.id.layout_trip_search);

        //init start elements:
        searchStartImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_search_start);
        cancelStartRippleLayout = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_cancel_start);
        textViewStartPoint = (TextViewCustom) this.fragmentView.findViewById(R.id.text_view_start_point);
        cancelStartImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_cancel_start);
        viewSearchDividerStart = fragmentView.findViewById(R.id.view_search_divider_start);
        textStartRippleView = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_text_start);
        textStartRippleView.setEnabled(true);

        startAddressRelativeLayout = (RelativeLayout) fragmentView.findViewById(R.id.relative_layout_start_address);

        //init destination elements:
        searchDestinationImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_search_destination);
        cancelDestinationRippleLayout = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_cancel_destination);
        textViewDestinationPoint = (TextViewCustom) this.fragmentView.findViewById(R.id.text_view_destination_point);
        destinationPointLayout = (FrameLayout) this.fragmentView.findViewById(R.id.layout_destination_point_text);
        cancelDestinationImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_cancel_destination);
        destinationAddressRelativeLayout = (RelativeLayout) fragmentView.findViewById(R.id.relative_layout_destination_address);
        viewSearchDividerDestination = fragmentView.findViewById(R.id.view_search_divider_destination);
        textDestinationRippleView = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_text_destination);
        textDestinationRippleView.setEnabled(true);

        //init waypoints elements
        waypointsFrameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.layout_waypoints);
        rippleLayoutGoToWayPoints = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_add_waypoints);
        textViewWaypoints = (TextViewCustom) this.fragmentView.findViewById(R.id.text_view_waypoints);
        textWayPointsRippleView = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_waypoints);
        imageViewAddWayPoints = (ImageView) this.fragmentView.findViewById(R.id.image_view_add_way_point);

        goNextStepButton = (Button) this.fragmentView.findViewById(R.id.button_go_next_step);

        ((GeneralActivity) getActivity()).getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initEvents() {
        this.myLocationImageView.setOnClickListener(this);
        this.searchFormLinearLayout.setOnClickListener(this);
        this.cancelStartImageView.setOnClickListener(this);
        this.cancelDestinationImageView.setOnClickListener(this);
        this.goNextStepButton.setOnClickListener(this);
        this.startAddressRelativeLayout.setOnClickListener(this);
        this.destinationAddressRelativeLayout.setOnClickListener(this);

        this.rippleLayoutGoToWayPoints.setOnClickListener(this);
        this.imageViewAddWayPoints.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.positionManager != null) {
            this.positionManager.stopAutoManage(getActivity());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (((RideCreationMapActivity) getActivity()).getRoads() == null)
            this.setStartLayout();

        else {
            this.zoomOutPlace();
            this.showRoads(((RideCreationMapActivity) getActivity()).getRoads(), false);

            ((RideCreationMapActivity) getActivity()).setOsmMaps(this.osmMaps);
            ((RideCreationMapActivity) getActivity()).addWayPointsOnMap();

            this.textViewDestinationPoint.setText(this.destinationTripEdge.getAddress());
            this.textViewStartPoint.setText(this.startTripEdge.getAddress());

            this.waypointsFrameLayout.setVisibility(View.VISIBLE);
            this.textWayPointsRippleView.setEnabled(true);

            this.setReviewLayout();

            String wayPointsLabel = getResources().getQuantityString(R.plurals.waypoints_quantity, ((RideCreationMapActivity) getActivity()).getWayPoints().size(), ((RideCreationMapActivity) getActivity()).getWayPoints().size());

            if (((RideCreationMapActivity) getActivity()).getWayPoints().size() == 0)
                wayPointsLabel = getString(R.string.hint_insert_waypoint);

            this.textViewWaypoints.setText(wayPointsLabel);
        }
    }

    private void zoomOutPlace() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                osmMaps.moveCamera(destinationTripEdge.getGeoPoint());
                osmMaps.zoomOutPlaces(startTripEdge.getGeoPoint(), destinationTripEdge.getGeoPoint(), ((RideCreationMapActivity) getActivity()).getWayPoints(), osmMaps.getPolylineList().get(0).getPoints());
            }
        }, 500);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.addWayPointsMenu = menu;
        inflater.inflate(R.menu.menu_add_waypoints, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_waypoints) {
            this.goToAddWayPoints();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToAddWayPoints() {
        RideAddWayPointsFragment fragment = new RideAddWayPointsFragment();

        ((RideCreationMapActivity) getActivity()).setOsmMaps(this.osmMaps);
        ((RideCreationMapActivity) getActivity()).setRoads(this.roads);
        ((RideCreationMapActivity) getActivity()).setStartTripEdge(this.startTripEdge);
        ((RideCreationMapActivity) getActivity()).setDestinationTripEdge(this.destinationTripEdge);

        ((GeneralActivity) getActivity()).replaceFragment(R.id.container, fragment, true);
    }

    private void setStartLayout() {
        this.actualPhase = 1;

        cancelStartImageView.setOnClickListener(null);

        startAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        startAddressRelativeLayout.setOnClickListener(this);
        textStartRippleView.setEnabled(true);

        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) startAddressRelativeLayout.getLayoutParams();
        params.rightMargin = 0;
        startAddressRelativeLayout.setLayoutParams(params);

        searchStartImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));
        cancelStartRippleLayout.setVisibility(View.GONE);
        destinationPointLayout.setVisibility(View.GONE);

        goNextStepButton.setText(R.string.set_starting_point);

        this.getActivity().setTitle(R.string.starting_point);
    }

    private void setDestinationLayout() {
        cancelStartImageView.setOnClickListener(this);

        startAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        startAddressRelativeLayout.setOnClickListener(null);
        textStartRippleView.setEnabled(false);

        viewSearchDividerDestination.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));
        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) startAddressRelativeLayout.getLayoutParams();
        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        startAddressRelativeLayout.setLayoutParams(params);

        cancelStartRippleLayout.setVisibility(View.VISIBLE);
        destinationPointLayout.setVisibility(View.VISIBLE);

        destinationAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        destinationAddressRelativeLayout.setOnClickListener(this);
        textDestinationRippleView.setEnabled(true);

        params.rightMargin = 0;
        destinationAddressRelativeLayout.setLayoutParams(params);

        searchDestinationImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));
        cancelDestinationRippleLayout.setVisibility(View.GONE);

        goNextStepButton.setText(R.string.set_destination_point);
        this.getActivity().setTitle(R.string.destination_point);
    }

    private void setReviewLayout() {

        this.actualPhase = ID_PHASE_REVIEW;

        cancelStartImageView.setOnClickListener(this);

        startAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        startAddressRelativeLayout.setOnClickListener(null);
        textStartRippleView.setEnabled(false);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) startAddressRelativeLayout.getLayoutParams();
        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        startAddressRelativeLayout.setLayoutParams(params);

        viewSearchDividerDestination.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));
        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));

        cancelStartRippleLayout.setVisibility(View.VISIBLE);
        searchStartImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));

        destinationPointLayout.setVisibility(View.VISIBLE);
        destinationAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        destinationAddressRelativeLayout.setOnClickListener(null);
        textDestinationRippleView.setEnabled(false);

        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        destinationAddressRelativeLayout.setLayoutParams(params);

        cancelDestinationRippleLayout.setVisibility(View.VISIBLE);
        searchDestinationImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));

        goNextStepButton.setText(R.string.label_continue);
        this.getActivity().setTitle(R.string.path);
    }

    private void setModifyStartLayout() {
        this.actualPhase = 4;

        cancelStartImageView.setOnClickListener(null);

        startAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        startAddressRelativeLayout.setOnClickListener(this);
        textStartRippleView.setEnabled(true);

        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) startAddressRelativeLayout.getLayoutParams();
        params.rightMargin = 0;
        startAddressRelativeLayout.setLayoutParams(params);

        searchStartImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));
        cancelStartRippleLayout.setVisibility(View.GONE);

        destinationPointLayout.setVisibility(View.VISIBLE);
        destinationAddressRelativeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        destinationAddressRelativeLayout.setOnClickListener(null);
        textDestinationRippleView.setEnabled(false);

        params.rightMargin = Utils.fromDptoPx(0, getActivity());
        destinationAddressRelativeLayout.setLayoutParams(params);

        cancelDestinationRippleLayout.setVisibility(View.VISIBLE);
        searchDestinationImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));

        goNextStepButton.setText(R.string.set_starting_point);
        this.getActivity().setTitle(R.string.starting_point);
    }

    @Override
    public void onPosition(Location location, int requestID) {
        if (location != null) {
            PositionGeocodingTask positionGeocodingTask = new PositionGeocodingTask(this, requestID, location.getLatitude(), location.getLongitude(), getContext());
            positionGeocodingTask.execute(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_ADDRESS_START)
            startAddressRelativeLayout.setOnClickListener(this);

        else if (requestCode == REQUEST_CODE_ADDRESS_DESTINATION)
            destinationAddressRelativeLayout.setOnClickListener(this);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_ADDRESS_START:
                    Place startPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                    AddressGeocodingTask retrievePositionFromStartAddress = new AddressGeocodingTask(this, 0, getContext());
                    retrievePositionFromStartAddress.execute(startPlace);
                    break;

                case REQUEST_CODE_ADDRESS_DESTINATION:
                    Place destinationPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                    AddressGeocodingTask retrievePositionFromDestinationAddress = new AddressGeocodingTask(this, 1, getContext());
                    retrievePositionFromDestinationAddress.execute(destinationPlace);
                    break;

                case REQUEST_CODE_ASK_LOCATION:
                    centerToMyLocation();
                    break;
            }

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Log.e(TAG, PlaceAutocomplete.getStatus(getActivity(), data).getStatusMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_go_next_step:
                if (startTripEdge == null) {
                    WidgetHelper.showToast(getActivity(), R.string.start_point_not_setted);
                } else {
                    if (destinationTripEdge == null) {
                        this.actualPhase = 2;
                        setDestinationLayout();
                    } else {
                        if (this.actualPhase != ID_PHASE_REVIEW) {
                            setReviewLayout();
                            this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());
                            this.executeDirectionRequest();
                        } else
                            this.validatePoints();
                    }
                }
                break;
            case R.id.relative_layout_start_address:
                startAddressRelativeLayout.setOnClickListener(null);
                launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_START);
                break;
            case R.id.relative_layout_destination_address:
                destinationAddressRelativeLayout.setOnClickListener(null);
                launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_DESTINATION);
                break;

            case R.id.image_view_cancel_start:
                if (this.actualPhase == 5 || this.actualPhase == ID_PHASE_REVIEW) {
                    setModifyStartLayout();
                } else {
                    setStartLayout();
                }
                this.removeAllPaths();
                this.osmMaps.moveCamera(this.startTripEdge.getGeoPoint());
                break;

            case R.id.image_view_cancel_destination:
                this.actualPhase = 5;
                this.setDestinationLayout();
                this.removeAllPaths();
                this.osmMaps.moveCamera(this.destinationTripEdge.getGeoPoint());
                break;

            case R.id.ripple_layout_add_waypoints:
                this.goToAddWayPoints();
                break;

            case R.id.image_view_add_way_point:
                this.goToAddWayPoints();
                break;

            case R.id.image_view_my_location:
                this.centerToMyLocation();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ID_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (this.positionManager == null) {
                    this.positionManager = new PositionManager((GeneralActivity) getActivity(), ID_REQ_POSITION_MY, this);
                } else {
                    this.positionManager.setRequestType(ID_REQ_POSITION_MY);
                    this.positionManager.getLastPosition();
                }
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CODE_ASK_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Error during startResolutionForResult: " + e);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    public void onMapReady() {

        if (((RideCreationMapActivity) getActivity()).getRoads() == null) {

            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) && PositionManager.isGpsEnable(getActivity())) {
                this.positionManager = new PositionManager((GeneralActivity) getActivity(), ID_REQ_POSITION_MARK_A, this);

            } else {
                if (this.osmMaps != null) {

                    String jsonLastLocationPoint = SocialCarApplication.getSharedPreferences().getString(PositionManager.KEY_LAST_KNOWN_POINT, null);

                    if (jsonLastLocationPoint != null) {
                        Point point = Point.fromJson(jsonLastLocationPoint);

                        this.osmMaps.clear();
                        this.textViewStartPoint.setText(point.getAddress());

                        this.startTripEdge = new TripEdge(new GeoPoint(point.getLat(), point.getLon()), point.getAddress());
                        this.startTripEdge.setMarker(this.osmMaps.addMarker(point.getLat(), point.getLon(), R.mipmap.ic_starting_point));
                    }
                }
            }
        }
    }

    @Override
    public boolean onClick(Polyline polylineSelected, MapView mapView, GeoPoint eventPos) {

        int j = 0;

        List<Polyline> polylineList = this.osmMaps.getPolylineList();

        for (int i = 0; i < polylineList.size(); i = i + 2) {

            Polyline polylineBorder = polylineList.get(i);
            Polyline polylineBody = polylineList.get(i + 1);

            if (polylineSelected.getPoints().size() == polylineBody.getPoints().size() || polylineSelected.getPoints().size() == polylineBorder.getPoints().size()) {
                this.selectedPathIndex = i;

                Road road = this.roads.get(j);

                polylineBody.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_selected_body));
                polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_selected_border));

                this.osmMaps.getOverlays().remove(polylineBorder);
                this.osmMaps.getOverlays().add(this.osmMaps.getOverlays().size(), polylineBorder);

                this.osmMaps.getOverlays().remove(polylineBody);
                this.osmMaps.getOverlays().add(this.osmMaps.getOverlays().size(), polylineBody);

                this.osmMaps.getOverlays().remove(this.startTripEdge.getMarker());
                this.osmMaps.getOverlays().add(this.osmMaps.getOverlays().size(), this.startTripEdge.getMarker());

                this.osmMaps.getOverlays().remove(this.destinationTripEdge.getMarker());
                this.osmMaps.getOverlays().add(this.osmMaps.getOverlays().size(), this.destinationTripEdge.getMarker());

                infoPolylineMarker.setPosition(polylineBody.getPoints().get(polylineBody.getPoints().size() / 2));
                infoPolylineMarker.setTitle(Road.getLengthDurationText(getContext(), road.mLength, road.mDuration));
                infoPolylineMarker.showInfoWindow();

            } else {
                polylineBody.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_unselected_body));
                polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_unselected_border));
            }
            j++;
        }

        return true;
    }

    private void launchPlaceAutoCompleteIntent(int requestCode) {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(GoogleLatLngBoundsTask.getPresentCountryLatLngBounds())
                    .build(getActivity());
            startActivityForResult(intent, requestCode);

        } catch (GooglePlayServicesRepairableException e) {
            WidgetHelper.showToast(getActivity(), R.string.google_play_service_error);
            Log.e(TAG, e.getMessage());

            if (requestCode == REQUEST_CODE_ADDRESS_START)
                startAddressRelativeLayout.setOnClickListener(this);
            else
                destinationAddressRelativeLayout.setOnClickListener(this);

        } catch (GooglePlayServicesNotAvailableException e) {
            WidgetHelper.showToast(getActivity(), R.string.google_play_service_not_available);
            Log.e(TAG, e.getMessage());

            if (requestCode == REQUEST_CODE_ADDRESS_START)
                startAddressRelativeLayout.setOnClickListener(this);
            else
                destinationAddressRelativeLayout.setOnClickListener(this);
        }
    }

    private void centerToMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (this.positionManager == null) {
                this.positionManager = new PositionManager((GeneralActivity) getActivity(), ID_REQ_POSITION_MY, this);
                this.positionManager.setRequestType(ID_REQ_POSITION_MY);
                this.positionManager.getLastPosition();
            } else {
                this.positionManager.setRequestType(ID_REQ_POSITION_MY);
                this.positionManager.getLastPosition();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ID_REQUEST_PERMISSION);
        }
    }

    private void promptActiveLocation() {

        GoogleApiClient googleApiClient = this.positionManager.getGoogleApiClient();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }

    private void executeDirectionRequest() {
        ((RideCreationMapActivity) this.getActivity()).showProgressDialog();

        OSMDirectionTask osmDirectionTask = new OSMDirectionTask(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint(), this, this.osmMaps);

        if (((RideCreationMapActivity) this.getActivity()).getWayPoints() != null) {

            List<GeoPoint> geoPointList = new ArrayList<>();

            for (WayPoint wayPoint : ((RideCreationMapActivity) this.getActivity()).getWayPoints()) {
                geoPointList.add(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));
            }

            osmDirectionTask.setAdditionalWayPoints(geoPointList);
        }

        osmDirectionTask.execute();
    }

    private void goToRideCreationFormActivity() {
        if (System.currentTimeMillis() - timestampClicked > 500) {

            if (this.selectedPathIndex > -1) {
                String encodedPolyline = PolylineEncoding.encodeOSM(this.osmMaps.getPolylineList().get(this.selectedPathIndex).getPoints());
                HashMap<String, Serializable> parameters = new HashMap<>();

                Point departure = new Point(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), this.startTripEdge.getAddress());
                Point arriving = new Point(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), this.destinationTripEdge.getAddress());

                parameters.put(RideCreationFormActivity.KEY_START_POINT, departure);
                parameters.put(RideCreationFormActivity.KEY_END_POINT, arriving);
                parameters.put(RideCreationFormActivity.KEY_POLYLINE, encodedPolyline);
                ActivityUtils.openActivityWithObjectParams(getActivity(), RideCreationFormActivity.class, parameters);

            } else {
                WidgetHelper.showToast(getContext(), R.string.invalid_search_ride_input);
            }
        }
        timestampClicked = System.currentTimeMillis();
    }

    private void removeAllPaths() {
        this.osmMaps.removeAllPolyline();
        this.osmMaps.clear();

        if (this.infoPolylineMarker != null)
            this.infoPolylineMarker.closeInfoWindow();

        if (this.startTripEdge != null) {
            this.osmMaps.removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(this.startTripEdge.getGeoPoint(), this.startTripEdge.getAddress());
            Marker startMarker = this.osmMaps.addMarker(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_starting_point, null);
            this.startTripEdge.setMarker(startMarker);
        }

        if (this.destinationTripEdge != null) {
            this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());

            this.destinationTripEdge = new TripEdge(this.destinationTripEdge.getGeoPoint(), this.destinationTripEdge.getAddress());
            Marker destinationMarker = this.osmMaps.addMarker(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_destination_point, null);
            this.destinationTripEdge.setMarker(destinationMarker);
        }

        this.selectedPathIndex = -1;
    }

    private void validatePoints() {
        if (TripEdge.validateTripEdges(startTripEdge, destinationTripEdge))
            this.goToRideCreationFormActivity();
        else
            WidgetHelper.showToast(getActivity(), R.string.no_rout_found);
    }

    public void onPositionRetrieved(Address addressFound, int requestID, double userLatitude, double userLongitude) {

        if (addressFound != null) {

            if (requestID == ID_REQ_POSITION_MY) {

                if (this.myPositionMarker != null)
                    this.osmMaps.removeMarker(this.myPositionMarker);

                this.myPositionMarker = this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.default_map_pin);

            } else if (requestID == ID_REQ_POSITION_MARK_A) {

                this.myPositionMarker = this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.default_map_pin);
                this.textViewStartPoint.setText(OsmGeocodingUtils.convertAddressToString(addressFound));

                if (this.startTripEdge != null)
                    this.osmMaps.removeMarker(this.startTripEdge.getMarker());

                this.startTripEdge = new TripEdge(new GeoPoint(userLatitude, userLongitude), OsmGeocodingUtils.convertAddressToString(addressFound));
                this.startTripEdge.setMarker(this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.ic_starting_point));
            }

            SocialCarApplication.getInstance().storeLastKnownLocation(userLatitude, userLongitude, OsmGeocodingUtils.convertAddressToString(addressFound));

            GoogleLatLngBoundsTask googleLatLngBoundsTask = new GoogleLatLngBoundsTask(userLatitude, userLongitude, this.getContext());
            googleLatLngBoundsTask.execute();
        }
    }

    public void showLocationFromAddress(Address address, Integer addressType) {

        switch (addressType) {

            case 0:
                this.textViewStartPoint.setText(OsmGeocodingUtils.convertAddressToString(address));

                if (this.startTripEdge != null)
                    this.osmMaps.removeMarker(this.startTripEdge.getMarker());

                this.startTripEdge = new TripEdge(new GeoPoint(address.getLatitude(), address.getLongitude()), OsmGeocodingUtils.convertAddressToString(address));

                Marker startMarker = this.osmMaps.addMarker(address.getLatitude(), address.getLongitude(), R.mipmap.ic_starting_point, null);
                this.startTripEdge.setMarker(startMarker);

                if (this.destinationTripEdge != null && this.actualPhase == ID_PHASE_REVIEW)
                    this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());
                break;

            case 1:
                this.textViewDestinationPoint.setText(OsmGeocodingUtils.convertAddressToString(address));

                if (this.destinationTripEdge != null)
                    this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());

                this.destinationTripEdge = new TripEdge(new GeoPoint(address.getLatitude(), address.getLongitude()), OsmGeocodingUtils.convertAddressToString(address));

                Marker destinationMarker = this.osmMaps.addMarker(address.getLatitude(), address.getLongitude(), R.mipmap.ic_destination_point, null);
                this.destinationTripEdge.setMarker(destinationMarker);

                if (this.destinationTripEdge != null && this.actualPhase == ID_PHASE_REVIEW)
                    this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());
                break;
        }
    }

    public void showNotFoundAddress() {
        WidgetHelper.showToast(getContext(), R.string.address_not_found);
    }

    public void showAddressGenericError() {
        WidgetHelper.showToast(getContext(), R.string.error_during_get_address);
    }

    @Override
    public void networkError() {
        WidgetHelper.showToast(getActivity(), R.string.no_connection_error);
    }

    @Override
    public void genericError(int errorType) {
        WidgetHelper.showToast(getActivity(), R.string.generic_error);
    }

    @Override
    public void locationServiceDisable() {
        promptActiveLocation();
    }

    @Override
    public void permissionDisabled(String permissionType) {
        WidgetHelper.showToast(getActivity(), R.string.error_enable_permission_app);
    }

    public void showGenericError() {
        WidgetHelper.showToast(getActivity(), R.string.generic_error);
        (getActivity()).finish();
    }

    public void showNetworkError() {
        WidgetHelper.showToast(getActivity(), R.string.no_connection_error);

        this.backToStartLayout();
    }

    public void showNoRoutesFoundError() {
        WidgetHelper.showToast(getActivity(), R.string.no_routes_found_error);

        this.backToStartLayout();
    }

    private void backToStartLayout() {
        this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());
        this.destinationTripEdge = null;

        this.textViewDestinationPoint.setText(getString(R.string.hint_insert_destination));

        this.osmMaps.moveCamera(this.startTripEdge.getGeoPoint());

        this.setStartLayout();
    }

    public void showRoads(List<Road> roads, boolean fromRideCreation) {

        if (fromRideCreation)
            this.addWayPointsMenu.findItem(R.id.add_waypoints).setVisible(true);

        ((RideCreationMapActivity) this.getActivity()).dismissDialog();

        this.selectedPathIndex = 0;
        this.roads = roads;

        Polyline polylineBodySelected = null;
        Polyline polylineBodyUnSelected;

        for (int i = roads.size() - 1; i >= 0; i--) {

            Road road = roads.get(i);

            if (i == 0) {
                Polyline polylineBorder = this.osmMaps.addBorderPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_selected_border));

                polylineBodySelected = this.osmMaps.addBodyPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBodySelected.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_selected_body));

                GeoPoint polyLineCenter = new GeoPoint(polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLatitude(), polylineBodySelected.getPoints().get(polylineBodySelected.getPoints().size() / 2).getLongitude());
                this.infoPolylineMarker = this.osmMaps.addMarker(polyLineCenter.getLatitude(), polyLineCenter.getLongitude(), false, Road.getLengthDurationText(getContext(), road.mLength, road.mDuration), R.layout.custom_info_adapter);
                this.infoPolylineMarker.setInfoWindowAnchor((float) 0.5, 1);
                this.infoPolylineMarker.showInfoWindow();

                this.osmMaps.setOnPolylineClickListener(polylineBodySelected, this);

            } else {
                Polyline polylineBorder = this.osmMaps.addBorderPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBorder.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_unselected_border));

                polylineBodyUnSelected = this.osmMaps.addBodyPolyline(RoadManager.buildRoadOverlay(roads.get(i)).getPoints());
                polylineBodyUnSelected.setColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.polyline_unselected_body));

                this.osmMaps.setOnPolylineClickListener(polylineBodyUnSelected, this);
            }
        }

        this.osmMaps.addPolylineEdge(polylineBodySelected);

        if (this.startTripEdge != null) {
            this.osmMaps.removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(this.startTripEdge.getGeoPoint(), this.startTripEdge.getAddress());
            Marker startMarker = this.osmMaps.addMarker(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_starting_point);
            this.startTripEdge.setMarker(startMarker);
        }

        if (this.destinationTripEdge != null) {
            this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());

            this.destinationTripEdge = new TripEdge(this.destinationTripEdge.getGeoPoint(), this.destinationTripEdge.getAddress());
            Marker destinationMarker = this.osmMaps.addMarker(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_destination_point);
            this.destinationTripEdge.setMarker(destinationMarker);
        }

        ((RideCreationMapActivity) getActivity()).addWayPointsOnMap();

        List<GeoPoint> polylinePoints = this.osmMaps.getPolylineList().get(this.selectedPathIndex).getPoints();

        if (this.startTripEdge != null && this.destinationTripEdge != null)
            this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint(), ((RideCreationMapActivity) getActivity()).getWayPoints(), polylinePoints);
    }
}
