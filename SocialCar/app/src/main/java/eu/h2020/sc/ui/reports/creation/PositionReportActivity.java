package eu.h2020.sc.ui.reports.creation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.report.Geometry;
import eu.h2020.sc.domain.report.Location;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.location.PositionManager;
import eu.h2020.sc.location.PositionManagerCallback;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.tasks.AddressGeocodingTask;
import eu.h2020.sc.tasks.PositionGeocodingTask;
import eu.h2020.sc.ui.home.trip.search.GoogleLatLngBoundsTask;
import eu.h2020.sc.ui.reports.creation.task.ReportCreationTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.WidgetHelper;

import static eu.h2020.sc.SocialCarApplication.getContext;

public class PositionReportActivity extends GeneralActivity implements View.OnClickListener, PositionManagerCallback, ResultCallback<LocationSettingsResult> {

    public static final String REPORT_KEY = PositionReportActivity.class.getSimpleName();

    private final int REQUEST_CODE_ADDRESS_START = 1;
    private final int REQUEST_CODE_ADDRESS_DESTINATION = 2;
    private final int REQUEST_PERMISSION_GPS = 1002;
    private final int REQUEST_CODE_TURN_ON_GPS = 1003;

    private TextView textViewFrom;
    private TextView textViewTo;
    private ImageView imageViewGeoFrom;
    private ImageView imageViewGeoTo;
    private Button buttonSendReport;

    private Place fromPlace;
    private Place toPlace;

    private PositionManager positionManager;
    private Report report;

