package eu.h2020.sc.ui.ride.creation.task;

import android.os.AsyncTask;

import eu.h2020.sc.dao.RideDAO;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationFormActivity;

/**
 * Created by Pietro on 26/09/16.
 */

public class RideCreationTask extends AsyncTask<Ride, Void, Integer> {

    private static final int RIDE_CREATION_CONNECTION_ERROR_RESULT = 0;
    private static final int RIDE_CREATION_SERVER_ERROR_RESULT = 1;
    private static final int RIDE_CREATION_ALREADY_EXISTS_RESULT = 2;
    private static final int RIDE_CREATION_UNAUTHORIZED = 3;
    private static final int RIDE_CREATION_COMPLETED_RESULT = 4;

    private RideCreationFormActivity rideCreationFormActivity;
    private RideDAO rideDAO;

    public RideCreationTask(RideCreationFormActivity rideCreationFormActivity) {
        this.rideCreationFormActivity = rideCreationFormActivity;
        this.rideDAO = new RideDAO();
    }

    @Override
    protected Integer doInBackground(Ride... rides) {

        Ride ride = rides[0];

        try {
            this.rideDAO.createRide(ride);
        } catch (ServerException e) {
            return RIDE_CREATION_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return RIDE_CREATION_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return RIDE_CREATION_UNAUTHORIZED;
        } catch (ConflictException e) {
            return RIDE_CREATION_ALREADY_EXISTS_RESULT;
        }

        return RIDE_CREATION_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.rideCreationFormActivity.dismissDialog();

        switch (resultCode) {
            case RIDE_CREATION_COMPLETED_RESULT:
                this.rideCreationFormActivity.goToHome();
                break;
            case RIDE_CREATION_SERVER_ERROR_RESULT:
                this.rideCreationFormActivity.showServerGenericError();
                break;
            case RIDE_CREATION_CONNECTION_ERROR_RESULT:
                this.rideCreationFormActivity.showConnectionError();
                break;
            case RIDE_CREATION_UNAUTHORIZED:
            case RIDE_CREATION_ALREADY_EXISTS_RESULT:
                this.rideCreationFormActivity.showUnauthorizedError();
                break;
        }
    }

    public boolean validateRide(Ride ride) {
        boolean valid = true;

        if (!ride.isNameValid()) {
            this.rideCreationFormActivity.showTripNameMarkerError();
            valid = false;
        } else {
            this.rideCreationFormActivity.hideTripNameMarkerError();
        }

        if (!ride.isAfterNow()) {
            this.rideCreationFormActivity.showTripDateTimeMarkerError();
            valid = false;
        } else {
            this.rideCreationFormActivity.hideTripDateTimeMarkerError();
        }

        if (!valid) {
            this.rideCreationFormActivity.showMissingFieldsMessage();
        }

        return valid;
    }


}
