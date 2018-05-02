package eu.h2020.sc.ui.home.trip.search;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Stop;
import eu.h2020.sc.domain.TimeRange;
import eu.h2020.sc.domain.TransferMode;
import eu.h2020.sc.domain.Transit;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.TripEdge;
import eu.h2020.sc.location.PositionManager;
import eu.h2020.sc.location.PositionManagerCallback;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.map.MapCallback;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.protocol.TripSolutionsRequest;
import eu.h2020.sc.tasks.AddressGeocodingTask;
import eu.h2020.sc.tasks.PositionGeocodingTask;
import eu.h2020.sc.ui.home.stopsaround.FindStopsAroundTask;
import eu.h2020.sc.ui.trip.solution.TripSolutionsActivity;
import eu.h2020.sc.ui.waitingtime.WaitingTimeActivity;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;
import eu.h2020.sc.utils.WidgetHelper;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class TripSearchOSMFragment extends Fragment implements MapCallback, MapEventsReceiver, Marker.OnMarkerClickListener, PositionManagerCallback, View.OnClickListener, ResultCallback<LocationSettingsResult> {

    private static final String TAG = TripSearchOSMFragment.class.getSimpleName();

    private final int REQUEST_CODE_ADDRESS_START = 1;
    private final int REQUEST_CODE_ADDRESS_DESTINATION = 2;
    private final int REQUEST_CODE_ASK_LOCATION = 3;

    private final int ID_PHASE_SET_START = 1;
    private final int ID_PHASE_SET_DESTINATION = 2;
    private final int ID_PHASE_REVIEW = 3;
    private final int ID_PHASE_MODIFY_START = 4;
    private final int ID_PHASE_MODIFY_DESTINATION = 5;
    private final int ID_REQUEST_PERMISSION = 1;

    public static final int ID_REQ_POSITION_MARK_A = 1;
    public static final int ID_REQ_POSITION_MY = 2;

    private TripEdge startTripEdge;
    private TripEdge destinationTripEdge;
    private Marker myPositionMarker;

    private long timestampClicked = 0;
    private int actualPhase;
    private PositionManager positionManager;
    private View fragmentView;
    private Button buttonGoNextStep;

    //start point elements
    private FrameLayout layoutFrameStartPoint;
    private RelativeLayout layoutRelativeStartAddress;
    private TextViewCustom textViewStartPoint;
    private ImageView imageViewSearchStartAddress;
    private MaterialRippleLayout layoutRippleStartPointCancel;
    private ImageView imageViewCancelStartAddress;
    private View viewSearchDividerStart;
    private MaterialRippleLayout layoutRippleStartPoint;
    private View viewShadowLine;

    //destination point elements
    private FrameLayout layoutFrameDestinationPoint;
    private RelativeLayout layoutRelativeDestinationAddress;
    private TextViewCustom textViewDestinationPoint;
    private ImageView imageViewSearchDestination;
    private MaterialRippleLayout layoutRippleDestinationCancel;
    private View viewSearchDividerDestination;
    private MaterialRippleLayout layoutRippleDestination;

    private MaterialRippleLayout layoutRippleBtnGoNextStep;

    //stop transits layout elements
    private MaterialRippleLayout layoutRippleStopTransits;
    private LinearLayout layoutLinearTransitLines;
    private TextView textViewStopName;

    private LayoutInflater layoutInflater;

    private HashMap<String, Object> stopsAroundMarkers;
    private List<Stop> listStopsAround;
    private Stop stopSelected;

    private OsmMaps osmMaps;
    private Place startPlace;
    private Place destinationPlace;
    private boolean isMarkerClicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

        this.fragmentView = inflater.inflate(R.layout.fragment_trip_search, container, false);
        this.osmMaps = new OsmMaps((MapView) fragmentView.findViewById(R.id.mapView), getActivity(), this);

        this.stopsAroundMarkers = new HashMap<>();
        this.listStopsAround = new ArrayList<>();

        initElementsReferences();
        prepareViewSetStartPoint();

        return fragmentView;
    }

    private void initElementsReferences() {
        ImageView imageViewMyLocation = (ImageView) this.fragmentView.findViewById(R.id.image_view_my_location);
        imageViewMyLocation.setOnClickListener(this);

        LinearLayout layoutTripSearch = (LinearLayout) this.fragmentView.findViewById(R.id.layout_trip_search);
        layoutTripSearch.setOnClickListener(this);

        layoutFrameStartPoint = (FrameLayout) this.fragmentView.findViewById(R.id.layout_start_address);
        imageViewSearchStartAddress = (ImageView) this.fragmentView.findViewById(R.id.image_view_search_start);
        layoutRippleStartPointCancel = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_cancel_start);
        textViewStartPoint = (TextViewCustom) this.fragmentView.findViewById(R.id.text_view_start_point);
        imageViewCancelStartAddress = (ImageView) this.fragmentView.findViewById(R.id.image_view_cancel_start);
        imageViewCancelStartAddress.setOnClickListener(this);
        layoutRippleStartPoint = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_text_start);
        layoutRippleStartPoint.setEnabled(true);
        viewShadowLine = this.fragmentView.findViewById(R.id.view_shadow_line);


        //init destination elements:
        imageViewSearchDestination = (ImageView) this.fragmentView.findViewById(R.id.image_view_search_destination);
        layoutRippleDestinationCancel = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_cancel_destination);
        textViewDestinationPoint = (TextViewCustom) this.fragmentView.findViewById(R.id.text_view_destination_point);
        layoutFrameDestinationPoint = (FrameLayout) this.fragmentView.findViewById(R.id.layout_destination_point_text);
        ImageView cancelDestinationImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_cancel_destination);
        cancelDestinationImageView.setOnClickListener(this);
        layoutRippleDestination = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_text_destination);
        layoutRippleDestination.setEnabled(true);


        layoutRippleBtnGoNextStep = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_btn_go_next_step);
        buttonGoNextStep = (Button) this.fragmentView.findViewById(R.id.button_go_next_step);
        buttonGoNextStep.setOnClickListener(this);


        layoutRelativeStartAddress = (RelativeLayout) fragmentView.findViewById(R.id.relative_layout_start_address);
        layoutRelativeStartAddress.setOnClickListener(this);

        layoutRelativeDestinationAddress = (RelativeLayout) fragmentView.findViewById(R.id.relative_layout_destination_address);
        layoutRelativeDestinationAddress.setOnClickListener(this);

        viewSearchDividerDestination = fragmentView.findViewById(R.id.view_search_divider_destination);
        viewSearchDividerStart = fragmentView.findViewById(R.id.view_search_divider_start);

        // stop transits ripple elements
        layoutRippleStopTransits = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.layout_ripple_stop_transits);
        layoutRippleStopTransits.setOnClickListener(clickListenerOfLayoutStopTransits);
        layoutLinearTransitLines = (LinearLayout) this.fragmentView.findViewById(R.id.layout_transit_lines);
        textViewStopName = (TextView) this.fragmentView.findViewById(R.id.tv_stop_name);

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void initMapListener() {
        this.osmMaps.setOnMapClickListener(this);
    }

    private void prepareViewSetStartPoint() {
        this.actualPhase = ID_PHASE_SET_START;

        imageViewCancelStartAddress.setOnClickListener(null);

        layoutRelativeStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        layoutRelativeStartAddress.setOnClickListener(this);
        layoutRippleStartPoint.setEnabled(true);

        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutRelativeStartAddress.getLayoutParams();
        params.rightMargin = 0;
        layoutRelativeStartAddress.setLayoutParams(params);

        imageViewSearchStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));
        layoutRippleStartPointCancel.setVisibility(View.GONE);
        layoutFrameDestinationPoint.setVisibility(View.GONE);

        buttonGoNextStep.setText(R.string.set_starting_point);
    }

    private void prepareViewSetDestinationPoint() {
        imageViewCancelStartAddress.setOnClickListener(this);

        layoutRelativeStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        layoutRelativeStartAddress.setOnClickListener(null);
        layoutRippleStartPoint.setEnabled(false);

        viewSearchDividerDestination.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));
        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutRelativeStartAddress.getLayoutParams();
        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        layoutRelativeStartAddress.setLayoutParams(params);

        layoutRippleStartPointCancel.setVisibility(View.VISIBLE);
        imageViewSearchStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));
        layoutFrameDestinationPoint.setVisibility(View.VISIBLE);

        layoutRelativeDestinationAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        layoutRelativeDestinationAddress.setOnClickListener(this);
        layoutRippleDestination.setEnabled(true);

        params.rightMargin = 0;
        layoutRelativeDestinationAddress.setLayoutParams(params);

        imageViewSearchDestination.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));
        layoutRippleDestinationCancel.setVisibility(View.GONE);

        buttonGoNextStep.setText(R.string.set_destination_point);

    }

    private void setReviewLayout() {
        this.actualPhase = ID_PHASE_REVIEW;

        imageViewCancelStartAddress.setOnClickListener(this);

        layoutRelativeStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        layoutRelativeStartAddress.setOnClickListener(null);
        layoutRippleStartPoint.setEnabled(false);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutRelativeStartAddress.getLayoutParams();
        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        layoutRelativeStartAddress.setLayoutParams(params);

        viewSearchDividerDestination.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));
        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.stroke_defined_search_field));

        layoutRippleStartPointCancel.setVisibility(View.VISIBLE);
        imageViewSearchStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));

        layoutFrameDestinationPoint.setVisibility(View.VISIBLE);
        layoutRelativeDestinationAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        layoutRelativeDestinationAddress.setOnClickListener(null);
        layoutRippleDestination.setEnabled(false);

        params.rightMargin = Utils.fromDptoPx(42, getActivity());
        layoutRelativeDestinationAddress.setLayoutParams(params);

        layoutRippleDestinationCancel.setVisibility(View.VISIBLE);
        imageViewSearchDestination.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));

        buttonGoNextStep.setText(R.string.search_solutions);
    }

    private void prepareViewModifyStartPoint() {
        this.actualPhase = ID_PHASE_MODIFY_START;

        imageViewCancelStartAddress.setOnClickListener(null);

        layoutRelativeStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        layoutRelativeStartAddress.setOnClickListener(this);
        layoutRippleStartPoint.setEnabled(true);

        viewSearchDividerStart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_grey));

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutRelativeStartAddress.getLayoutParams();
        params.rightMargin = 0;
        layoutRelativeStartAddress.setLayoutParams(params);

        imageViewSearchStartAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));
        layoutRippleStartPointCancel.setVisibility(View.GONE);

        layoutFrameDestinationPoint.setVisibility(View.VISIBLE);
        layoutRelativeDestinationAddress.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_dark_bg));
        layoutRelativeDestinationAddress.setOnClickListener(null);
        layoutRippleDestination.setEnabled(false);

        params.rightMargin = Utils.fromDptoPx(0, getActivity());
        layoutRelativeDestinationAddress.setLayoutParams(params);

        layoutRippleDestinationCancel.setVisibility(View.VISIBLE);
        imageViewSearchDestination.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_b));

        buttonGoNextStep.setText(R.string.set_starting_point);
    }

    @Override
    public void onPosition(Location location, int requestID) {
        if (location != null) {
            PositionGeocodingTask positionGeocodingTask = new PositionGeocodingTask(this, requestID, location.getLatitude(), location.getLongitude(), getContext());
            positionGeocodingTask.execute(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }
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
        this.promptActiveLocation();
    }

    @Override
    public void permissionDisabled(String permissionType) {
        WidgetHelper.showToast(getActivity(), R.string.error_enable_permission_app);
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
                layoutRelativeStartAddress.setOnClickListener(this);
            else
                layoutRelativeDestinationAddress.setOnClickListener(this);

        } catch (GooglePlayServicesNotAvailableException e) {
            WidgetHelper.showToast(getActivity(), R.string.google_play_service_not_available);
            Log.e(TAG, e.getMessage());

            if (requestCode == REQUEST_CODE_ADDRESS_START)
                layoutRelativeStartAddress.setOnClickListener(this);
            else
                layoutRelativeDestinationAddress.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_ADDRESS_START)
            layoutRelativeStartAddress.setOnClickListener(this);

        else if (requestCode == REQUEST_CODE_ADDRESS_DESTINATION)
            layoutRelativeDestinationAddress.setOnClickListener(this);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_ADDRESS_START:
                    this.startPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                    AddressGeocodingTask retrievePositionFromStartAddress = new AddressGeocodingTask(this, 0, getContext());
                    retrievePositionFromStartAddress.execute(this.startPlace);
                    break;

                case REQUEST_CODE_ADDRESS_DESTINATION:
                    this.destinationPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                    AddressGeocodingTask retrievePositionFromDestinationAddress = new AddressGeocodingTask(this, 1, getContext());
                    retrievePositionFromDestinationAddress.execute(this.destinationPlace);
                    break;

                case REQUEST_CODE_ASK_LOCATION:
                    this.centerToMyLocation();
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
                if (this.startTripEdge == null) {
                    WidgetHelper.showToast(getActivity(), R.string.start_point_not_setted);
                } else {
                    if (this.destinationTripEdge == null) {
                        this.actualPhase = ID_PHASE_SET_DESTINATION;
                        prepareViewSetDestinationPoint();
                    } else {
                        if (this.actualPhase != ID_PHASE_REVIEW) {
                            setReviewLayout();
                            this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());
                        } else {
                            validatePoints();
                        }
                    }
                }
                break;
            case R.id.relative_layout_start_address:
                layoutRelativeStartAddress.setOnClickListener(null);
                launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_START);
                break;
            case R.id.relative_layout_destination_address:
                layoutRelativeDestinationAddress.setOnClickListener(null);
                launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_DESTINATION);
                break;

            case R.id.image_view_cancel_start:
                if (this.actualPhase == ID_PHASE_MODIFY_DESTINATION || this.actualPhase == ID_PHASE_REVIEW) {
                    prepareViewModifyStartPoint();
                } else {
                    prepareViewSetStartPoint();
                }
                this.osmMaps.moveCamera(startTripEdge.getGeoPoint());
                break;

            case R.id.image_view_cancel_destination:
                this.actualPhase = ID_PHASE_MODIFY_DESTINATION;
                prepareViewSetDestinationPoint();
                this.osmMaps.moveCamera(destinationTripEdge.getGeoPoint());
                break;
            case R.id.image_view_my_location:
                centerToMyLocation();
                break;
        }
    }

    private void validatePoints() {
        if (TripEdge.validateTripEdges(startTripEdge, destinationTripEdge))
            goToTripSolutionsActivity();
        else
            WidgetHelper.showToast(getActivity(), R.string.no_rout_found);
    }


    private void centerToMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (this.positionManager == null) {
                this.positionManager = new PositionManager((GeneralActivity) getActivity(), ID_REQ_POSITION_MY, this);
            } else {
                this.positionManager.setRequestType(ID_REQ_POSITION_MY);
                this.positionManager.getLastPosition();
            }

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ID_REQUEST_PERMISSION);
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

    private void goToTripSolutionsActivity() {
        if (System.currentTimeMillis() - timestampClicked > Globals.MULTIPLE_CLICK_TIME_SPAN_MILLIS) {
            Intent intent = new Intent(getActivity(), TripSolutionsActivity.class);
            intent.putExtra(TripSolutionsRequest.TRIP_REQUEST_KEY, prepareTripRequest());
            this.startActivity(intent);
        }
        timestampClicked = System.currentTimeMillis();
    }

    private TripSolutionsRequest prepareTripRequest() {

        List<TravelMode> modes = new ArrayList<>();
        modes.add(TravelMode.BUS);
        modes.add(TravelMode.METRO);
        modes.add(TravelMode.RAIL);

        TransferMode transferMode = TransferMode.CHEAPEST_ROUTE;

        TravelMode[] modesArray = new TravelMode[modes.size()];

        Point startPoint = new Point(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), this.startTripEdge.getAddress());
        Point endPoint = new Point(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), this.destinationTripEdge.getAddress());

        TripSolutionsRequest tripSolutionsRequest = new TripSolutionsRequest(startPoint, endPoint,
                modes.toArray(modesArray), transferMode);

        tripSolutionsRequest.setTimeRange(new TimeRange(new Date()));

        return tripSolutionsRequest;
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
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) && PositionManager.isGpsEnable(getActivity())) {
            this.positionManager = new PositionManager((GeneralActivity) getActivity(), ID_REQ_POSITION_MARK_A, this);
        }
    }

    public void addMarkersStopsAround() {
        this.initMapListener();

        for (Stop stop : this.listStopsAround) {
            Marker marker = this.osmMaps.addMarker(stop.getLat(), stop.getLon(), R.mipmap.ic_stop, false, stop.getStopCode(), this);
            this.stopsAroundMarkers.put(marker.getSnippet(), stop);
        }
    }

    public void updateMapMarkers() {
        this.osmMaps.clear();
        addMarkerStartPoint();
        addMarkerDestinationPoint();
        addMarkersStopsAround();
        addMarkerCurrentPosition();
    }

    private void addMarkerCurrentPosition() {
        Point point = SocialCarApplication.getInstance().retrieveLastKnowLocation();
        if (point != null) {
            if (this.myPositionMarker != null) {
                this.osmMaps.removeMarker(this.myPositionMarker);
                this.myPositionMarker = this.osmMaps.addMarker(point.getLat(), point.getLon(), R.mipmap.default_map_pin, null);
            }
        }
    }

    private void addMarkerStartPoint() {
        Point point;
        if (this.startTripEdge != null) {
            point = new Point(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), this.startTripEdge.getAddress());
        } else {
            point = SocialCarApplication.getInstance().retrieveLastKnowLocation();
        }

        if (point != null) {
            this.textViewStartPoint.setText(point.getAddress());

            if (this.startTripEdge != null)
                this.osmMaps.removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(new GeoPoint(point.getLat(), point.getLon()), point.getAddress());
            this.startTripEdge.setMarker(this.osmMaps.addMarker(point.getLat(), point.getLon(), R.mipmap.ic_starting_point, null));
        }
    }

    private void addMarkerDestinationPoint() {
        if (this.destinationTripEdge != null) {
            this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());
            this.osmMaps.addMarker(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_destination_point, null);
        }
    }

    /**
     * ***************************************************|| onMarker Click Listener ||****************************************************************************************************
     */

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        this.stopSelected = (Stop) this.stopsAroundMarkers.get(marker.getSnippet());
        isMarkerClicked = true;

        if (this.stopSelected != null) {
            prepareViewStopTransits();
            populateViewStopTransits();
        }
        return false;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {

        if (!isMarkerClicked) {
            this.hideTransitLayout();
        }

        isMarkerClicked = false;

        return true;
    }

    private void hideTransitLayout() {

        layoutRippleStopTransits.setVisibility(View.GONE);

        viewShadowLine.setVisibility(View.VISIBLE);
        layoutFrameStartPoint.setVisibility(View.VISIBLE);
        layoutFrameDestinationPoint.setVisibility(View.VISIBLE);
        layoutRippleBtnGoNextStep.setVisibility(View.VISIBLE);

        switch (actualPhase) {
            case ID_PHASE_SET_START:
                prepareViewSetStartPoint();
                break;
            case ID_PHASE_SET_DESTINATION:
                prepareViewSetDestinationPoint();
                break;
            case ID_PHASE_REVIEW:
                setReviewLayout();
                break;
            case ID_PHASE_MODIFY_START:
                prepareViewModifyStartPoint();
                break;
            case ID_PHASE_MODIFY_DESTINATION:
                prepareViewSetDestinationPoint();
                break;
        }
    }

    private void populateViewStopTransits() {
        if (this.stopSelected != null) {
            this.textViewStopName.setText(this.stopSelected.getName());
            this.layoutLinearTransitLines.removeAllViews();

            for (Transit transit : this.stopSelected.getTransits()) {

                View view = this.layoutInflater.inflate(R.layout.layout_transit_line, null);
                TextView textViewTransitLine = (TextView) view.findViewById(R.id.text_view_transit_line);
                textViewTransitLine.setText(transit.getPublicTransport().getShortName());
                this.layoutLinearTransitLines.addView(view);
            }
        }
    }

    private View.OnClickListener clickListenerOfLayoutStopTransits = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (System.currentTimeMillis() - timestampClicked > Globals.MULTIPLE_CLICK_TIME_SPAN_MILLIS) {

                Intent intent = new Intent(getActivity(), WaitingTimeActivity.class);
                intent.putExtra(WaitingTimeActivity.WAITING_TIME_KEY, stopSelected);
                startActivity(intent);

                hideTransitLayout();
            }
            timestampClicked = System.currentTimeMillis();
        }
    };

    private void prepareViewStopTransits() {

        layoutRippleStopTransits.setVisibility(View.VISIBLE);

        viewShadowLine.setVisibility(View.GONE);
        layoutFrameStartPoint.setVisibility(View.GONE);
        layoutFrameDestinationPoint.setVisibility(View.GONE);
        layoutRippleBtnGoNextStep.setVisibility(View.GONE);
    }

    public void setListStopsAround(List<Stop> listStopsAround) {
        this.listStopsAround = listStopsAround;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    public void showLocationFromAddress(Address address, Integer addressType) {

        if (addressType == 0) {
            this.textViewStartPoint.setText(OsmGeocodingUtils.convertAddressToString(address));

            if (this.startTripEdge != null)
                this.osmMaps.removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(new GeoPoint(address.getLatitude(), address.getLongitude()), OsmGeocodingUtils.convertAddressToString(address));

            Marker startMarker = this.osmMaps.addMarker(address.getLatitude(), address.getLongitude(), R.mipmap.ic_starting_point, null);
            this.startTripEdge.setMarker(startMarker);

            if (this.destinationTripEdge != null && this.actualPhase == ID_PHASE_REVIEW)
                this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());

        } else {
            this.textViewDestinationPoint.setText(OsmGeocodingUtils.convertAddressToString(address));

            if (this.destinationTripEdge != null)
                this.osmMaps.removeMarker(this.destinationTripEdge.getMarker());

            this.destinationTripEdge = new TripEdge(new GeoPoint(address.getLatitude(), address.getLongitude()), OsmGeocodingUtils.convertAddressToString(address));
            Marker destinationMarker = this.osmMaps.addMarker(address.getLatitude(), address.getLongitude(), R.mipmap.ic_destination_point, null);
            this.destinationTripEdge.setMarker(destinationMarker);

            if (this.destinationTripEdge != null && this.actualPhase == ID_PHASE_REVIEW)
                this.osmMaps.zoomOutPlaces(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint());
        }
    }

    public void onPositionRetrieved(Address addressFound, int requestID, double userLatitude, double userLongitude) {

        if (addressFound != null) {

            if (requestID == ID_REQ_POSITION_MY) {

                if (this.myPositionMarker != null)
                    this.osmMaps.removeMarker(this.myPositionMarker);

                this.myPositionMarker = this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.default_map_pin, null);

                FindStopsAroundTask findStopsAroundTask = new FindStopsAroundTask(this);
                findStopsAroundTask.execute(new Point(userLatitude, userLongitude));

            } else if (requestID == ID_REQ_POSITION_MARK_A) {
                this.osmMaps.clear();
                this.myPositionMarker = this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.default_map_pin, null);
                this.textViewStartPoint.setText(OsmGeocodingUtils.convertAddressToString(addressFound));

                if (this.startTripEdge != null)
                    this.osmMaps.removeMarker(this.startTripEdge.getMarker());

                this.startTripEdge = new TripEdge(new GeoPoint(userLatitude, userLongitude), OsmGeocodingUtils.convertAddressToString(addressFound));
                this.startTripEdge.setMarker(this.osmMaps.addMarker(userLatitude, userLongitude, R.mipmap.ic_starting_point, null));
            }

            SocialCarApplication.getInstance().storeLastKnownLocation(userLatitude, userLongitude, OsmGeocodingUtils.convertAddressToString(addressFound));

            GoogleLatLngBoundsTask googleLatLngBoundsTask = new GoogleLatLngBoundsTask(userLatitude, userLongitude, this.getContext());
            googleLatLngBoundsTask.execute();
        }
    }

    public void showNotFoundAddress() {
        WidgetHelper.showToast(getContext(), R.string.address_not_found);
    }

    public void showAddressGenericError() {
        WidgetHelper.showToast(getContext(), R.string.error_during_get_address);
    }
}
