package eu.h2020.sc.ui.ride.creation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.TripEdge;
import eu.h2020.sc.domain.WayPoint;
import eu.h2020.sc.map.MapCallback;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.tasks.AddressGeocodingTask;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.home.trip.search.GoogleLatLngBoundsTask;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationMapActivity;
import eu.h2020.sc.ui.ride.creation.adapter.WayPointAdapter;
import eu.h2020.sc.ui.ride.creation.task.OSMDirectionTask;
import eu.h2020.sc.utils.MaterialRippleLayout;
import eu.h2020.sc.utils.WidgetHelper;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class RideAddWayPointsFragment extends Fragment implements View.OnClickListener, MapCallback, RecyclerViewItemClickListener {

    private String TAG = RideAddWayPointsFragment.class.getName();

    private final int REQUEST_CODE_ADDRESS = 1;

    private View fragmentView;
    private OsmMaps osmMaps;

    private TripEdge startTripEdge;
    private TripEdge destinationTripEdge;

    private List<WayPoint> points;
    private RecyclerView recycleViewWayPoints;
    private WayPointAdapter wayPointsAdapter;

    private List<Road> roads;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.fragmentView = inflater.inflate(R.layout.fragment_add_waypoints, container, false);

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        this.osmMaps = new OsmMaps((MapView) this.fragmentView.findViewById(R.id.mapView), getActivity(), this);

        this.initUI();
        return fragmentView;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    setFragmentArgument();
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    private void initUI() {
        this.getActivity().setTitle(R.string.add_waypoints);

        ((GeneralActivity) getActivity()).getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragmentArgument();
                getFragmentManager().popBackStack();
            }
        });

        RelativeLayout relativeLayoutAddWayPoint = (RelativeLayout) this.fragmentView.findViewById(R.id.relative_layout_add_waypoint);
        relativeLayoutAddWayPoint.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.search_field_bg));
        relativeLayoutAddWayPoint.setOnClickListener(this);

        MaterialRippleLayout textNewWayPointRipple = (MaterialRippleLayout) this.fragmentView.findViewById(R.id.ripple_layout_add_waypoint);
        textNewWayPointRipple.setEnabled(true);

        ImageView searchWayPointImageView = (ImageView) this.fragmentView.findViewById(R.id.image_view_search_waypoint);
        searchWayPointImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_point_a));

        this.recycleViewWayPoints = (RecyclerView) this.fragmentView.findViewById(R.id.recycler_view_waypoints);

        this.startTripEdge = ((RideCreationMapActivity) getActivity()).getStartTripEdge();
        this.destinationTripEdge = ((RideCreationMapActivity) getActivity()).getDestinationTripEdge();

        this.initRecycleViewWayPoints();

        this.showElementsOnMap();
    }

    private void initRecycleViewWayPoints() {

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        this.recycleViewWayPoints.setHasFixedSize(true);
        this.recycleViewWayPoints.setLayoutManager(llm);
        this.recycleViewWayPoints.setItemAnimator(new DefaultItemAnimator());

        this.points = new ArrayList<>();
        this.wayPointsAdapter = new WayPointAdapter();
        this.wayPointsAdapter.setRecyclerViewItemClickListener(this);

        this.recycleViewWayPoints.setAdapter(this.wayPointsAdapter);
    }

    private void showElementsOnMap() {

        ((RideCreationMapActivity) getActivity()).setOsmMaps(this.osmMaps);
        ((RideCreationMapActivity) getActivity()).showRoads();

        this.roads = ((RideCreationMapActivity) getActivity()).getRoads();

        if (((RideCreationMapActivity) getActivity()).getWayPoints() != null) {
            this.wayPointsAdapter.refreshRecyclerView(((RideCreationMapActivity) getActivity()).getWayPoints());
            this.points = ((RideCreationMapActivity) getActivity()).getWayPoints();
            ((RideCreationMapActivity) getActivity()).addWayPointsOnMap();

            this.setRecycleViewHeightByPoints();
        }

        this.zoomOutPlace();
    }

    private void setRecycleViewHeightByPoints() {

        switch (this.points.size()) {

            case 0:
                this.incrementRecycleViewHeight(0);
                break;

            case 1:
                this.incrementRecycleViewHeight(64);
                break;

            case 2:
                this.incrementRecycleViewHeight(128);
                break;

            case 3:
                this.incrementRecycleViewHeight(192);
                break;

            default:
                break;
        }

    }

    private void incrementRecycleViewHeight(int heightDps) {

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (heightDps * scale + 0.5f);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.recycleViewWayPoints.getLayoutParams();
        params.height = pixels;
        this.recycleViewWayPoints.setLayoutParams(params);
    }

    private void zoomOutPlace() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                GeoPoint startTripPoint = ((RideCreationMapActivity) getActivity()).getStartTripEdge().getGeoPoint();
                GeoPoint destinationTripPoint = ((RideCreationMapActivity) getActivity()).getDestinationTripEdge().getGeoPoint();

                List<WayPoint> wayPoints = ((RideCreationMapActivity) getActivity()).getWayPoints();

                ((RideCreationMapActivity) getActivity()).getOsmMaps().zoomOutPlaces(startTripPoint, destinationTripPoint, wayPoints, ((RideCreationMapActivity) getActivity()).getOsmMaps().getPolylineList().get(0).getPoints());

            }
        }, 800);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_layout_add_waypoint:
                this.launchPlaceAutoCompleteIntent();
                break;
        }
    }

    private void launchPlaceAutoCompleteIntent() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(GoogleLatLngBoundsTask.getPresentCountryLatLngBounds())
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);

        } catch (GooglePlayServicesRepairableException e) {
            WidgetHelper.showToast(getActivity(), R.string.google_play_service_error);
            Log.e(TAG, e.getMessage());

        } catch (GooglePlayServicesNotAvailableException e) {
            WidgetHelper.showToast(getActivity(), R.string.google_play_service_not_available);
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_ADDRESS:
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    AddressGeocodingTask retrievePositionFromStartAddress = new AddressGeocodingTask(this, getContext());
                    retrievePositionFromStartAddress.execute(place);
                    break;
            }

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Log.e(TAG, PlaceAutocomplete.getStatus(getActivity(), data).getStatusMessage());
        }
    }

    @Override
    @Deprecated
    public void onMapReady() {
    }

    public void showLocationFromAddress(Address address) {
        WayPoint wayPoint = new WayPoint(address);

        if (this.points.isEmpty()) {
            this.wayPointsAdapter.addItem(wayPoint, 0);
        } else
            this.wayPointsAdapter.addItem(wayPoint, this.points.size());

        this.points.add(wayPoint);
        this.recycleViewWayPoints.scrollToPosition(this.wayPointsAdapter.getPositionByItem(wayPoint));
        this.wayPointsAdapter.refreshItems();

        this.setRecycleViewHeightByPoints();

        this.executeDirectionRequest(this.points);
    }

    private void executeDirectionRequest(List<WayPoint> additionalWayPoints) {
        ((RideCreationMapActivity) this.getActivity()).showProgressDialog();

        OSMDirectionTask osmDirectionTask = new OSMDirectionTask(this.startTripEdge.getGeoPoint(), this.destinationTripEdge.getGeoPoint(), this, this.osmMaps);

        List<GeoPoint> geoPointList = new ArrayList<>();

        for (WayPoint wayPoint : additionalWayPoints) {
            geoPointList.add(new GeoPoint(wayPoint.getLatitude(), wayPoint.getLongitude()));
        }

        osmDirectionTask.setAdditionalWayPoints(geoPointList);
        osmDirectionTask.execute();
    }

    public void showRoads(List<Road> roads) {

        this.roads = roads;

        ((RideCreationMapActivity) getActivity()).setRoads(roads);
        this.removeAllPaths();
        ((RideCreationMapActivity) getActivity()).showRoads();

        for (WayPoint wayPoint : this.points) {
            ((RideCreationMapActivity) getActivity()).getOsmMaps().addMarker(wayPoint.getLatitude(), wayPoint.getLongitude(), R.mipmap.ic_pin_waypoint, false);
        }

        this.zoomOutPlace();
    }

    private void removeAllPaths() {

        ((RideCreationMapActivity) getActivity()).getOsmMaps().removeAllPolyline();
        ((RideCreationMapActivity) getActivity()).getOsmMaps().clear();

        ((RideCreationMapActivity) getActivity()).removeInfoMarker();

        if (this.startTripEdge != null) {
            ((RideCreationMapActivity) getActivity()).getOsmMaps().removeMarker(this.startTripEdge.getMarker());

            this.startTripEdge = new TripEdge(this.startTripEdge.getGeoPoint(), this.startTripEdge.getAddress());
            Marker startMarker = ((RideCreationMapActivity) getActivity()).getOsmMaps().addMarker(this.startTripEdge.getGeoPoint().getLatitude(), this.startTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_starting_point, false);
            this.startTripEdge.setMarker(startMarker);
        }

        if (this.destinationTripEdge != null) {
            ((RideCreationMapActivity) getActivity()).getOsmMaps().removeMarker(this.destinationTripEdge.getMarker());

            this.destinationTripEdge = new TripEdge(this.destinationTripEdge.getGeoPoint(), this.destinationTripEdge.getAddress());
            Marker destinationMarker = ((RideCreationMapActivity) getActivity()).getOsmMaps().addMarker(this.destinationTripEdge.getGeoPoint().getLatitude(), this.destinationTripEdge.getGeoPoint().getLongitude(), R.mipmap.ic_destination_point, false);
            this.destinationTripEdge.setMarker(destinationMarker);
        }
    }

    public void showAddressGenericError() {
        WidgetHelper.showToast(getContext(), R.string.error_during_get_address);
    }

    public void showNotFoundAddress() {
        WidgetHelper.showToast(getContext(), R.string.address_not_found);
    }

    public void showGenericError() {
        WidgetHelper.showToast(getActivity(), R.string.generic_error);
    }

    public void showNetworkError() {
        WidgetHelper.showToast(getActivity(), R.string.no_connection_error);
    }

    public void showNoRoutesFoundError() {
        WidgetHelper.showToast(getActivity(), R.string.no_routes_found_error);
    }

    private void setFragmentArgument() {
        ((RideCreationMapActivity) getActivity()).setRoads(this.roads);
        ((RideCreationMapActivity) getActivity()).setWayPoints(this.points);
    }

    @Override
    public void onItemClickListener(View v, int position) {

        this.points.remove(position);
        this.wayPointsAdapter.removeItem(position);

        this.setRecycleViewHeightByPoints();

        this.executeDirectionRequest(this.points);
    }
}
