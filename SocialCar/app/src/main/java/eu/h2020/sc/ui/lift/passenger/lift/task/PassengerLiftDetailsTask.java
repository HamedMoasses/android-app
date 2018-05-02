package eu.h2020.sc.ui.lift.passenger.lift.task;

import android.os.AsyncTask;
import android.util.Log;

import eu.h2020.sc.R;
import eu.h2020.sc.ui.lift.passenger.lift.PassengerLiftDetailsActivity;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class PassengerLiftDetailsTask {

    private static final String TAG = PassengerLiftDetailsTask.class.getSimpleName();

    public int counterTasksResult;

    private PassengerLiftDetailsActivity activity;
    private PassengerLiftUserDetailsTask liftUserDetailsTask;
    private PassengerLiftFeedbackTask passengerLiftFeedbackTask;

    public PassengerLiftDetailsTask(PassengerLiftDetailsActivity activity) {
        this.activity = activity;
    }

    public void execute(String userID) {

        this.liftUserDetailsTask = new PassengerLiftUserDetailsTask(this);
        this.passengerLiftFeedbackTask = new PassengerLiftFeedbackTask(this);

        this.liftUserDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID);
        this.passengerLiftFeedbackTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID);
    }

    public synchronized void onSuccess() {

        int numTasksResult = 2;

        if (this.counterTasksResult == numTasksResult) {

            Log.i(TAG, "FEEDBACK LIST SIZE : " + this.passengerLiftFeedbackTask.getFeedbackList().size());
            Log.i(TAG, "PASSENGER : " + this.liftUserDetailsTask.getPassenger());

            this.activity.showFeedback(this.passengerLiftFeedbackTask.getFeedbackList());
            this.activity.setPassenger(this.liftUserDetailsTask.getPassenger());
            this.activity.showUserDetails(this.liftUserDetailsTask.getPassenger());
            this.activity.showRatingWithFeedback(this.liftUserDetailsTask.getPassenger(), this.passengerLiftFeedbackTask.getFeedbackList());
            this.activity.showLayoutsDetails();

            this.activity.dismissDialog();
        }
    }

    public synchronized void noServerError() {
        this.activity.dismissDialog();
        this.activity.showErrorLayout(activity.getString(R.string.generic_error));
    }

    public synchronized void noConnectionError() {
        this.activity.dismissDialog();
        this.activity.showErrorLayout(activity.getString(R.string.no_connection_error));
    }

    public synchronized void unauthorizedError() {
        this.activity.dismissDialog();
        this.activity.showUnauthorizedError();
        this.activity.goToSignInActivity();
    }
}
