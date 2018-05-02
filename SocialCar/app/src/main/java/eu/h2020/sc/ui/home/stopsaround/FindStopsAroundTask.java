/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.ui.home.stopsaround;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.dao.PublicTransportDAO;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Stop;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.home.trip.search.TripSearchOSMFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khairul.alam on 19/01/17.
 */

public class FindStopsAroundTask extends AsyncTask<Point, Void, Integer> {

    private final String TAG = this.getClass().getName();
    private PublicTransportDAO publicTransportDao;
    private TripSearchOSMFragment tripSearchOSMFragment;
    private List<Stop> stops;

    public static final int RESULT_FIND_STOPS_AROUND_SUCCEDED = 0;
    public static final int RESULT_GENERIC_ERROR = 1;
    public static final int RESULT_CONNECTION_ERROR = 2;

    public FindStopsAroundTask(TripSearchOSMFragment tripSearchOSMFragment) {
        this.publicTransportDao = new PublicTransportDAO();
        this.stops = new ArrayList<>();
        this.tripSearchOSMFragment = tripSearchOSMFragment;
    }

    @Override
    protected Integer doInBackground(Point... points) {

        try {
            this.stops = this.publicTransportDao.findStopsAround(points[0]);
        } catch (ServerException | UnauthorizedException | NotFoundException e) {
            Log.e(TAG, "Something went wrong ...", e);
            return RESULT_GENERIC_ERROR;
        } catch (ConnectionException e) {
            Log.e(TAG, "Connection error ...", e);
            return RESULT_CONNECTION_ERROR;
        }
        return RESULT_FIND_STOPS_AROUND_SUCCEDED;
    }

    @Override
    protected void onPostExecute(Integer resultcode) {
        switch (resultcode) {
            case RESULT_FIND_STOPS_AROUND_SUCCEDED:
                this.tripSearchOSMFragment.setListStopsAround(this.stops);
                this.tripSearchOSMFragment.updateMapMarkers();
                break;
            case RESULT_GENERIC_ERROR:
                this.tripSearchOSMFragment.genericError(0);
                break;
            case RESULT_CONNECTION_ERROR:
                this.tripSearchOSMFragment.networkError();
                break;
            default:
                break;
        }
    }

}
