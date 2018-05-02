package eu.h2020.sc.ui.lift;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.ui.lift.adapter.LiftAdapter;
import eu.h2020.sc.utils.TextViewCustom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pietro on 28/09/16.
 */

public abstract class LiftFragment extends Fragment {

    public View fragmentView;
    public RecyclerView recyclerLifts;
    public LiftAdapter liftAdapter;
    public ProgressBar progressBar;
    public View noLiftsLayout;
    public View noNoDataLayout;
    public TextView noDataTextView;

    public TextViewCustom textViewCustomNoLifts;
    public ImageView imageViewNoLifts;

    public Button btnNoDataRetry;
    public Button btnNoLifts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.fragmentView = inflater.inflate(R.layout.fragment_lift, container, false);

        this.progressBar = (ProgressBar) this.fragmentView.findViewById(R.id.progress_bar_lift);
        this.recyclerLifts = (RecyclerView) this.fragmentView.findViewById(R.id.recycler_view_lift);
        this.noLiftsLayout = this.fragmentView.findViewById(R.id.layout_no_lifts);
        this.noNoDataLayout = this.fragmentView.findViewById(R.id.layout_generic_error_retry);
        this.btnNoLifts = (Button) this.noLiftsLayout.findViewById(R.id.btn_template_background);
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextViewCustom) this.noNoDataLayout.findViewById(R.id.no_data_text_view);
        this.textViewCustomNoLifts = (TextViewCustom) this.noLiftsLayout.findViewById(R.id.text_view_template_background);
        this.imageViewNoLifts = (ImageView) this.noLiftsLayout.findViewById(R.id.image_view_template_background);

        this.recyclerLifts.setHasFixedSize(true);

        this.liftAdapter = new LiftAdapter(new ArrayList<Lift>());
        this.recyclerLifts.setAdapter(this.liftAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        this.recyclerLifts.setLayoutManager(mLayoutManager);

        this.initListener();

        return this.fragmentView;
    }

    public void showLoader() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void dismissLoader() {
        this.progressBar.setVisibility(View.GONE);
    }

    public void showConnectionError() {
        this.noDataTextView.setText(R.string.no_connection_error);
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    public void showUnauthorizedError() {
        ((GeneralActivity) getActivity()).showUnauthorizedError();
    }

    public void goToSignInActivity() {
        ((GeneralActivity) getActivity()).goToSignInActivity();
    }

    public abstract void initListener();

    public abstract void executeLiftRequest();

    public abstract void showLayoutLiftsList(List<Lift> lifts);

    public abstract void showLayoutEmptyLift();

    public abstract void showLayoutErrorParser();

    public abstract void showServerGenericError();

    public abstract void showNotFoundError();

}