    private int requestCodeByGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_report);
        this.report = (Report) ActivityUtils.getSerializableFromIntent(this, REPORT_KEY);
        this.positionManager = new PositionManager(this, 0, this);
        this.positionManager.setResultCallBackLocationSettings(this);
        this.initUI();
        this.initListeners();
    }

    private void initUI() {
        this.textViewFrom = (TextView) findViewById(R.id.activity_position_report_tv_from);
        this.textViewTo = (TextView) findViewById(R.id.activity_position_report_tv_to);
        this.imageViewGeoFrom = (ImageView) findViewById(R.id.activity_position_report_btn_from);
        this.imageViewGeoTo = (ImageView) findViewById(R.id.activity_position_report_btn_to);
        this.buttonSendReport = (Button) findViewById(R.id.button_report_send);
    }

    private void initListeners() {
        this.textViewFrom.setOnClickListener(this);
        this.textViewTo.setOnClickListener(this);
        this.imageViewGeoFrom.setOnClickListener(this);
        this.imageViewGeoTo.setOnClickListener(this);
    }

    public void sendReport(View view) {
        showProgressDialog();
        ReportCreationTask reportCreationTask = new ReportCreationTask(this);
        reportCreationTask.execute(this.report);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void goToPostReportFacebook(Report report) {
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(PostReportFacebookActivity.REPORT_KEY, report);

        ActivityUtils.openActivityWithObjectParams(this, PostReportFacebookActivity.class, params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.positionManager.getLastPosition();
            } else {
                WidgetHelper.showToast(this, R.string.error_enable_permission_app);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADDRESS_START:
                    this.fromPlace = PlaceAutocomplete.getPlace(this, data);
                    this.executeAddressGeocodingTask(this.fromPlace, REQUEST_CODE_ADDRESS_START);
                    break;
                case REQUEST_CODE_ADDRESS_DESTINATION:
                    this.toPlace = PlaceAutocomplete.getPlace(this, data);
                    this.executeAddressGeocodingTask(this.toPlace, REQUEST_CODE_ADDRESS_DESTINATION);
                    break;
                case REQUEST_CODE_TURN_ON_GPS:
                    this.positionManager.getLastPosition();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_CODE_TURN_ON_GPS:
                    dismissDialog();
                    break;
            }
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(this, REQUEST_CODE_TURN_ON_GPS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(getTagFromActivity(this), "Error during startResolutionForResult: " + e);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_position_report_tv_from:
                this.launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_START);
                break;
            case R.id.activity_position_report_tv_to:
                this.launchPlaceAutoCompleteIntent(REQUEST_CODE_ADDRESS_DESTINATION);
                break;
            case R.id.activity_position_report_btn_from:
                showProgressDialog();
                this.requestCodeByGeo = REQUEST_CODE_ADDRESS_START;
                this.positionManager.getLastPosition();
                break;
            case R.id.activity_position_report_btn_to:
                showProgressDialog();
                this.requestCodeByGeo = REQUEST_CODE_ADDRESS_DESTINATION;
                this.positionManager.getLastPosition();
                break;
        }
    }

    private void launchPlaceAutoCompleteIntent(int requestCode) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(GoogleLatLngBoundsTask.getPresentCountryLatLngBounds())
                    .build(this);
            startActivityForResult(intent, requestCode);

        } catch (GooglePlayServicesRepairableException e) {
            WidgetHelper.showToast(this, R.string.google_play_service_error);
            Log.e(getTagFromActivity(this), e.getMessage());

        } catch (GooglePlayServicesNotAvailableException e) {
            WidgetHelper.showToast(this, R.string.google_play_service_not_available);
            Log.e(getTagFromActivity(this), e.getMessage());
        }
    }

    public void showLocationFromAddress(Address address, Integer addressType) {

        switch (addressType) {
            case REQUEST_CODE_ADDRESS_START:
                this.textViewFrom.setText(OsmGeocodingUtils.convertAddressToString(address));
                this.setLocationReport(address.getLatitude(), address.getLongitude(), OsmGeocodingUtils.convertAddressToString(address));
                this.enableSendReportButton();
                break;
            case REQUEST_CODE_ADDRESS_DESTINATION:
                this.textViewTo.setText(OsmGeocodingUtils.convertAddressToString(address));
                break;
        }

        dismissDialog();
    }

    public void onPositionRetrieved(Address addressFound, int requestID, double userLatitude, double userLongitude) {
        switch (requestID) {
            case REQUEST_CODE_ADDRESS_START:
                this.textViewFrom.setText(OsmGeocodingUtils.convertAddressToString(addressFound));
                this.setLocationReport(userLatitude, userLongitude, OsmGeocodingUtils.convertAddressToString(addressFound));
                this.enableSendReportButton();
                break;
            case REQUEST_CODE_ADDRESS_DESTINATION:
                this.textViewTo.setText(OsmGeocodingUtils.convertAddressToString(addressFound));
                break;
        }

        dismissDialog();
    }

    public void showNotFoundAddress() {
        dismissDialog();
        WidgetHelper.showToast(getContext(), R.string.address_not_found);
    }

    public void showAddressGenericError() {
        WidgetHelper.showToast(getContext(), R.string.error_during_get_address);
    }

    @Override
    public void onPosition(android.location.Location location, int requestID) {
        PositionGeocodingTask positionGeocodingTask = new PositionGeocodingTask(this, this.requestCodeByGeo, location.getLongitude(), location.getLatitude(), this);
        positionGeocodingTask.execute(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void networkError() {
        showConnectionError();
    }

    @Override
    public void genericError(int errorType) {
        showServerGenericError();
    }

    @Override
    public void locationServiceDisable() {
        this.positionManager.promptActiveLocation();
    }

    @Override
    public void permissionDisabled(String permissionType) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_GPS);
    }

    private void setLocationReport(double lat, double lon, String address) {
        Geometry geometry = new Geometry();
        geometry.addCoordinate(lat);
        geometry.addCoordinate(lon);
        Location location = new Location(address, geometry);
        this.report.setLocation(location);
    }


    private void enableSendReportButton() {
        this.buttonSendReport.setAlpha((float) 1);
        this.buttonSendReport.setEnabled(true);
    }

    private void executeAddressGeocodingTask(Place place, int requestCode) {
        AddressGeocodingTask retrievePositionFromDestinationAddress = new AddressGeocodingTask(this, requestCode, getContext());
        retrievePositionFromDestinationAddress.execute(place);
    }
}