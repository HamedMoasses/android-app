package eu.h2020.sc.ui.trip.solution.details;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.PrivateTransport;
import eu.h2020.sc.domain.Step;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.home.driver.DriverDetailsActivity;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.ui.trip.solution.details.adapter.TripSolutionDetailsAdapter;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;

public class TripSolutionDetailsActivity extends GeneralActivity implements RecyclerViewItemClickListener, View.OnClickListener {

    public static final String SOLUTION_DETAILS_KEY = TripSolutionDetailsActivity.class.getCanonicalName();
    private RecyclerView rvTripSolutionDetails;
    private Trip trip;
    private MaterialRippleLayout sendRequestRipple;
    private TextView textViewSendRequest;
    private boolean hasExternalDriverStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_solution_details);

        this.trip = (Trip) ActivityUtils.getSerializableFromIntent(this, SOLUTION_DETAILS_KEY);

        this.initUI();
    }

    private void initUI() {

        initToolBar(false);
        initBack();

        this.sendRequestRipple = (MaterialRippleLayout) findViewById(R.id.btn_send_request);
        this.textViewSendRequest = (TextView) findViewById(R.id.activity_trip_solution_details_tv_send);

        this.sendRequestRipple.setOnClickListener(this);

        TextView textViewTripPrice = (TextView) findViewById(R.id.text_view_total_cost);

        if (trip.getPrice().getAmount().equals(new BigDecimal(-1)) || trip.getPrice().getAmount().equals(new BigDecimal(0))) {
            textViewTripPrice.setText(String.format("%s", "N/A"));
        } else {
            textViewTripPrice.setText(String.format("%s%s", trip.getPrice().getCurrency().getSymbol(), trip.getPrice().getAmountTwoDecimals()));
        }

        TextView textViewTripDuration = (TextView) findViewById(R.id.text_view_total_duration);
        textViewTripDuration.setText(String.format("%s min", trip.getTripDurationInMinutes()));

        TextView textViewDepartureDate = (TextView) findViewById(R.id.textViewDepartureDate);
        textViewDepartureDate.setText(DateUtils.dateLongToStringCanonic(this.trip.getStartDate()));

        this.setDriverStepLayout();

        this.rvTripSolutionDetails = (RecyclerView) findViewById(R.id.rv_solution_details);
        this.initTripDetails();

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initTripDetails() {
        this.rvTripSolutionDetails.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.rvTripSolutionDetails.setLayoutManager(mLayoutManager);

        TripSolutionDetailsAdapter tripSolutionDetailsAdapter = new TripSolutionDetailsAdapter(this.trip);
        tripSolutionDetailsAdapter.setOnItemClickListener(this);
        this.rvTripSolutionDetails.setAdapter(tripSolutionDetailsAdapter);
    }


    private void setDriverStepLayout() {

        RelativeLayout detailsTripLayout = (RelativeLayout) findViewById(R.id.layout_details_trip);
        this.sendRequestRipple = (MaterialRippleLayout) findViewById(R.id.btn_send_request);

        if (!this.trip.hasDriverStep()) {

            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) detailsTripLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            detailsTripLayout.setLayoutParams(layoutParams);

            this.sendRequestRipple.setVisibility(View.GONE);
        } else {

            this.hasExternalDriverStep = this.trip.hasExternalDriverStep();

            if (hasExternalDriverStep) {
                this.textViewSendRequest.setText(getString(R.string.book_now));
                this.textViewSendRequest.setTextColor(ContextCompat.getColor(this, R.color.polyline_selected_body));
            } else {
                this.textViewSendRequest.setText(getString(R.string.send_request));
                this.textViewSendRequest.setTextColor(ContextCompat.getColor(this, R.color.green));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_solution_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (item.getItemId() == R.id.polylineTripSolution) {
            Log.i(getTagFromActivity(this), "GO TO MAP!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Step step = this.trip.getSteps().get(position);

        if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING)) {
            Car car = step.getPrivateTransport().getCar();
            User driver = step.getPrivateTransport().getDriver();

            HashMap<String, Serializable> params = new HashMap<>();
            params.put(DriverDetailsActivity.DRIVER_DETAILS_KEY, driver);
            params.put(DriverDetailsActivity.CAR_DETAILS_KEY, car);

            ActivityUtils.openActivityWithObjectParams(this, DriverDetailsActivity.class, params);
        }
    }


    @Override
    public void onClick(View v) {
        if (checkClickTime()) return;

        List<PrivateTransport> privateTransportList = this.trip.retrievePrivateTransportList();

        if (privateTransportList.size() > 0) {
            PrivateTransport privateTransport = privateTransportList.get(0);

            if (this.hasExternalDriverStep) {
                // TODO: temporary fix: due to multiple carpooler
                if (privateTransport.getPublicURI() == null || privateTransport.getPublicURI().isEmpty())
                    showServerGenericError();
                else {
                    // TODO prepare URL based on user's locale
                    String multilingualUrl;
                    if (privateTransport.getPublicURI().contains("/en/")) {
                        String defaultLanguage = Locale.getDefault().getLanguage();
                        if (defaultLanguage.contains("nl") || defaultLanguage.contains("fr")) {
                            multilingualUrl = privateTransport.getPublicURI().replace("/en/", String.format("/%s/", defaultLanguage));
                        } else {
                            multilingualUrl = privateTransport.getPublicURI();
                        }
                    } else {
                        multilingualUrl = privateTransport.getPublicURI();
                    }
                    ActivityUtils.openBrowserAtURL(multilingualUrl, this);
                }

            } else {
                showProgressDialog();
                //TODO: send request to all private transport. Is necessary modify UX? Rendezvous?
                TripSolutionDetailsTask tripSolutionDetailsTask = new TripSolutionDetailsTask(this, privateTransport.getRideID(), privateTransport.getCar().getId(), privateTransport.getDriver().getId());
                tripSolutionDetailsTask.execute(this.trip);
            }
        }
    }

    public void goToLiftActivity() {
        ActivityUtils.openActivityWithParam(this, LiftActivity.class, LiftActivity.TAG, 0);
    }
}