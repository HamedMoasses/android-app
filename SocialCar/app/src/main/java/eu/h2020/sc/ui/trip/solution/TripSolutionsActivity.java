package eu.h2020.sc.ui.trip.solution;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Price;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.protocol.TripSolutionsRequest;
import eu.h2020.sc.protocol.gson.adapter.DateAdapter;
import eu.h2020.sc.protocol.gson.adapter.PriceAdapter;
import eu.h2020.sc.ui.commons.BaseHttpListener;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.trip.solution.adapter.TripSolutionsAdapter;
import eu.h2020.sc.ui.trip.solution.details.TripSolutionDetailsActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.Utils;
import eu.h2020.sc.utils.WidgetHelper;

public class TripSolutionsActivity extends GeneralActivity implements RecyclerViewItemClickListener, View.OnClickListener {

    public static final String TRIP_DATE_TIME_24H_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String TRIP_DATE_TIME_12H_FORMAT = "dd/MM/yyyy hh:mm a";
    public static final String TRIP_DATE_FORMAT = "dd/MM/yyyy";

    private BaseHttpListener baseHttpListener;
    private TripSolutionsAdapter tripSolutionsAdapter;
    private TextView textViewStartAddress;
    private TextView noDataTextView;
    private TextView textViewDestination;
    private ProgressBar listSolutionsProgressBar;
    private RecyclerView tripSolutionsRecyclerView;
    private ImageView imageViewInvertAddresses;

    private SocialCarStore socialCarStore;

    private Menu menu;
    private List<Trip> trips;
    private TripSolutionsRequest tripSolutionsRequest;

    private View noNoDataLayout;
    private Button btnNoDataRetry;

    private NumberPicker pickerHour;
    private NumberPicker pickerMinutes;
    private NumberPicker pickerAmPm;
    private TextViewCustom textViewEndDate;
    private TextViewCustom textViewStartDate;
    private Button btnSetStartDate;
    private Button btnSetEndDate;
    private RelativeLayout layoutTripDateTimeSelection;
    private Date selectedStartDateTime;
    private Date selectedEndDateTime;

    private TextView tripDateTimeLabel;
    private TextView tripDatePickerLabel;

    private MaterialDialog materialDialogSearchOption;

    private CheckBox checkBoxBus;
    private CheckBox checkBoxCarpooling;
    private CheckBox checkBoxSubway;
    private CheckBox checkBoxTrain;
    private CheckBox checkBoxTram;

    private RadioButton tripOptionsSortByCheapest;
    private RadioButton tripOptionsSortByFastest;

