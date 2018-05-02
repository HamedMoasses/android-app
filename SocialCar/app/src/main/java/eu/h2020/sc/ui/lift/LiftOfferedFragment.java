package eu.h2020.sc.ui.lift;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.home.driver.becomedriver.BecomeDriverActivity;
import eu.h2020.sc.ui.lift.passenger.lift.PassengerLiftDetailsActivity;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationMapActivity;
import eu.h2020.sc.utils.ActivityUtils;


public class LiftOfferedFragment extends LiftFragment implements RecyclerViewItemClickListener, View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();
        User loggedUser = SocialCarApplication.getInstance().getUser();

        if (loggedUser != null && loggedUser.isDriver()) {
            this.executeLiftRequest();
        } else {
            this.noLiftsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initListener() {
        this.liftAdapter.setRecyclerViewItemClickListener(this);
        this.btnNoLifts.setOnClickListener(this);
        this.btnNoDataRetry.setOnClickListener(this);
    }

    @Override
    public void showLayoutEmptyLift() {
        this.noLiftsLayout.setVisibility(View.VISIBLE);
        this.textViewCustomNoLifts.setText(R.string.text_no_trips_found);
        this.imageViewNoLifts.setImageResource(R.mipmap.img_driver_no_trips);
        this.btnNoLifts.setText(R.string.offer_trip);
    }

    @Override
    public void showLayoutLiftsList(List<Lift> lifts) {
        this.liftAdapter.refreshRecyclerView(lifts);
        this.recyclerLifts.setVisibility(RecyclerView.VISIBLE);
        this.noLiftsLayout.setVisibility(View.GONE);
    }

    @Override
    public void executeLiftRequest() {
        showLoader();
        RetrieveLiftTask retrieveLiftTask = new RetrieveLiftTask(this);
        retrieveLiftTask.execute(LiftType.OFFERED);
    }

    @Override
    public void onClick(View view) {
        this.noNoDataLayout.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.btn_no_data_retry:
                executeLiftRequest();
                break;
            case R.id.btn_template_background:
                String btnCaption = String.valueOf(btnNoLifts.getText()).trim();
                if (btnCaption.equalsIgnoreCase(getResources().getString(R.string.offer_trip).trim())) {
                    ActivityUtils.openActivity(getActivity(), RideCreationMapActivity.class);
                } else {
                    ActivityUtils.openActivity(getActivity(), BecomeDriverActivity.class);
                    this.noLiftsLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void showLayoutErrorParser() {
        showGenericError();
    }

    @Override
    public void showServerGenericError() {
        showGenericError();
    }

    @Override
    public void showNotFoundError() {
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClickListener(View v, int position) {
        if (((GeneralActivity) getActivity()).checkClickTime()) return;
        Lift lift = this.liftAdapter.getItem(position);

        HashMap<String, Serializable> params = new HashMap();
        params.put(PassengerLiftDetailsActivity.LIFT_KEY, lift);

        ActivityUtils.openActivityWithObjectParams(getActivity(), PassengerLiftDetailsActivity.class, params);
    }

    public void showGenericError() {
        this.noDataTextView.setText(getResources().getString(R.string.generic_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

}