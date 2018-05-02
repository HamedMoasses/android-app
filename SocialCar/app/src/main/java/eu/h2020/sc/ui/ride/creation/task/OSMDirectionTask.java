package eu.h2020.sc.ui.ride.creation.task;

import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.map.OsmMaps;
import eu.h2020.sc.ui.ride.creation.fragment.RideAddWayPointsFragment;
import eu.h2020.sc.ui.ride.creation.fragment.RideCreationMapFragment;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class OSMDirectionTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = OSMDirectionTask.class.getSimpleName();

    private static final int DIRECTION_GENERIC_ERROR_RESULT = 0;
    private static final int DIRECTION_CONNECTION_ERROR_RESULT = 1;
    private static final int DIRECTION_NOT_FOUND_ERROR_RESULT = 2;
    private static final int DIRECTION_RETRIEVED_SUCCESS = 3;

    private GeoPoint startPoint;
    private GeoPoint destinationPoint;
    private RideCreationMapFragment rideCreationMapFragment;
    private RideAddWayPointsFragment rideAddWayPointsFragment;
    private OsmMaps osmMaps;
    private List<Road> roads;

    private List<GeoPoint> additionalWayPoints;

    public OSMDirectionTask(GeoPoint startPoint, GeoPoint destinationPoint, RideCreationMapFragment rideCreationMapFragment, OsmMaps osmMaps) {
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.rideCreationMapFragment = rideCreationMapFragment;
        this.osmMaps = osmMaps;
    }

    public OSMDirectionTask(GeoPoint startPoint, GeoPoint destinationPoint, RideAddWayPointsFragment rideAddWayPointsFragment, OsmMaps osmMaps) {
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.rideAddWayPointsFragment = rideAddWayPointsFragment;
        this.osmMaps = osmMaps;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {

            this.roads = this.osmMaps.getDirections(this.startPoint, this.destinationPoint, this.additionalWayPoints);
            return DIRECTION_RETRIEVED_SUCCESS;

        } catch (ServerException e) {
            Log.e(TAG, "Something went wrong during get direction...", e);
            return DIRECTION_GENERIC_ERROR_RESULT;

        } catch (NotFoundException e) {
            Log.e(TAG, "No route was found...", e);
            return DIRECTION_NOT_FOUND_ERROR_RESULT;

        } catch (ConnectionException e) {
            Log.e(TAG, "No internet connection...", e);
            return DIRECTION_CONNECTION_ERROR_RESULT;
        }
    }

    @Override
    protected void onPostExecute(final Integer resultCode) {

        if (this.rideCreationMapFragment != null)
            ((GeneralActivity) this.rideCreationMapFragment.getActivity()).dismissDialog();

        else if (this.rideAddWayPointsFragment != null)
            ((GeneralActivity) this.rideAddWayPointsFragment.getActivity()).dismissDialog();

        switch (resultCode) {
            case DIRECTION_GENERIC_ERROR_RESULT:

                if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showGenericError();

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showGenericError();

                break;

            case DIRECTION_NOT_FOUND_ERROR_RESULT:

                if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showNoRoutesFoundError();

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showNoRoutesFoundError();

                break;

            case DIRECTION_CONNECTION_ERROR_RESULT:

                if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showNetworkError();

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showNetworkError();

                break;

            case DIRECTION_RETRIEVED_SUCCESS:

                if (this.rideCreationMapFragment != null)
                    this.rideCreationMapFragment.showRoads(this.roads, true);

                else if (this.rideAddWayPointsFragment != null)
                    this.rideAddWayPointsFragment.showRoads(this.roads);

                break;
        }
    }

    public void setAdditionalWayPoints(List<GeoPoint> additionalWayPoints) {
        this.additionalWayPoints = additionalWayPoints;
    }
}
