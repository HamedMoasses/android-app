package eu.h2020.sc.ui.home.driver.ride.task;

import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.dao.RideDAO;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.ui.home.driver.DriverFragment;

/**
 * Created by Pietro on 20/09/16.
 */
public class DeleteRideTask extends AsyncTask<String, Void, Integer> {

    private static final int DELETE_RIDE_CONNECTION_ERROR_RESULT = 0;
    private static final int DELETE_RIDE_SERVER_ERROR_RESULT = 1;
    private static final int DELETE_RIDE_UNAUTHORIZED = 2;
    private static final int DELETE_RIDE_NOT_FOUND = 3;
    private static final int DELETE_RIDE_COMPLETED = 4;

    private DriverFragment driverFragment;
    private RideDAO rideDAO;

    public DeleteRideTask(DriverFragment driverFragment) {
        this.driverFragment = driverFragment;
        this.rideDAO = new RideDAO();
    }

    @Override
    protected Integer doInBackground(String... rides) {
        Integer resultCode = DELETE_RIDE_COMPLETED;

        String rideID = rides[0];

        try {
            this.rideDAO.deleteRideByID(rideID);
        } catch (ConnectionException e) {
            resultCode = DELETE_RIDE_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            resultCode = DELETE_RIDE_NOT_FOUND;
        } catch (UnauthorizedException e) {
            resultCode = DELETE_RIDE_UNAUTHORIZED;
        } catch (ServerException e) {
            resultCode = DELETE_RIDE_SERVER_ERROR_RESULT;
        }

        return resultCode;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {
            case DELETE_RIDE_COMPLETED:
                this.driverFragment.cleanSupportRideToUpdate();
                break;
            case DELETE_RIDE_UNAUTHORIZED:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.driverFragment.showUnauthorizedError();
                this.driverFragment.goToSignInActivity();
                break;
            case DELETE_RIDE_CONNECTION_ERROR_RESULT:
                this.driverFragment.showLayoutNoInternetConnection();
                break;
            case DELETE_RIDE_SERVER_ERROR_RESULT:
                this.driverFragment.undoDelete();
                this.driverFragment.showServerGenericError();
            default:
                this.driverFragment.showLayoutGenericError();
                break;
        }
        this.driverFragment.dismissLoader();
    }
}