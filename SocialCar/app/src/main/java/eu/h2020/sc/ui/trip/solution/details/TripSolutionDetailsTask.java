package eu.h2020.sc.ui.trip.solution.details;

import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.LiftDAO;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.Step;
import eu.h2020.sc.domain.TimeSpacePoint;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class TripSolutionDetailsTask extends AsyncTask<Trip, Void, Integer> {

    private static final int LIFT_CONNECTION_ERROR_RESULT = 0;
    private static final int LIFT_SERVER_ERROR_RESULT = 1;
    private static final int LIFT_ALREADY_EXISTS_RESULT = 2;
    private static final int LIFT_UNAUTHORIZED = 3;
    private static final int LIFT_COMPLETED_RESULT = 4;

    private TripSolutionDetailsActivity tripSolutionDetailsActivity;
    private LiftDAO liftDAO;
    private SocialCarStore socialCarStore;
    private String rideID;
    private String carID;
    private String driverID;

    public TripSolutionDetailsTask(TripSolutionDetailsActivity tripSolutionDetailsActivity, String rideID, String carID, String driverID) {
        this.tripSolutionDetailsActivity = tripSolutionDetailsActivity;
        this.liftDAO = new LiftDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
        this.rideID = rideID;
        this.carID = carID;
        this.driverID = driverID;
    }

    @Override
    protected Integer doInBackground(Trip... trips) {

        Trip trip = trips[0];

        Step step = trip.getDriverStep();

        TimeSpacePoint startPoint = step.getRoute().getStartPoint();
        TimeSpacePoint endPoint = step.getRoute().getEndPoint();

        User user = this.socialCarStore.getUser();

        Lift lift = new Lift(user.getId(), trip, startPoint, endPoint, this.rideID, this.carID, this.driverID);

        try {
            this.liftDAO.createLift(lift);
        } catch (ServerException e) {
            return LIFT_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return LIFT_CONNECTION_ERROR_RESULT;
        } catch (ConflictException e) {
            return LIFT_ALREADY_EXISTS_RESULT;
        } catch (UnauthorizedException e) {
            return LIFT_UNAUTHORIZED;
        }

        return LIFT_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.tripSolutionDetailsActivity.dismissDialog();
        switch (resultCode) {
            case LIFT_COMPLETED_RESULT:
                this.tripSolutionDetailsActivity.goToLiftActivity();
                break;
            case LIFT_UNAUTHORIZED:
                this.socialCarStore.removeAllUserInfo();
                this.tripSolutionDetailsActivity.showUnauthorizedError();
                this.tripSolutionDetailsActivity.goToSignInActivity();
                break;
            case LIFT_CONNECTION_ERROR_RESULT:
                this.tripSolutionDetailsActivity.showConnectionError();
                break;
            case LIFT_SERVER_ERROR_RESULT:
            case LIFT_ALREADY_EXISTS_RESULT:
                this.tripSolutionDetailsActivity.showServerGenericError();
                break;
        }
    }
}
