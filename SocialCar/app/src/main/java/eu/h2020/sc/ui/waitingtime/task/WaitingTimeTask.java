package eu.h2020.sc.ui.waitingtime.task;


import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.PublicTransportDAO;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.domain.Stop;
import eu.h2020.sc.ui.waitingtime.WaitingTimeActivity;

import java.util.Collections;

/**
 * Created by Pietro on 28/09/16.
 */

public class WaitingTimeTask extends AsyncTask<String, Void, Integer> {

    private final String TAG = WaitingTimeTask.class.getCanonicalName();

    private static final int WAITING_TIME_SERVER_ERROR_RESULT = 0;
    private static final int WAITING_TIME_CONNECTION_ERROR_RESULT = 1;
    private static final int WAITING_TIME_UNAUTHORIZED = 2;
    private static final int WAITING_TIME_NOT_FOUND = 3;
    private static final int WAITING_TIME_COMPLETED = 4;

    private WaitingTimeActivity waitingTimeActivity;
    private PublicTransportDAO publicTransportDAO;
    private Stop stop;

    public WaitingTimeTask(WaitingTimeActivity waitingTimeActivity) {
        this.publicTransportDAO = new PublicTransportDAO();
        this.waitingTimeActivity = waitingTimeActivity;
    }

    @Override
    protected Integer doInBackground(String... stopCodes) {

        String stopCode = stopCodes[0];

        try {
            this.stop = this.publicTransportDAO.getWaitingTime(stopCode);

            Collections.sort(this.stop.getTransits(), new TransitStopDistanceComparator());
            Collections.sort(this.stop.getTransits(), new TransitTimeComparator());

        } catch (ConnectionException e) {
            return WAITING_TIME_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            return WAITING_TIME_NOT_FOUND;
        } catch (UnauthorizedException e) {
            return WAITING_TIME_UNAUTHORIZED;
        } catch (ServerException e) {
            return WAITING_TIME_SERVER_ERROR_RESULT;
        }

        return WAITING_TIME_COMPLETED;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {
            case WAITING_TIME_SERVER_ERROR_RESULT:
                this.waitingTimeActivity.onInternalServerError();
                break;
            case WAITING_TIME_CONNECTION_ERROR_RESULT:
                this.waitingTimeActivity.dismissProgressDialog();
                this.waitingTimeActivity.onConnectionError();
                break;
            case WAITING_TIME_NOT_FOUND:
                this.waitingTimeActivity.showNotFoundStop();
                break;
            case WAITING_TIME_UNAUTHORIZED:
                SocialCarApplication.getInstance().removeAllUserInfo();
                this.waitingTimeActivity.showUnauthorizedError();
                this.waitingTimeActivity.goToSignInActivity();
                break;
            case WAITING_TIME_COMPLETED:
                this.waitingTimeActivity.dismissProgressDialog();
                this.waitingTimeActivity.showWaitingTime(this.stop);
                break;
        }
    }
}
