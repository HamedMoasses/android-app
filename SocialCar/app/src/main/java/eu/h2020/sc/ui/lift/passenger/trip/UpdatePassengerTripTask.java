package eu.h2020.sc.ui.lift.passenger.trip;

import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.LiftDAO;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class UpdatePassengerTripTask extends AsyncTask<Lift, Void, Integer> {

    private static final int UPDATE_LIFT_GENERIC_ERROR_RESULT = 0;
    private static final int UPDATE_LIFT_CONNECTION_ERROR_RESULT = 1;
    private static final int UPDATE_SERVER_ERROR_RESULT = 2;
    private static final int UPDATE_LIFT_UNAUTHORIZED = 3;
    private static final int UPDATE_LIFT_NOT_FOUND = 4;
    private static final int UPDATE_LIFT_COMPLETED = 5;

    private PassengerTripDetailsActivity passengerTripDetailsActivity;
    private LiftDAO liftDAO;

    private Lift lift;

    public UpdatePassengerTripTask(PassengerTripDetailsActivity passengerTripDetailsActivity) {
        this.passengerTripDetailsActivity = passengerTripDetailsActivity;
        this.liftDAO = new LiftDAO();
    }

    @Override
    protected Integer doInBackground(Lift... lifts) {

        Integer resultCode = UPDATE_LIFT_COMPLETED;

        Lift lift = lifts[0];

        try {
            this.lift = lift;
            this.liftDAO.updateLift(lift);
        } catch (ConnectionException e) {
            return UPDATE_LIFT_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            return UPDATE_LIFT_NOT_FOUND;
        } catch (UnauthorizedException e) {
            return UPDATE_LIFT_UNAUTHORIZED;
        } catch (ServerException e) {
            return UPDATE_LIFT_GENERIC_ERROR_RESULT;
        }
        return resultCode;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.passengerTripDetailsActivity.dismissDialog();

        switch (resultCode) {
            case UPDATE_LIFT_COMPLETED:
                this.passengerTripDetailsActivity.onLiftCancelled(this.lift);
                break;
            case UPDATE_LIFT_GENERIC_ERROR_RESULT:
            case UPDATE_SERVER_ERROR_RESULT:
            case UPDATE_LIFT_NOT_FOUND:
                this.passengerTripDetailsActivity.showServerGenericError();
                this.passengerTripDetailsActivity.finish();
                break;
            case UPDATE_LIFT_CONNECTION_ERROR_RESULT:
                this.passengerTripDetailsActivity.showConnectionError();
                this.passengerTripDetailsActivity.finish();
                break;
            case UPDATE_LIFT_UNAUTHORIZED:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.passengerTripDetailsActivity.showUnauthorizedError();
                this.passengerTripDetailsActivity.goToSignInActivity();
                break;
        }
    }
}
