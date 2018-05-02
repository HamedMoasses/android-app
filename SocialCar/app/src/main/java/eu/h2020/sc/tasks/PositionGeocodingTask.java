package eu.h2020.sc.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.ui.home.trip.search.TripSearchOSMFragment;
import eu.h2020.sc.ui.reports.creation.PositionReportActivity;
import eu.h2020.sc.ui.ride.creation.fragment.RideCreationMapFragment;
import eu.h2020.sc.ui.trip.recording.TripRecordedMapActivity;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class PositionGeocodingTask extends AsyncTask<GeoPoint, Void, Integer> {

    private static final String TAG = PositionGeocodingTask.class.getSimpleName();

    private static final int RETRIEVE_POSITION_COMPLETED_RESULT = 1;
    private static final int RETRIEVE_POSITION_GENERIC_ERROR = 2;

    private TripSearchOSMFragment tripSearchOSMFragment;
    private RideCreationMapFragment rideCreationMapFragment;

    @SuppressLint("StaticFieldLeak")
    private PositionReportActivity positionReportActivity;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private Address addressFound;

    private int requestID;
    private double userLatitude;
    private double userLongitude;

    public PositionGeocodingTask(TripSearchOSMFragment tripSearchOSMFragment, int requestID, double latitude, double longitude, Context context) {
        this.tripSearchOSMFragment = tripSearchOSMFragment;
        this.context = context;
        this.requestID = requestID;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
    }

    public PositionGeocodingTask(RideCreationMapFragment rideCreationMapFragment, int requestID, double latitude, double longitude, Context context) {
        this.rideCreationMapFragment = rideCreationMapFragment;
        this.context = context;
        this.requestID = requestID;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
    }

    public PositionGeocodingTask(PositionReportActivity positionReportActivity, int requestID, double latitude, double longitude, Context context) {
        this.positionReportActivity = positionReportActivity;
        this.context = context;
        this.requestID = requestID;
        this.userLatitude = latitude;
        this.userLongitude = longitude;
    }

    @Override
    protected Integer doInBackground(GeoPoint... geoPoints) {
        GeoPoint geoPoint = geoPoints[0];
        try {
            this.addressFound = OsmGeocodingUtils.convertLocationToAddress(geoPoint.getLatitude(), geoPoint.getLongitude(), this.context);
        } catch (NotFoundException | IOException e) {
            Log.e(TAG, e.getMessage());
            return RETRIEVE_POSITION_GENERIC_ERROR;
        }
        return RETRIEVE_POSITION_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {
            case RETRIEVE_POSITION_GENERIC_ERROR:
                if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.genericError(0);
                else if (this.tripSearchOSMFragment != null)
                    this.tripSearchOSMFragment.genericError(0);
                else if (this.positionReportActivity != null) {
                    this.positionReportActivity.genericError(0);
                }
                break;
            case RETRIEVE_POSITION_COMPLETED_RESULT:
                if (this.tripSearchOSMFragment != null)
                    this.tripSearchOSMFragment.onPositionRetrieved(this.addressFound, this.requestID, this.userLatitude, this.userLongitude);
                else if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.onPositionRetrieved(this.addressFound, this.requestID, this.userLatitude, this.userLongitude);
                else if (this.positionReportActivity != null) {
                    this.positionReportActivity.onPositionRetrieved(this.addressFound, this.requestID, this.userLatitude, this.userLongitude);
                }
                break;
        }
    }

}
