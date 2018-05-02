package eu.h2020.sc.ui.trip.solution;

import android.os.AsyncTask;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.TripSolutionsRequest;
import eu.h2020.sc.transport.GetHttpsTask;

/**
 * Created by khairul.alam on 30/08/17.
 */

public class TripSolutionsSearchTask extends AsyncTask<Void, Void, Integer> {

    private static final int TRIP_SEARCH_COMPLETED = 1;
    private static final int CONNECTION_ERROR = 2;
    private static final int UNAUTHORIZED_ERROR = 3;
    private static final int NOT_FOUND_ERROR = 4;
    private static final int GENERIC_ERROR = 5;

    private TripSolutionsActivity tripSolutionsActivity;
    private TripSolutionsRequest tripSolutionsRequest;

    private String jsonResult;

    public TripSolutionsSearchTask(TripSolutionsActivity tripSolutionsActivity, TripSolutionsRequest tripSolutionsRequest) {
        this.tripSolutionsActivity = tripSolutionsActivity;
        this.tripSolutionsRequest = tripSolutionsRequest;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        GetHttpsTask getHttpsTask = new GetHttpsTask();
        try {
            this.jsonResult = getHttpsTask.makeRequest(this.tripSolutionsRequest);
        } catch (ConnectionException e) {
            return CONNECTION_ERROR;
        } catch (ServerException e) {
            return GENERIC_ERROR;
        } catch (UnauthorizedException e) {
            return UNAUTHORIZED_ERROR;
        } catch (NotFoundException e) {
            return NOT_FOUND_ERROR;
        }
        return TRIP_SEARCH_COMPLETED;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {

        switch (resultCode) {
            case TRIP_SEARCH_COMPLETED:
                this.tripSolutionsActivity.onTripSolutionsResult(this.jsonResult);
                break;
            case CONNECTION_ERROR:
                this.tripSolutionsActivity.showConnectionError();
                break;
            case NOT_FOUND_ERROR:
                this.tripSolutionsActivity.showNotFoundError();
                break;
            case UNAUTHORIZED_ERROR:
                this.tripSolutionsActivity.showUnauthorizedError();
                break;
            case GENERIC_ERROR:
                this.tripSolutionsActivity.showServerGenericError();
                break;
        }
    }
}
