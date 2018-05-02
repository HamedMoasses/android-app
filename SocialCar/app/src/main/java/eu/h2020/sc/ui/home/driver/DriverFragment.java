package eu.h2020.sc.ui.home.driver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.ui.commons.RecyclerViewItemCheckedListener;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.home.driver.becomedriver.BecomeDriverActivity;
import eu.h2020.sc.ui.home.driver.ride.task.DeleteRideTask;
import eu.h2020.sc.ui.home.driver.ride.task.RetrieveRideTask;
import eu.h2020.sc.ui.home.driver.ride.task.UpdateRideTask;
import eu.h2020.sc.ui.ride.myrides.MyRidesAdapter;
import eu.h2020.sc.ui.ride.myrides.RecyclerViewRideClickListener;
import eu.h2020.sc.ui.trip.ChooseTripTypeActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.WidgetHelper;


public class DriverFragment extends Fragment implements View.OnClickListener, RecyclerViewItemClickListener, RecyclerViewItemCheckedListener, RecyclerViewRideClickListener {

    private View fragmentView;
    private Button buttonDriver;
    private TextViewCustom textViewDescription;
    private ImageView imageViewDescription;
    private ProgressBar rideProgressBar;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recycleViewRides;
    private MyRidesAdapter ridesAdapter;
    private List<Ride> myRides;
    private View noTemplateLayout;
    private View genericErrorLayout;
    private Button btnNoDataRetry;
    private TextViewCustom genericErrorTextView;
    private int position;
    private Ride rideSelected;
    private Ride rideToUpdate;
    private List<Lift> rideLifts;
    private boolean updateInProgress = false;
    private Snackbar.Callback snackBarDeleteCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar snackbar, int event) {
            switch (event) {
                case Snackbar.Callback.DISMISS_EVENT_ACTION:
                    noTemplateLayout.setVisibility(View.GONE);
                    recycleViewRides.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    myRides.add(position, rideSelected);
                    ridesAdapter.addItem(rideSelected, position);
                    break;

                case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                    DeleteRideTask deleteRideTask = new DeleteRideTask(DriverFragment.this);
                    deleteRideTask.execute(rideSelected.getId());
                    break;
            }
        }
    };
    private MaterialDialog.SingleButtonCallback deleteCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            removeRide();
        }
    };
    private Snackbar.Callback snackBarUpdateCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar snackbar, int event) {
            switch (event) {
                case Snackbar.Callback.DISMISS_EVENT_ACTION:
                    updateInProgress = false;
                    invalidateUpdateRide();
                    break;
                case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                    UpdateRideTask updateRideTask = new UpdateRideTask(DriverFragment.this);
                    updateRideTask.execute(rideToUpdate);
                    break;
            }
        }
    };
    private MaterialDialog.SingleButtonCallback disabledCallback = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            DriverFragment.this.updateInProgress = true;

            rideLifts.clear();
            rideLifts.addAll(rideToUpdate.getLifts());

            rideToUpdate.setActivated(false);
            rideToUpdate.deleteAllLifts();

            myRides.set(position, rideToUpdate);
            ridesAdapter.updateItem(rideToUpdate, position);

            String textSnack = getString(R.string.ride_disabled);
            WidgetHelper.showSnackbar(getContext(), (CoordinatorLayout) fragmentView.findViewById(R.id.coordinator_layout_driver), textSnack, getString(R.string.undo_snackbar), DriverFragment.this, snackBarUpdateCallback);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.fragmentView = inflater.inflate(R.layout.fragment_driver, container, false);

        this.rideLifts = new ArrayList<>();

        this.initUI();
        this.initListener();
        this.initRecycleViewRides();
        return this.fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        RetrieveRideTask retrieveRideTask = new RetrieveRideTask(this);
        if (!retrieveRideTask.isUserDriver()) {
            showBecomeDriverLayout();
        } else {
            showLoader();
            retrieveRideTask.execute();
        }
    }

    private void initUI() {
        this.noTemplateLayout = this.fragmentView.findViewById(R.id.layout_template_driver_background);
        this.rideProgressBar = (ProgressBar) this.fragmentView.findViewById(R.id.progress_bar_ride);
        this.genericErrorLayout = this.fragmentView.findViewById(R.id.layout_generic_error_retry);
        this.btnNoDataRetry = (Button) this.genericErrorLayout.findViewById(R.id.btn_no_data_retry);
        this.genericErrorTextView = (TextViewCustom) this.genericErrorLayout.findViewById(R.id.no_data_text_view);

        this.buttonDriver = (Button) noTemplateLayout.findViewById(R.id.btn_template_background);
        this.textViewDescription = (TextViewCustom) noTemplateLayout.findViewById(R.id.text_view_template_background);
        this.imageViewDescription = (ImageView) noTemplateLayout.findViewById(R.id.image_view_template_background);
        this.recycleViewRides = (RecyclerView) this.fragmentView.findViewById(R.id.rv);

        this.floatingActionButton = (FloatingActionButton) this.fragmentView.findViewById(R.id.fab_add_ride);
    }

    private void initListener() {
        this.floatingActionButton.setOnClickListener(this);
        this.buttonDriver.setOnClickListener(this);
        this.btnNoDataRetry.setOnClickListener(this);
    }

    private void initRecycleViewRides() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        this.recycleViewRides.setHasFixedSize(true);
        this.recycleViewRides.setLayoutManager(llm);
        this.recycleViewRides.setItemAnimator(new DefaultItemAnimator());

        this.ridesAdapter = new MyRidesAdapter(new ArrayList<Ride>(), getContext(), getActivity());
        this.ridesAdapter.setDeleteItemClickListener(this);
        this.ridesAdapter.setRecycleViewItemCheckedListener(this);
        this.ridesAdapter.setViewRideClickListener(this);
        this.recycleViewRides.setAdapter(this.ridesAdapter);
    }

    @Override
    public void onClick(View v) {
        RetrieveRideTask retrieveRideTask = new RetrieveRideTask(this);
        switch (v.getId()) {
            case R.id.btn_template_background:
                if (!retrieveRideTask.isUserDriver()) {
                    ActivityUtils.openActivity(getActivity(), BecomeDriverActivity.class);
                } else {
                    ActivityUtils.openActivity(getActivity(), ChooseTripTypeActivity.class);
                }
                break;
            case R.id.fab_add_ride:
                ActivityUtils.openActivity(getActivity(), ChooseTripTypeActivity.class);
                break;
            case R.id.btn_no_data_retry:
                showLoader();
                retrieveRideTask.execute();
                break;
        }
    }

    @Override
    public void onItemClickListener(View v, int position) {
        int rideUpdatePosition = myRides.indexOf(rideToUpdate);
        if (!(updateInProgress && (rideUpdatePosition == position))) {
            this.position = position;
            this.rideSelected = this.myRides.get(position);

            if (this.rideSelected.isActivated()) {
                MaterialDialogHelper.createDialog(this.getContext(), R.string.delete_ride_dialog_title, R.string.ride_dialog_body_message, R.string.yes, R.string.no, this.deleteCallback).show();
            } else {
                this.removeRide();
            }
        }
    }

    @Override
    public void onItemCheckedListener(View v, int position, boolean isChecked) {

        if (position >= 0) {
            this.updateInProgress = true;
            this.myRides.get(position).setActivated(isChecked);
            this.rideToUpdate = this.myRides.get(position);

            String textSnack = getString(R.string.ride_disabled);
            if (isChecked)
                textSnack = getString(R.string.ride_activated);

            WidgetHelper.showSnackbar(getContext(), (CoordinatorLayout) this.fragmentView.findViewById(R.id.coordinator_layout_driver), textSnack, getString(R.string.undo_snackbar), this, snackBarUpdateCallback);
        }
    }

    @Override
    public void onItemClickListener(View v, int realPosition, int pos) {
        this.rideToUpdate = this.myRides.get(realPosition);
        this.position = realPosition;

        MaterialDialogHelper.createDialog(this.getContext(), R.string.update_ride_dialog_title, R.string.ride_dialog_body_message, R.string.yes, R.string.no, this.disabledCallback).show();
    }

    public void showLoader() {
        this.noTemplateLayout.setVisibility(View.GONE);
        this.rideProgressBar.setVisibility(View.VISIBLE);
        this.recycleViewRides.setVisibility(RecyclerView.INVISIBLE);
        this.floatingActionButton.setVisibility(View.GONE);
        this.genericErrorLayout.setVisibility(View.GONE);
    }

    public void dismissLoader() {
        this.rideProgressBar.setVisibility(View.GONE);
    }

    public void showLayoutGenericError() {
        this.recycleViewRides.setVisibility(RecyclerView.GONE);
        this.floatingActionButton.setVisibility(View.GONE);
        this.genericErrorTextView.setText(getResources().getString(R.string.generic_error));
        this.genericErrorLayout.setVisibility(View.VISIBLE);
    }

    public void showLayoutNoInternetConnection() {
        this.recycleViewRides.setVisibility(RecyclerView.GONE);
        this.floatingActionButton.setVisibility(View.GONE);
        this.genericErrorTextView.setText(getResources().getString(R.string.no_connection_error));
        this.genericErrorLayout.setVisibility(View.VISIBLE);
    }

    public void invalidateUpdateRide() {
        int index = myRides.indexOf(this.rideToUpdate);
        if (index != -1) {
            boolean newState = !this.rideToUpdate.isActivated();
            this.rideToUpdate.setActivated(newState);

            this.rideToUpdate.addAll(this.rideLifts);

            this.myRides.set(index, this.rideToUpdate);
            this.ridesAdapter.updateItem(this.rideToUpdate, index);
        }
    }

    public void showBecomeDriverLayout() {
        this.noTemplateLayout.setVisibility(View.VISIBLE);
        this.buttonDriver.setText(getString(R.string.become_a_driver));
        this.textViewDescription.setText(getString(R.string.new_driver_message));
        this.imageViewDescription.setImageResource(R.mipmap.img_new_driver);
        this.floatingActionButton.setVisibility(View.GONE);
    }

    public void showOfferRideLayout() {
        this.recycleViewRides.setVisibility(RecyclerView.GONE);
        this.noTemplateLayout.setVisibility(View.VISIBLE);
        this.buttonDriver.setText(getString(R.string.offer_trip));
        this.textViewDescription.setText(getString(R.string.text_no_trips_found));
        this.imageViewDescription.setImageResource(R.mipmap.img_driver_no_trips);
        this.floatingActionButton.setVisibility(View.GONE);
    }

    public void showRideLayout(List<Ride> rides) {
        this.recycleViewRides.setVisibility(RecyclerView.VISIBLE);
        this.noTemplateLayout.setVisibility(View.GONE);
        this.floatingActionButton.setVisibility(View.VISIBLE);
        this.genericErrorLayout.setVisibility(View.GONE);
        this.myRides = rides;
        this.ridesAdapter.refreshRecyclerView(rides);
    }

    public void setUpdateInProgress(boolean updateInProgress) {
        this.updateInProgress = updateInProgress;
    }

    public void showUnauthorizedError() {
        ((GeneralActivity) getActivity()).showUnauthorizedError();
    }

    public void goToSignInActivity() {
        ((GeneralActivity) getActivity()).goToSignInActivity();
    }

    public void showConnectionError() {
        ((GeneralActivity) getActivity()).showConnectionError();
    }

    public void showServerGenericError() {
        ((GeneralActivity) getActivity()).showServerGenericError();
    }

    public void undoDelete() {
        myRides.add(position, rideSelected);
        ridesAdapter.addItem(rideSelected, position);
    }

    private void removeRide() {
        this.myRides.remove(this.position);
        this.ridesAdapter.removeItem(this.position);

        if (this.myRides.size() == 0) {
            this.recycleViewRides.setVisibility(View.GONE);
            this.floatingActionButton.setVisibility(View.GONE);

            this.noTemplateLayout.setVisibility(View.VISIBLE);
            this.buttonDriver.setText(getString(R.string.offer_trip));
            this.textViewDescription.setText(getString(R.string.text_no_trips_found));
            this.imageViewDescription.setImageResource(R.mipmap.img_driver_no_trips);
        }

        WidgetHelper.showSnackbar(getContext(), (CoordinatorLayout) this.fragmentView.findViewById(R.id.coordinator_layout_driver), getString(R.string.trip_deleted), getString(R.string.undo_snackbar), this, snackBarDeleteCallback);
    }

    public void cleanSupportRideLifts() {
        if (this.rideLifts != null)
            this.rideLifts.clear();
    }

    public void cleanSupportRideToUpdate() {
        this.rideToUpdate = null;
    }

}