package eu.h2020.sc.ui.drawer.task;

import android.os.AsyncTask;
import android.util.Log;

import eu.h2020.sc.GeneralActivity;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class RendezvousLiftTask {

    private static final String TAG = RendezvousLiftTask.class.getSimpleName();

    public int counterTasksResult;

    private RetrieveLiftAsPassengerTask retrieveLiftAsPassengerTask;
    private RetrieveLiftAsDriverTask retrieveLiftAsDriverTask;

    private GeneralActivity activity;

    public RendezvousLiftTask(GeneralActivity activity) {
        this.activity = activity;
    }

    public void execute() {

        this.retrieveLiftAsPassengerTask = new RetrieveLiftAsPassengerTask(this);
        this.retrieveLiftAsDriverTask = new RetrieveLiftAsDriverTask(this);

        this.retrieveLiftAsPassengerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.retrieveLiftAsDriverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized void onSuccess() {

        int numTasksResult = 2;

        if (this.counterTasksResult == numTasksResult) {

            Log.i(TAG, "Retrieved lifts successfully!");

            LiftsController.getInstance().syncRequestedLifts(this.retrieveLiftAsPassengerTask.getLifts());
            LiftsController.getInstance().syncOfferedLifts(this.retrieveLiftAsDriverTask.getLifts());
        }
    }


    public synchronized void serverError() {
        LiftsController.getInstance().clearLifts();
        this.activity.hideServerError();
    }

    public synchronized void noConnectionError() {
        LiftsController.getInstance().clearLifts();
        this.activity.hideNoConnectionError();
    }

    public synchronized void unauthorizedError() {
        LiftsController.getInstance().clearLifts();
        this.activity.showUnauthorizedError();
        this.activity.goToSignInActivity();
    }
}
