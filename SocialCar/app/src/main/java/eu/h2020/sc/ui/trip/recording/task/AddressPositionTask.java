package eu.h2020.sc.ui.trip.recording.task;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;
import eu.h2020.sc.ui.trip.recording.TripRecordedMapActivity;

/**
 * Created by pietro on 15/03/2018.
 */

public class AddressPositionTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = AddressPositionTask.class.getSimpleName();

    private static final int RETRIEVE_POSITION_COMPLETED_RESULT = 1;
    private static final int RETRIEVE_POSITION_GENERIC_ERROR = 2;

    private TripRecordedMapActivity recordedMapActivity;
    private GeoPoint startPoint;
    private GeoPoint destinationPoint;
    private String startAddressName;
    private String destinationAddressName;

    public AddressPositionTask(TripRecordedMapActivity recordedMapActivity, GeoPoint startPoint, GeoPoint destinationPoint) {
        this.recordedMapActivity = recordedMapActivity;
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
    }


    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            Address startAddress = OsmGeocodingUtils.convertLocationToAddress(this.startPoint.getLatitude(), this.startPoint.getLongitude(), this.recordedMapActivity);
            Address destinationAddress = OsmGeocodingUtils.convertLocationToAddress(this.destinationPoint.getLatitude(), this.destinationPoint.getLongitude(), this.recordedMapActivity);
            this.startAddressName = OsmGeocodingUtils.convertAddressToString(startAddress);
            this.destinationAddressName = OsmGeocodingUtils.convertAddressToString(destinationAddress);
        } catch (NotFoundException | IOException e) {
            Log.e(TAG, e.getMessage());
            return RETRIEVE_POSITION_GENERIC_ERROR;
        }
        return RETRIEVE_POSITION_COMPLETED_RESULT;

    }


    @Override
    protected void onPostExecute(Integer resultCode) {

        this.recordedMapActivity.dismissDialog();

        switch (resultCode) {
            case RETRIEVE_POSITION_GENERIC_ERROR:
                this.recordedMapActivity.showServerGenericError();
                break;
            case RETRIEVE_POSITION_COMPLETED_RESULT:
                this.recordedMapActivity.goToRideCreationFormActivity(this.startAddressName, this.destinationAddressName);
                break;
        }
    }
}
