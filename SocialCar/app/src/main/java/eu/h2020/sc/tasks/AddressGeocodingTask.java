package eu.h2020.sc.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.places.Place;

import java.io.IOException;

import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.ui.home.trip.search.TripSearchOSMFragment;
import eu.h2020.sc.ui.reports.creation.PositionReportActivity;
import eu.h2020.sc.ui.ride.creation.fragment.RideAddWayPointsFragment;
import eu.h2020.sc.ui.ride.creation.fragment.RideCreationMapFragment;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class AddressGeocodingTask extends AsyncTask<Place, Void, Integer> {

    private static final String TAG = AddressGeocodingTask.class.getSimpleName();

    private static final int RETRIEVE_POSITION_COMPLETED_RESULT = 1;
    private static final int RETRIEVE_POSITION_GENERIC_ERROR = 2;
    private static final int RETRIEVE_POSITION_NOT_FOUND_ERROR = 3;

    private TripSearchOSMFragment tripSearchOSMFragment;
    private RideCreationMapFragment rideCreationMapFragment;
    private RideAddWayPointsFragment rideAddWayPointsFragment;

    @SuppressLint("StaticFieldLeak")
    private PositionReportActivity positionReportActivity;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private Address address;
    private Integer addressType;

    public AddressGeocodingTask(TripSearchOSMFragment tripSearchOSMFragment, Integer addressType, Context context) {
        this.tripSearchOSMFragment = tripSearchOSMFragment;
        this.addressType = addressType;
        this.context = context;
    }

    public AddressGeocodingTask(RideCreationMapFragment rideCreationMapFragment, Integer addressType, Context context) {
        this.rideCreationMapFragment = rideCreationMapFragment;
        this.addressType = addressType;
        this.context = context;
    }

    public AddressGeocodingTask(RideAddWayPointsFragment rideAddWayPointsFragment, Context context) {
        this.rideAddWayPointsFragment = rideAddWayPointsFragment;
        this.context = context;
    }

    public AddressGeocodingTask(PositionReportActivity positionReportActivity, Integer addressType, Context context) {
        this.positionReportActivity = positionReportActivity;
        this.addressType = addressType;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Place... places) {
        Place place = places[0];

        try {
            this.address = OsmGeocodingUtils.getAddressFromStreet(place, this.context);
        } catch (NotFoundException e) {
            Log.e(TAG, e.getMessage());
            return RETRIEVE_POSITION_NOT_FOUND_ERROR;

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return RETRIEVE_POSITION_GENERIC_ERROR;
        }

        return RETRIEVE_POSITION_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {

            case RETRIEVE_POSITION_NOT_FOUND_ERROR:

                if (this.tripSearchOSMFragment != null)
                    this.tripSearchOSMFragment.showNotFoundAddress();

                else if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showNotFoundAddress();

                else if (this.positionReportActivity != null)
                    this.positionReportActivity.showNotFoundAddress();

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showNotFoundAddress();

                break;

            case RETRIEVE_POSITION_GENERIC_ERROR:

                if (this.tripSearchOSMFragment != null)
                    this.tripSearchOSMFragment.showAddressGenericError();

                else if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showAddressGenericError();

                else if (this.positionReportActivity != null)
                    this.positionReportActivity.showAddressGenericError();

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showAddressGenericError();

                break;

            case RETRIEVE_POSITION_COMPLETED_RESULT:

                if (this.tripSearchOSMFragment != null)
                    this.tripSearchOSMFragment.showLocationFromAddress(this.address, this.addressType);

                else if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showLocationFromAddress(this.address, this.addressType);

                else if (this.positionReportActivity != null)
                    this.positionReportActivity.showLocationFromAddress(this.address, this.addressType);

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showLocationFromAddress(this.address);

                break;
        }
    }

}
