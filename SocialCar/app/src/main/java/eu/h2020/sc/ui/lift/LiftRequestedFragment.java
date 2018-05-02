package eu.h2020.sc.ui.lift;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.lift.passenger.trip.PassengerTripDetailsActivity;
import eu.h2020.sc.utils.ActivityUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public class LiftRequestedFragment extends LiftFragment implements RecyclerViewItemClickListener, View.OnClickListener {

    @Override
    public void onResume() {
        super.onResume();
        this.executeLiftRequest();
    }

    @Override
    public void initListener() {
        this.liftAdapter.setRecyclerViewItemClickListener(this);
        this.btnNoLifts.setOnClickListener(this);
        this.btnNoDataRetry.setOnClickListener(this);
    }

    @Override
    public void onItemClickListener(View v, int position) {
        if (((GeneralActivity) getActivity()).checkClickTime()) return;
        Lift lift = this.liftAdapter.getItem(position);

        HashMap<String,Serializable> params = new HashMap();
        params.put(PassengerTripDetailsActivity.LIFT_KEY, lift);

        ActivityUtils.openActivityWithObjectParams(getActivity(), PassengerTripDetailsActivity.class, params);
    }


    @Override
    public void executeLiftRequest() {
        showLoader();

        RetrieveLiftTask retrieveLiftTask = new RetrieveLiftTask(this);
        retrieveLiftTask.execute(LiftType.REQUESTED);
    }

    @Override
    public void onClick(View view) {
        this.noNoDataLayout.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.btn_no_data_retry:
                this.executeLiftRequest();
                break;
            case R.id.btn_template_background:
                ActivityUtils.openActivity(getActivity(), HomeActivity.class);
                break;
        }
    }


    @Override
    public void showLayoutErrorParser() {
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLayoutLiftsList(List<Lift> lifts) {
        this.liftAdapter.refreshRecyclerView(lifts);
        this.recyclerLifts.setVisibility(RecyclerView.VISIBLE);
        this.noLiftsLayout.setVisibility(View.GONE);
        this.noNoDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLayoutEmptyLift() {
        this.textViewCustomNoLifts.setText(R.string.text_no_trips_found);
        this.imageViewNoLifts.setImageResource(R.mipmap.img_passenger_no_trips);
        this.btnNoLifts.setText(R.string.search_for_trips);
        this.noLiftsLayout.setVisibility(View.VISIBLE);
        this.noNoDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void showServerGenericError() {
        this.showLayoutGenericError();
    }

    @Override
    public void showNotFoundError() {
        this.showLayoutGenericError();
    }

    private void showLayoutGenericError() {
        this.recyclerLifts.setVisibility(RecyclerView.INVISIBLE);
        this.noLiftsLayout.setVisibility(View.GONE);
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }
}