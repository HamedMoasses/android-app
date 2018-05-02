package eu.h2020.sc.ui.ride.creation.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.Route;
import eu.h2020.sc.domain.TimeSpacePoint;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.ride.creation.TimePickerFragment;
import eu.h2020.sc.ui.ride.creation.fragment.DatePickerFragment;
import eu.h2020.sc.ui.ride.creation.task.RideCreationTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.Utils;

import java.util.Date;

public class RideCreationFormActivity extends GeneralActivity implements TextWatcher {

    public static final String KEY_START_POINT = "KEY_START_POINT";
    public static final String KEY_END_POINT = "KEY_END_POINT";
    public static final String KEY_POLYLINE = "KEY_POLYLINE";

    public static final String TRIP_DATE_FORMAT = "E d MMM yyyy";
    public static final String TRIP_DATE_TIME_24H_FORMAT = "E d MMM yyyy HH:mm";
    public static final String TRIP_DATE_TIME_12H_FORMAT = "E d MMM yyyy hh:mm a";

    private Point startPoint;
    private Point endPoint;
    private String polyline;
    private Car car;

    private TextView textViewTripDate;
    private TextView textViewTripTime;
    private TextView textViewTripName;

    private boolean use24HourFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_creation);

        this.use24HourFormat = DateFormat.is24HourFormat(this);

        initDataModel();
        initUI();

    }

    private void initDataModel() {
        Intent intent = getIntent();
        this.startPoint = (Point) intent.getSerializableExtra(KEY_START_POINT);
        this.endPoint = (Point) intent.getSerializableExtra(KEY_END_POINT);
        this.polyline = intent.getStringExtra(KEY_POLYLINE);
        this.car = SocialCarApplication.getInstance().getCar();
    }

    private void initUI() {
        initToolBar(false);
        initBack();

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle(getString(R.string.toolbar_label_new_trip));

        this.textViewTripName = (TextView) findViewById(R.id.ridecreation_edittext_trip);
        this.textViewTripName.addTextChangedListener(this);
        Date now = new Date();

        this.textViewTripDate = (TextView) findViewById(R.id.ridecreation_text_tripdate);
        textViewTripDate.setText(DateUtils.formatDateToString(now, TRIP_DATE_FORMAT));

        this.textViewTripTime = (TextView) findViewById(R.id.ridecreation_text_triptime);
        textViewTripTime.setText(DateUtils.formatToHoursAndMinutes(now, use24HourFormat));

        TextView textViewCarModel = (TextView) findViewById(R.id.ridecreation_text_carmodel);
        textViewCarModel.setText(this.car.getModel());

        TextView textViewCarPlate = (TextView) findViewById(R.id.ridecreation_text_carplate);
        textViewCarPlate.setText(this.car.getPlate());

        TextView textViewCarStartingAddress = (TextView) findViewById(R.id.ridecreation_text_departureaddress);
        textViewCarStartingAddress.setText(startPoint.getAddress());

        TextView textViewCarArrivingAddress = (TextView) findViewById(R.id.ridecreation_text_arrivingaddress);
        textViewCarArrivingAddress.setText(endPoint.getAddress());
    }

    public void makeRideCreationRequest(View view) {
        hideSoftKeyboard();

        RideCreationTask rideCreationTask = new RideCreationTask(this);

        String tripName = this.textViewTripName.getText().toString().trim();

        Date tripDatetime;
        if (use24HourFormat)
            tripDatetime = DateUtils.formatStringToDate(this.textViewTripDate.getText() + " " + this.textViewTripTime.getText(), TRIP_DATE_TIME_24H_FORMAT);
        else
            tripDatetime = DateUtils.formatStringToDate(this.textViewTripDate.getText() + " " + this.textViewTripTime.getText(), TRIP_DATE_TIME_12H_FORMAT);

        User driver = SocialCarApplication.getInstance().getUser();

        //TODO Currently the user has only one Car
        String carID = driver.getCarsID().get(0);

        TimeSpacePoint startTimeSpacePoint = new TimeSpacePoint(startPoint, tripDatetime);
        TimeSpacePoint endTimeSpacePoint = new TimeSpacePoint(endPoint, null);

        Ride ride = new Ride(tripName, new Route(startTimeSpacePoint, endTimeSpacePoint), polyline, carID, driver.getId());
        ride.setActivated(true);

        if (rideCreationTask.validateRide(ride)) {
            rideCreationTask.execute(ride);
        }
    }

    public void showTimePickerDialog(View v) {
        if (checkClickTime()) return;
        Utils.hideSoftKeyboard(v);
        (findViewById(R.id.ridecreation_edittext_trip)).clearFocus();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        if (checkClickTime()) return;
        Utils.hideSoftKeyboard(v);
        (findViewById(R.id.ridecreation_edittext_trip)).clearFocus();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (this.textViewTripName.isFocused()) {
            this.textViewTripName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void showTripNameMarkerError() {
        this.textViewTripName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null);
    }

    public void hideTripNameMarkerError() {
        this.textViewTripName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void showTripDateTimeMarkerError() {
        this.textViewTripTime.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_error_24dp), null, null, null);
    }

    public void hideTripDateTimeMarkerError() {
        this.textViewTripTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void goToHome() {
        ActivityUtils.openActivityWithParam(this, HomeActivity.class, HomeActivity.HOME_ACTIVITY_INDEX_SELECTED_TAB, 1);
    }

}
