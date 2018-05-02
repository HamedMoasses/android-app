package eu.h2020.sc.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import eu.h2020.sc.GeneralActivity;

import static eu.h2020.sc.config.Globals.FASTEST_INTERVAL_SEC;
import static eu.h2020.sc.config.Globals.INTERVAL_SEC;
import static eu.h2020.sc.config.Globals.SMALLEST_DISPLACEMENT_METERS;

/**
 * Created by pietro on 13/03/2018.
 */
public class PositionTrackingManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected GeneralActivity activity;
    private GoogleApiClient googleApiClient;
    private PositionTrackingCallback callBack;
    private LocationManager locationManager;
    private ResultCallback<LocationSettingsResult> resultCallBackLocationSetting;
    private int idRequestPermission;
    private ArrayList<GeoPoint> storedTrip;
    private boolean running;

    public PositionTrackingManager(GeneralActivity activity, PositionTrackingCallback callBack, ResultCallback<LocationSettingsResult> resultCallBackLocationSetting, int idRequestPermission) {
        this.activity = activity;
        this.callBack = callBack;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.resultCallBackLocationSetting = resultCallBackLocationSetting;
        this.idRequestPermission = idRequestPermission;
        this.storedTrip = new ArrayList<>();
        this.running = false;
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(this.activity, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
    }


    public boolean isGpsEnable() {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (this.running) {
            this.startTracking();
        }
    }

    public void startTracking() {
        if (googleApiClient.isConnected()) {

            if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (isGpsEnable()) {
                    LocationRequest mLocationRequest = LocationRequest.create();
                    mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_METERS);
                    mLocationRequest.setInterval(INTERVAL_SEC * 1000);
                    mLocationRequest.setFastestInterval(FASTEST_INTERVAL_SEC * 1000);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            googleApiClient, mLocationRequest, this);
                    this.running = true;

                    this.callBack.onStarted();
                } else {
                    this.promptActiveLocation();
                }
            } else {
                ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                        this.idRequestPermission);
            }
        } else {
            this.googleApiClient.connect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }


    public void promptActiveLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this.resultCallBackLocationSetting);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.NETWORK_ERROR)
            callBack.networkError();

        else if (connectionResult.getErrorCode() == ConnectionResult.RESOLUTION_REQUIRED)
            googleApiClient.connect();
        else
            callBack.genericError(connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        this.storedTrip.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    public void deleteStoredTrip() {
        this.storedTrip.clear();
    }

    public void stopTracking() {
        this.running = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
        this.callBack.onStopped();
    }

    public ArrayList<GeoPoint> getStoredTrip() {
        return this.storedTrip;
    }

    public boolean isRunning() {
        return running;
    }

}
