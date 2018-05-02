package eu.h2020.sc.ui.trip;

import android.os.Bundle;
import android.view.View;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationMapActivity;
import eu.h2020.sc.ui.trip.recording.TripRecordingActivity;
import eu.h2020.sc.utils.ActivityUtils;

public class ChooseTripTypeActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trip_type);

        this.initUI();
    }

    private void initUI() {
        setTitle(getString(R.string.choose_trip_type));

        initToolBar(false);
        initBack();
    }


    public void goToTripRecordingActivity(View view) {
        ActivityUtils.openActivity(this, TripRecordingActivity.class);
    }

    public void goToRideCreationMapActivity(View view) {
        ActivityUtils.openActivity(this, RideCreationMapActivity.class);
    }

}
