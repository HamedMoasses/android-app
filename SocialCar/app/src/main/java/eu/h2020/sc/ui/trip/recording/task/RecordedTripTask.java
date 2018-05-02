package eu.h2020.sc.ui.trip.recording.task;

import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.ui.trip.recording.TripRecordingActivity;

/**
 * Created by pietro on 13/03/2018.
 */
public class RecordedTripTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = AddressPositionTask.class.getSimpleName();

    private static final int MAX_WAYPOINTS_NUMBER = 50;

    private static final int COMPLETED = 0;
    private static final int ERROR = 1;
    private static final int RECORDED_TRIP_TOO_SHORT = 3;

    private TripRecordingActivity activity;
    private ArrayList<GeoPoint> recordedTrip;
    private Road road;

    public RecordedTripTask(TripRecordingActivity activity, ArrayList<GeoPoint> recordedTrip) {
        this.activity = activity;
        this.recordedTrip = recordedTrip;
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        if (this.recordedTrip.size() < 2) {
            Log.e(TAG, "The recorded trip is too short.");
            return RECORDED_TRIP_TOO_SHORT;
        }

        RoadManager roadManager = new MapQuestRoadManager(this.activity.getString(R.string.mapquest_api_key));
        List<Road> roads = Arrays.asList(roadManager.getRoads(this.recordedTrip));

        if (roads.get(0).mStatus != Road.STATUS_OK) {
            //WorkAround to reduce the waypoint
            this.reduceWayPoint();

            roads = Arrays.asList(roadManager.getRoads(this.recordedTrip));

            if (roads.get(0).mStatus != Road.STATUS_OK) {
                Log.e(TAG, "Road Status is " + roads.get(0).mStatus);
                return ERROR;
            }
        }

        this.road = roads.get(0);

        return COMPLETED;
    }


    private void reduceWayPoint() {
        for (int i = 3; i < this.recordedTrip.size(); i += 3) {
            if (i != (this.recordedTrip.size() - 1)) {
                this.recordedTrip.remove(i);
            }
        }

        if (this.recordedTrip.size() > MAX_WAYPOINTS_NUMBER) {
            reduceWayPoint();
        }
    }

    @Override
    protected void onPostExecute(final Integer resultCode) {
        this.activity.dismissDialog();

        switch (resultCode) {
            case RECORDED_TRIP_TOO_SHORT:
                this.activity.showErrorRecordedTripTooShort();
                break;
            case COMPLETED:
                this.activity.deleteStoredTrip();
                this.activity.goToTripRecordedMapActivity(this.road);
                break;
            case ERROR:
                this.activity.showServerGenericError();
                break;
        }
    }
}