package eu.h2020.sc.ui.home.driver.ride.task;

import android.os.AsyncTask;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.dao.RideDAO;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.ui.home.driver.DriverFragment;

/**
 * Created by Pietro on 20/09/16.
 */
public class UpdateRideTask extends AsyncTask<Ride, Void, Integer> {

    private static final int UPDATE_RIDE_GENERIC_ERROR_RESULT = 0;
    private static final int UPDATE_RIDE_CONNECTION_ERROR_RESULT = 1;
    private static final int UPDATE_SERVER_ERROR_RESULT = 2;
    private static final int UPDATE_RIDE_UNAUTHORIZED = 3;
    private static final int UPDATE_RIDE_NOT_FOUND = 4;
    private static final int UPDATE_RIDE_COMPLETED = 5;

    private RideDAO rideDAO;
    private DriverFragment driverFragment;

    public UpdateRideTask(DriverFragment driverFragment) {
        this.driverFragment = driverFragment;
        this.rideDAO = new RideDAO();
    }

    @Override
    protected Integer doInBackground(Ride... rides) {
        Integer resultCode = UPDATE_RIDE_COMPLETED;

        Ride ride = rides[0];
        try {
            this.rideDAO.updateRide(ride);
        } catch (ConnectionException e) {
            resultCode = UPDATE_RIDE_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            resultCode = UPDATE_RIDE_NOT_FOUND;
        } catch (UnauthorizedException e) {
            resultCode = UPDATE_RIDE_UNAUTHORIZED;
        } catch (ServerException e) {
            resultCode = UPDATE_SERVER_ERROR_RESULT;
        }

        return resultCode;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.driverFragment.setUpdateInProgress(false);
        switch (resultCode) {
            case UPDATE_RIDE_COMPLETED:
                this.driverFragment.cleanSupportRideLifts();
                break;
            case UPDATE_RIDE_GENERIC_ERROR_RESULT:
                this.driverFragment.invalidateUpdateRide();
                break;
            case UPDATE_RIDE_CONNECTION_ERROR_RESULT:
                this.driverFragment.invalidateUpdateRide();
                this.driverFragment.showConnectionError();
                break;
            case UPDATE_SERVER_ERROR_RESULT:
                this.driverFragment.invalidateUpdateRide();
                this.driverFragment.showServerGenericError();
                break;
            case UPDATE_RIDE_UNAUTHORIZED:
                this.driverFragment.invalidateUpdateRide();
                this.driverFragment.showUnauthorizedError();
                break;
            case UPDATE_RIDE_NOT_FOUND:
                this.driverFragment.invalidateUpdateRide();
                this.driverFragment.showServerGenericError();
                break;
        }
    }
}
