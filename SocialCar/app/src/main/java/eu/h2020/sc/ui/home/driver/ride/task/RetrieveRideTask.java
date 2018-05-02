package eu.h2020.sc.ui.home.driver.ride.task;

import android.os.AsyncTask;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.RideDAO;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.home.driver.DriverFragment;

/**
 * Created by Pietro on 20/09/16.
 */
public class RetrieveRideTask extends AsyncTask<Void, Void, Integer> {

    private static final int RETRIEVE_RIDE_GENERIC_ERROR_RESULT = 0;
    private static final int RETRIEVE_RIDE_CONNECTION_ERROR_RESULT = 1;
    private static final int RETRIEVE_SERVER_ERROR_RESULT = 2;
    private static final int RETRIEVE_RIDE_UNAUTHORIZED = 3;
    private static final int RETRIEVE_RIDE_NOT_FOUND = 4;
    private static final int RETRIEVE_RIDE_COMPLETED_WITH_DATA = 5;
    private static final int RETRIEVE_RIDE_COMPLETED_NO_DATA = 6;


    private DriverFragment driverFragment;
    private SocialCarStore socialCarStore;
    private RideDAO rideDAO;
    private List<Ride> rides;

    public RetrieveRideTask(DriverFragment driverFragment) {
        this.driverFragment = driverFragment;
        this.socialCarStore = SocialCarApplication.getInstance();
        this.rideDAO = new RideDAO();
    }

    public boolean isUserDriver() {
        User user = this.socialCarStore.getUser();
        return user.isDriver();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        Integer resultCode = RETRIEVE_RIDE_COMPLETED_WITH_DATA;

        User user = this.socialCarStore.getUser();

        if (user != null) {
            String userID = user.getId();
            try {
                this.rides = this.rideDAO.findRidesByUserID(userID);

                if (this.rides.size() < 1) {
                    return RETRIEVE_RIDE_COMPLETED_NO_DATA;
                }
            } catch (ServerException e) {
                return RETRIEVE_SERVER_ERROR_RESULT;
            } catch (ConnectionException e) {
                return RETRIEVE_RIDE_CONNECTION_ERROR_RESULT;
            } catch (UnauthorizedException e) {
                return RETRIEVE_RIDE_UNAUTHORIZED;
            } catch (NotFoundException e) {
                return RETRIEVE_RIDE_NOT_FOUND;
            } catch (JSONException e) {
                return RETRIEVE_RIDE_GENERIC_ERROR_RESULT;
            }
        } else {
            return RETRIEVE_RIDE_UNAUTHORIZED;
        }

        return resultCode;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {

        this.driverFragment.dismissLoader();

        switch (resultCode) {
            case RETRIEVE_RIDE_COMPLETED_WITH_DATA:
                List<Ride> sortedRides = sortRidesInReverseOrder(this.rides);
                this.driverFragment.showRideLayout(sortedRides);
                break;
            case RETRIEVE_RIDE_COMPLETED_NO_DATA:
                this.driverFragment.showOfferRideLayout();
                break;
            case RETRIEVE_RIDE_UNAUTHORIZED:
                this.driverFragment.showUnauthorizedError();
                break;
            case RETRIEVE_RIDE_CONNECTION_ERROR_RESULT:
                this.driverFragment.showLayoutNoInternetConnection();
                break;
            default:
                this.driverFragment.showLayoutGenericError();
                break;
        }
    }

    private List<Ride> sortRidesInReverseOrder(List<Ride> rides) {
        List<Ride> sortedRides = new ArrayList<>();
        for (int i = rides.size() - 1; i >= 0; i--) {
            sortedRides.add(rides.get(i));
        }
        return sortedRides;
    }
}