    boolean useTime24HourFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_solutions);

        this.socialCarStore = SocialCarApplication.getInstance();

        this.useTime24HourFormat = DateFormat.is24HourFormat(this);

        this.initUI();
        this.initEvents();
        this.initSolutionsAdapterComponents();

        // TODO handle exception cases: if 'tripSolutionsRequest' is null

        if (getIntent().getParcelableExtra(TripSolutionsRequest.TRIP_REQUEST_KEY) != null) {
            this.tripSolutionsRequest = getIntent().getParcelableExtra(TripSolutionsRequest.TRIP_REQUEST_KEY);

            findTripSolutions();
        } else {
            this.tripSolutionsRequest = TripSolutionsRequest.fromJson(SocialCarApplication.getSharedPreferences().getString(getString(R.string.trip_solution_current_tripRequest), null));
            this.showSolutions(loadFoundTrips());
        }

        showFormattedTripDateTime();

    }

    private void initUI() {

        initToolBar(false);
        initBack();

        setTitle(getString(R.string.solutions_toolbar_label));

        this.baseHttpListener = new BaseHttpListener(this);
        this.textViewStartAddress = (TextView) findViewById(R.id.lift_details_text_view_from);
        this.textViewDestination = (TextView) findViewById(R.id.lift_details_text_view_to);
        this.listSolutionsProgressBar = (ProgressBar) findViewById(R.id.progress_bar_list_solutions);
        this.tripSolutionsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_solutions);
        this.imageViewInvertAddresses = (ImageView) findViewById(R.id.image_view_invert);

        this.noNoDataLayout = this.findViewById(R.id.layout_trip_solutions_error_retry);
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextView) this.noNoDataLayout.findViewById(R.id.no_data_text_view);

        initTripTimeSelectionComponents();
        initTripSearchOptionsComponents();
    }

    private void initTripSearchOptionsComponents() {
        this.materialDialogSearchOption = MaterialDialogHelper.createCustomViewDialog(this, R.string.solutions_filters_label, R.layout.custom_layout_trip_search_options);

        View viewCustomDialog = this.materialDialogSearchOption.getCustomView();

        this.checkBoxBus = (CheckBox) viewCustomDialog.findViewById(R.id.trip_options_checkbox_bus);
        this.checkBoxCarpooling = (CheckBox) viewCustomDialog.findViewById(R.id.trip_options_checkbox_carpooling);
        this.checkBoxSubway = (CheckBox) viewCustomDialog.findViewById(R.id.trip_options_checkbox_subway);
        this.checkBoxTrain = (CheckBox) viewCustomDialog.findViewById(R.id.trip_options_checkbox_train);
        this.checkBoxTram = (CheckBox) viewCustomDialog.findViewById(R.id.trip_options_checkbox_tram);

        this.tripOptionsSortByCheapest = (RadioButton) viewCustomDialog.findViewById(R.id.trip_options_sort_by_cheapest);
        this.tripOptionsSortByFastest = (RadioButton) viewCustomDialog.findViewById(R.id.trip_options_sort_by_fastest);
    }

    private void initTripTimeSelectionComponents() {

        this.tripDateTimeLabel = (TextView) findViewById(R.id.trip_date_time_label);
        this.tripDatePickerLabel = (TextView) findViewById(R.id.trip_solution_date_picker_label);
        this.pickerHour = (NumberPicker) findViewById(R.id.hourPicker);
        this.pickerMinutes = (NumberPicker) findViewById(R.id.minutesPicker);
        this.textViewEndDate = (TextViewCustom) findViewById(R.id.textview_tab_trip_end_date);
        this.textViewStartDate = (TextViewCustom) findViewById(R.id.textview_tab_trip_start_date);
        this.btnSetStartDate = (Button) findViewById(R.id.btn_set_trip_start_date);
        this.btnSetEndDate = (Button) findViewById(R.id.btn_set_trip_end_date);
        this.layoutTripDateTimeSelection = (RelativeLayout) findViewById(R.id.layout_trip_date_time_selection);

        this.pickerHour.setMinValue(1);
        if (this.useTime24HourFormat)
            this.pickerHour.setMaxValue(23);
        else
            this.pickerHour.setMaxValue(12);

        this.pickerMinutes.setMinValue(0);
        this.pickerMinutes.setMaxValue(59);

        this.pickerAmPm = (NumberPicker) findViewById(R.id.picker_am_pm);
        this.pickerAmPm.setMinValue(Calendar.AM);
        this.pickerAmPm.setMaxValue(Calendar.PM);
        this.pickerAmPm.setWrapSelectorWheel(true);
        this.pickerAmPm.setDisplayedValues(getResources().getStringArray(R.array.array_am_pm));
        if (this.useTime24HourFormat)
            this.pickerAmPm.setVisibility(View.GONE);
        else
            this.pickerAmPm.setVisibility(View.VISIBLE);

    }

    private void initEvents() {
        this.btnNoDataRetry.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_trip_solutions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;

        } else if (item.getItemId() == R.id.refresh) {
            this.listSolutionsProgressBar.setVisibility(View.VISIBLE);

            findTripSolutions();

            item.setEnabled(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findTripSolutions() {
        Log.i(getTagFromActivity(this), this.tripSolutionsRequest.toJson());

        this.noNoDataLayout.setVisibility(View.GONE);
        showLoader();

        this.textViewStartAddress.setText(this.tripSolutionsRequest.getStartPoint().getAddress());
        this.textViewDestination.setText(this.tripSolutionsRequest.getEndPoint().getAddress());

        Date startDate = this.tripSolutionsRequest.getTimeRange().getStartDate();
        Date endDate = this.tripSolutionsRequest.getTimeRange().getEndDate();

        if (startDate == null) {
            this.tripSolutionsRequest.getTimeRange().setStartDate(new Date());
        }

        if (endDate == null) {

            final Calendar calStartDate = Calendar.getInstance();
            calStartDate.setTime(startDate);

            final Calendar calNewEndDate = Calendar.getInstance();
            calNewEndDate.set(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH), calStartDate.get(Calendar.DATE) + 1, 00, 00, 00);

            this.tripSolutionsRequest.getTimeRange().setEndDate(calNewEndDate.getTime());
        }

        TripSolutionsSearchTask searchTask = new TripSolutionsSearchTask(this, this.tripSolutionsRequest);
        searchTask.execute();
    }


    private void initSolutionsAdapterComponents() {

        this.tripSolutionsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.tripSolutionsRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Trip> tripSolutions = new ArrayList<>();

        this.tripSolutionsAdapter = new TripSolutionsAdapter(tripSolutions);
        this.tripSolutionsAdapter.setRecyclerViewItemClickListener(this);
        this.tripSolutionsRecyclerView.setAdapter(this.tripSolutionsAdapter);

    }

    public void updateTripsSolution(List<Trip> newTripSolutions) {
        this.tripSolutionsAdapter.refreshRecyclerView(newTripSolutions);
    }

    public void invertAddressRequest(View view) {

        TripSolutionsRequest tripRequestWithInvertedAddress = new TripSolutionsRequest(this.tripSolutionsRequest.getEndPoint(), this.tripSolutionsRequest.getStartPoint(), this.tripSolutionsRequest.getTravelModes(), this.tripSolutionsRequest.getTransferMode());
        tripRequestWithInvertedAddress.setTimeRange(this.tripSolutionsRequest.getTimeRange());

        this.tripSolutionsRequest = tripRequestWithInvertedAddress;
        this.textViewStartAddress.setText(tripRequestWithInvertedAddress.getStartPoint().getAddress());
        this.textViewDestination.setText(tripRequestWithInvertedAddress.getEndPoint().getAddress());

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        this.imageViewInvertAddresses.startAnimation(rotation);

        this.noNoDataLayout.setVisibility(View.GONE);
        this.imageViewInvertAddresses.setEnabled(false);

        findTripSolutions();
    }

    public void onTripSolutionsResult(String jsonResult) {
        Log.i(getTagFromActivity(this), jsonResult);

        if (this.menu != null)
            this.menu.getItem(0).setEnabled(true);

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray tripsJsonArray = jsonResponse.getJSONArray(Trip.TRIPS);

            ArrayList<Trip> tripSolutions = SocialCarApplication.getGson().fromJson(tripsJsonArray.toString(), new TypeToken<List<Trip>>() {
            }.getType());

            this.trips = this.obtainValidTrips(tripSolutions);
            this.saveCurrentTrips(this.trips);

            SocialCarApplication.getEditor().putString(getString(R.string.trip_solution_current_tripRequest), this.tripSolutionsRequest.toJson()).commit();

            dismissLoader();
            this.showSolutions(this.trips);

        } catch (JSONException e) {
            Log.e(getTagFromActivity(this), e.getMessage());
        }
    }

    private void showSolutions(List<Trip> tripSolutions) {
        if (this.tripSolutionsRequest != null) {
            this.textViewStartAddress.setText(this.tripSolutionsRequest.getStartPoint().getAddress());
            this.textViewDestination.setText(this.tripSolutionsRequest.getEndPoint().getAddress());

            if (tripSolutions.size() > 0)
                this.updateTripsSolution(tripSolutions);
            else {
                this.tripSolutionsRecyclerView.setVisibility(View.GONE);
                this.noDataTextView.setText(getResources().getString(R.string.no_solutions_found));
                this.noNoDataLayout.setVisibility(View.VISIBLE);
            }

            this.imageViewInvertAddresses.setEnabled(true);
        }
    }

    private void showFilteredList(List<Trip> filteredList) {
        if (filteredList.size() > 0) {
            this.noNoDataLayout.setVisibility(View.GONE);
            this.tripSolutionsRecyclerView.setVisibility(View.VISIBLE);
            this.updateTripsSolution(filteredList);
        } else {
            this.listSolutionsProgressBar.setVisibility(View.GONE);
            this.tripSolutionsRecyclerView.setVisibility(View.GONE);
            this.noDataTextView.setText(getString(R.string.empty_filtered_list));
            this.noNoDataLayout.setVisibility(View.VISIBLE);
            this.btnNoDataRetry.setVisibility(View.GONE);
        }

    }

    private void showLoader() {
        this.tripSolutionsRecyclerView.setVisibility(View.GONE);
        this.noNoDataLayout.setVisibility(View.GONE);
        this.listSolutionsProgressBar.setVisibility(View.VISIBLE);
    }

    private void dismissLoader() {
        this.listSolutionsProgressBar.setVisibility(View.GONE);
        this.tripSolutionsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showServerGenericError() {
        dismissLoader();
        this.tripSolutionsRecyclerView.setVisibility(View.GONE);
        this.noDataTextView.setText(getResources().getString(R.string.generic_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showConnectionError() {
        dismissLoader();

        //TODO Remove
        if (this.menu != null)
            this.menu.getItem(0).setEnabled(true);

        this.tripSolutionsRecyclerView.setVisibility(View.GONE);
        this.noDataTextView.setText(getResources().getString(R.string.no_connection_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUnauthorizedError() {
        dismissLoader();
        this.baseHttpListener.onUnauthorized();
    }

    public void showNotFoundError() {
        dismissLoader();
        this.baseHttpListener.onNotFound();
    }

    @Override
    public void onItemClickListener(View v, int position) {
        HashMap<String, Serializable> params = new HashMap<>();
        params.put(TripSolutionDetailsActivity.SOLUTION_DETAILS_KEY, this.tripSolutionsAdapter.getItem(position));

        ActivityUtils.openActivityWithObjectParams(this, TripSolutionDetailsActivity.class, params);
    }

    @Override
    public void onClick(View view) {
        noNoDataLayout.setVisibility(View.GONE);

        findTripSolutions();
    }

    private void saveCurrentTrips(List<Trip> trips) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Price.class, new PriceAdapter());
        gsonBuilder.registerTypeAdapter(Date.class, new DateAdapter());

        this.socialCarStore.store(gsonBuilder.create().toJson(trips));
    }

    private List<Trip> obtainValidTrips(ArrayList<Trip> tripSolutions) {

        List<Trip> validatedTrips = new ArrayList<>();

        for (Trip trip : tripSolutions) {
            if (!trip.areStepsEmpty()) {
                validatedTrips.add(trip);
            } else
                Log.e(getTagFromActivity(this), "Unable to performs operation on trip...steps are empty!!");
        }

        return validatedTrips;
    }

    private List<Trip> loadFoundTrips() {

        try {
            String json = this.socialCarStore.retrieveTripSolutions();

            if (json == null) {
                Log.e(getTagFromActivity(this), "Unable to get trip solutions json from shared!");
                return new ArrayList<>();
            }

            JSONArray jsonTrips = new JSONArray(json);
            this.trips = SocialCarApplication.getGson().fromJson(jsonTrips.toString(), new TypeToken<List<Trip>>() {
            }.getType());

            return this.trips;

        } catch (JSONException e) {
            Log.e(getTagFromActivity(this), e.getMessage());
            this.noDataTextView.setText(getResources().getString(R.string.generic_error));
            this.noNoDataLayout.setVisibility(View.VISIBLE);

            return new ArrayList<>();
        }
    }

    public void prepareTabDepartureAt(View view) {
        textViewStartDate.setBackgroundResource(R.drawable.selected_bottom_border);
        textViewStartDate.setTextColor(ContextCompat.getColor(this, R.color.primary_color));

        textViewEndDate.setBackgroundResource(R.drawable.bottom_border);
        textViewEndDate.setTextColor(ContextCompat.getColor(this, R.color.secondary_text_color));

        btnSetEndDate.setVisibility(View.GONE);
        btnSetStartDate.setVisibility(View.VISIBLE);
    }

    public void prepareTabArriveBy(View view) {
        textViewEndDate.setBackgroundResource(R.drawable.selected_bottom_border);
        textViewEndDate.setTextColor(ContextCompat.getColor(this, R.color.primary_color));

        textViewStartDate.setBackgroundResource(R.drawable.bottom_border);
        textViewStartDate.setTextColor(ContextCompat.getColor(this, R.color.secondary_text_color));

        btnSetEndDate.setVisibility(View.VISIBLE);
        btnSetStartDate.setVisibility(View.GONE);
    }

    public void setTripStartDate(View view) {

        if (this.useTime24HourFormat)
            this.selectedStartDateTime = DateUtils.formatStringToDate(getSelectedTripDateTime(), TRIP_DATE_TIME_24H_FORMAT);
        else
            this.selectedStartDateTime = DateUtils.formatStringToDate(getSelectedTripDateTime(), TRIP_DATE_TIME_12H_FORMAT);

        if (isDateTimeBeforeNow(this.selectedStartDateTime)) {

            this.layoutTripDateTimeSelection.setVisibility(View.GONE);
            this.selectedEndDateTime = null;
            this.tripSolutionsRequest.getTimeRange().setStartDate(this.selectedStartDateTime);

            final Calendar calStartDate = Calendar.getInstance();
            calStartDate.setTime(this.selectedStartDateTime);
            final Calendar calNewEndDate = Calendar.getInstance();
            calNewEndDate.set(calStartDate.get(Calendar.YEAR), calStartDate.get(Calendar.MONTH), calStartDate.get(Calendar.DATE) + 1, 00, 00, 00);

            this.tripSolutionsRequest.getTimeRange().setEndDate(calNewEndDate.getTime());

            showFormattedTripDateTime();
            findTripSolutions();
        } else {
            WidgetHelper.showToast(this, R.string.invalid_date_time);
        }

    }

    public void setTripSearchOptions(View view) {

        List<TravelMode> modes = new ArrayList<>();
        if (this.checkBoxBus.isChecked())
            modes.add(TravelMode.BUS);
        if (this.checkBoxCarpooling.isChecked())
            modes.add(TravelMode.CAR_POOLING);
        if (this.checkBoxSubway.isChecked())
            modes.add(TravelMode.METRO);
        if (this.checkBoxTrain.isChecked())
            modes.add(TravelMode.RAIL);
        if (this.checkBoxTram.isChecked())
            modes.add(TravelMode.TRAM);


        if (this.trips != null && this.trips.size() > 0) {
            List<Trip> filteredList = this.trips;

            if (!modes.isEmpty())
                filteredList = Trip.filterByTravelModes(filteredList, modes);

            if (tripOptionsSortByCheapest.isChecked())
                filteredList = Trip.orderByCheapestRoute(filteredList);
            else if (tripOptionsSortByFastest.isChecked())
                filteredList = Trip.orderByFasterRoute(filteredList);

            this.showFilteredList(filteredList);
        }
        this.materialDialogSearchOption.dismiss();
    }

    public void setTripEndDate(View view) {

        if (this.useTime24HourFormat)
            this.selectedEndDateTime = DateUtils.formatStringToDate(getSelectedTripDateTime(), TRIP_DATE_TIME_24H_FORMAT);
        else
            this.selectedEndDateTime = DateUtils.formatStringToDate(getSelectedTripDateTime(), TRIP_DATE_TIME_12H_FORMAT);

        if (isDateTimeBeforeNow(this.selectedEndDateTime)) {

            this.layoutTripDateTimeSelection.setVisibility(View.GONE);
            this.selectedStartDateTime = null;
            this.tripSolutionsRequest.getTimeRange().setEndDate(this.selectedEndDateTime);

            final Calendar calNewSartDate = Calendar.getInstance();
            this.tripSolutionsRequest.getTimeRange().setStartDate(calNewSartDate.getTime());

            showFormattedTripDateTime();
            findTripSolutions();
        } else {
            WidgetHelper.showToast(this, (R.string.invalid_date_time));
        }

    }

    public void showLayoutTripTimeSelection(View view) {
        this.layoutTripDateTimeSelection.setVisibility(View.VISIBLE);
    }

    public void showLayoutTripSearchOptions(View view) {
        this.materialDialogSearchOption.show();
    }

    public void hideLayoutTripTimeSelection(View view) {
        this.layoutTripDateTimeSelection.setVisibility(View.GONE);
    }

    public void hideLayoutTripSearchOptions(View view) {
        this.materialDialogSearchOption.dismiss();
    }

    public void showDatePickerDialog(View v) {
        if (checkClickTime()) return;
        Utils.hideSoftKeyboard(v);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showFormattedTripDateTime() {
        if (this.tripSolutionsRequest != null) {

            Date startDate = this.tripSolutionsRequest.getTimeRange().getStartDate();

            if (this.selectedStartDateTime != null) {

                final Calendar c = Calendar.getInstance();
                c.setTime(this.selectedStartDateTime);
                if (this.useTime24HourFormat)
                    this.tripDateTimeLabel.setText(getString(R.string.depart_at) + " " + DateUtils.formatDateToString(this.selectedStartDateTime, TRIP_DATE_TIME_24H_FORMAT));
                else {
                    this.tripDateTimeLabel.setText(getString(R.string.depart_at) + " " + DateUtils.formatDateToString(this.selectedStartDateTime, TRIP_DATE_TIME_12H_FORMAT));
                    int amPm = c.get(Calendar.AM_PM);
                    this.pickerAmPm.setValue(amPm);
                }

                this.tripDatePickerLabel.setText(DateUtils.formatDateToString(this.selectedStartDateTime, TRIP_DATE_FORMAT));
                this.pickerHour.setValue(Integer.parseInt(DateUtils.formatCurrentHour(c.getTime(), this.useTime24HourFormat)));
                this.pickerMinutes.setValue(c.get(Calendar.MINUTE));

            } else if (this.selectedEndDateTime != null) {

                final Calendar c = Calendar.getInstance();
                c.setTime(this.selectedEndDateTime);
                if (this.useTime24HourFormat)
                    this.tripDateTimeLabel.setText(getString(R.string.arrive_by) + " " + DateUtils.formatDateToString(this.selectedEndDateTime, TRIP_DATE_TIME_24H_FORMAT));
                else {
                    this.tripDateTimeLabel.setText(getString(R.string.arrive_by) + " " + DateUtils.formatDateToString(this.selectedEndDateTime, TRIP_DATE_TIME_12H_FORMAT));
                    int amPm = c.get(Calendar.AM_PM);
                    this.pickerAmPm.setValue(amPm);
                }

                this.tripDatePickerLabel.setText(DateUtils.formatDateToString(this.selectedEndDateTime, TRIP_DATE_FORMAT));
                pickerHour.setValue(Integer.parseInt(DateUtils.formatCurrentHour(c.getTime(), this.useTime24HourFormat)));
                pickerMinutes.setValue(c.get(Calendar.MINUTE));

            } else {
                final Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                if (this.useTime24HourFormat)
                    this.tripDateTimeLabel.setText(getString(R.string.depart_at) + " " + DateUtils.formatDateToString(startDate, TRIP_DATE_TIME_24H_FORMAT));
                else {
                    this.tripDateTimeLabel.setText(getString(R.string.depart_at) + " " + DateUtils.formatDateToString(startDate, TRIP_DATE_TIME_12H_FORMAT));
                    int amPm = c.get(Calendar.AM_PM);
                    this.pickerAmPm.setValue(amPm);
                }

                this.tripDatePickerLabel.setText(DateUtils.formatDateToString(startDate, TRIP_DATE_FORMAT));
                this.pickerHour.setValue(Integer.parseInt(DateUtils.formatCurrentHour(c.getTime(), this.useTime24HourFormat)));
                this.pickerMinutes.setValue(c.get(Calendar.MINUTE));

            }
        }
    }

    private String getSelectedTripDateTime() {
        String hoursAndMinutes = DateUtils.formatToHoursAndMinutes(getSelectedTime(), this.useTime24HourFormat);
        String tripDate = (String) ((TextView) findViewById(R.id.trip_solution_date_picker_label)).getText();
        return (tripDate + " " + hoursAndMinutes);
    }

    private Date getSelectedTime() {
        final Calendar c = Calendar.getInstance();
        if (this.useTime24HourFormat) {
            c.set(Calendar.HOUR_OF_DAY, this.pickerHour.getValue());
            c.set(Calendar.MINUTE, this.pickerMinutes.getValue());
        } else {
            c.set(Calendar.AM_PM, this.pickerAmPm.getValue());
            c.set(Calendar.HOUR, this.pickerHour.getValue());
            c.set(Calendar.MINUTE, this.pickerMinutes.getValue());
        }
        return c.getTime();
    }


    private boolean isDateTimeBeforeNow(Date selectedDateTime) {

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date currentDate = c.getTime();

        return selectedDateTime.equals(currentDate) || !selectedDateTime.before(currentDate);
    }
}