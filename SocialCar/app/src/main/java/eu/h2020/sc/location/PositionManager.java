package eu.h2020.sc.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import eu.h2020.sc.GeneralActivity;


public class PositionManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String KEY_LAST_KNOWN_POINT = "KEY_LAST_KNOWN_POINT";

    private final int MS_DELAY_POSITION = 6000;

    private final int DEFAULT_VALUE_REQUEST_TYPE = 0;

    protected GoogleApiClient googleApiClient;
    protected PositionManagerCallback callBack;
    protected GeneralActivity activity;
    private LocationManager locationManager;
    private ConnectivityManager connectivityManager;
    protected ResultCallback<LocationSettingsResult> resultCallBackLocationSettings;
    private int requestType = DEFAULT_VALUE_REQUEST_TYPE;
    private Location lastLocation;

    public PositionManager(GeneralActivity activity, int requestType, PositionManagerCallback callBack) {
        this.activity = activity;
        this.callBack = callBack;
        this.requestType = requestType;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        buildGoogleApiClient();
        initLocationListener();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
    }

    private void initLocationListener() {
    }

    public void stopAutoManage(FragmentActivity activity) {
        if (this.googleApiClient != null) {
            this.googleApiClient.stopAutoManage(activity);
            this.googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLastPosition();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (result.getErrorCode() == ConnectionResult.NETWORK_ERROR)
            callBack.networkError();

        else if (result.getErrorCode() == ConnectionResult.RESOLUTION_REQUIRED)
            googleApiClient.connect();
        else
            callBack.genericError(result.getErrorCode());

    }

    public void getLastPosition() {

        if (googleApiClient.isConnected()) {
            if (isGpsEnable(this.activity)) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            googleApiClient);

                    if (lastLocation != null) {
                        callBack.onPosition(lastLocation, this.requestType);

                    } else {
                        LocationRequest locationRequest = this.buildRequestPosition();
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                googleApiClient, locationRequest, this);
                    }
                } else {
                    callBack.permissionDisabled(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else
                callBack.locationServiceDisable();
        } else
            googleApiClient.connect();
    }

    public static boolean isGpsEnable(Activity activity) {
        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    public boolean isCanGetLocation() {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    public boolean isConnectionAvailable() {
        NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    private LocationRequest buildRequestPosition() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);

        if (isCanGetLocation() && isConnectionAvailable()) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }

        return mLocationRequest;
    }

    public void promptActiveLocation() {

        GoogleApiClient googleApiClient = this.getGoogleApiClient();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this.resultCallBackLocationSettings);
    }

    public void setResultCallBackLocationSettings(ResultCallback<LocationSettingsResult> resultCallBackLocationSettings) {
        this.resultCallBackLocationSettings = resultCallBackLocationSettings;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;

        if (this.lastLocation != null) {
            callBack.onPosition(this.lastLocation, this.requestType);
        } else {
            callBack.genericError(0);
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
    }
}
