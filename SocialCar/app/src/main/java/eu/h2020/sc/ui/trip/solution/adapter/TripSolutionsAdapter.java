package eu.h2020.sc.ui.trip.solution.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Step;
import eu.h2020.sc.domain.Transport;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;
import eu.h2020.sc.utils.DateUtils;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class TripSolutionsAdapter extends RecyclerView.Adapter<TripSolutionViewHolder> {

    private static final String TAG = TripSolutionsAdapter.class.getCanonicalName();
    private ArrayList<Trip> trips;
    private ViewGroup viewGroup;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public TripSolutionsAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;
    }

    @Override
    public TripSolutionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.trip_solution_row, parent, false);
        TripSolutionViewHolder viewHolder = new TripSolutionViewHolder(contactView);

        this.viewGroup = parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TripSolutionViewHolder viewHolder, int position) {
        AnimationHelper.runEnterAnimationRow(viewHolder.itemView, position);

        Trip trip = this.getItem(position);

        viewHolder.frameLayoutTripSolution.setVisibility(View.VISIBLE);

        Log.i(TAG, trip.toString());

        viewHolder.minuteTextView.setText(String.format("%s min", trip.getTripDurationInMinutes()));
        viewHolder.timeStartStopTextView.setText(this.obtainTripInterval(trip));

        if (trip.getPrice().getAmount().equals(new BigDecimal(-1)) || trip.getPrice().getAmount().equals(new BigDecimal(0))) {
            viewHolder.priceTextView.setText("N/A");
        } else {
            viewHolder.priceTextView.setText(String.format("%s%s", trip.getPrice().getCurrency().getSymbol(), trip.getPrice().getAmountTwoDecimals()));
        }

        LinearLayout stepsLinearLayout = viewHolder.stepsLinearLayout;

        stepsLinearLayout.removeAllViews();
        stepsLinearLayout.invalidate();

        this.initSteps(trip, stepsLinearLayout);

        viewHolder.childLinearLayout.setTag(trip);
        viewHolder.stepsLinearLayout.setTag(trip);

        viewHolder.stepsLinearLayout.setOnClickListener(detailTrip);
        viewHolder.childLinearLayout.setOnClickListener(detailTrip);

    }

    private void initSteps(Trip trip, LinearLayout stepsLinearLayout) {

        LayoutInflater layoutInflater = LayoutInflater.from(this.viewGroup.getContext());

        int i = 0;
        for (Step step : trip.getSteps()) {

            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.trip_step_element, this.viewGroup, false);

            if (i == trip.getSteps().size() - 1) {
                ImageView arrowNextStepImageView = (ImageView) linearLayout.findViewById(R.id.image_view_arrow_next_step);
                arrowNextStepImageView.setVisibility(View.GONE);
            }

            Transport transport = step.getTransport();

            ImageView typeTransportStepImageView = (ImageView) linearLayout.findViewById(R.id.image_view_type_transport_step);
            TextView stepProviderTextView = (TextView) linearLayout.findViewById(R.id.text_view_step_provider);

            if (transport.getTravelMode() == TravelMode.BUS) {
                typeTransportStepImageView.setImageResource(R.drawable.ic_directions_bus_black_24dp);
                stepProviderTextView.setText(step.getPublicTransport().getShortName());

            } else if (transport.getTravelMode() == TravelMode.METRO) {
                typeTransportStepImageView.setImageResource(R.drawable.ic_directions_subway_black_24dp);
                stepProviderTextView.setText(step.getPublicTransport().getShortName());

            } else if (transport.getTravelMode() == TravelMode.TRAM) {
                typeTransportStepImageView.setImageResource(R.drawable.ic_directions_tram_24dp);
                stepProviderTextView.setText(step.getPublicTransport().getShortName());

            } else if (transport.getTravelMode() == TravelMode.RAIL) {
                typeTransportStepImageView.setImageResource(R.drawable.ic_directions_railway_black_24dp);
                stepProviderTextView.setText(step.getPublicTransport().getShortName());

            } else if (transport.getTravelMode() == TravelMode.FEET) {
                stepProviderTextView.setText(String.format("%s", step.getDistance()));
                typeTransportStepImageView.setImageResource(R.drawable.ic_directions_walk_24dp);

            } else if (transport.getTravelMode() == TravelMode.CAR_POOLING) {

                stepProviderTextView.setText(String.format("%s %s", this.viewGroup.getContext().getString(R.string.driver), step.getPrivateTransport().getDriver().getRating()));
                stepProviderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_bordered_20dp, 0);

                String publicURI = step.getPrivateTransport().getPublicURI();

                if (publicURI == null || publicURI.trim().isEmpty()) {
                    typeTransportStepImageView.setColorFilter(ContextCompat.getColor(this.viewGroup.getContext(), R.color.primary_color));
                }

            }
            stepsLinearLayout.addView(linearLayout);
            i++;

        }
    }

    public Trip getItem(int position) {
        return trips.get(position);
    }

    public void refreshRecyclerView(List<Trip> tripSolutions) {

        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.trips.clear();
        this.trips.addAll(tripSolutions);
        this.notifyDataSetChanged();
    }


    private View.OnClickListener detailTrip = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Trip item = (Trip) v.getTag();
            int position = trips.indexOf(item);
            recyclerViewItemClickListener.onItemClickListener(v, position);
        }
    };

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListenerMenu) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListenerMenu;
    }

    private String obtainTripInterval(Trip trip) {

        String startDateString = DateUtils.hourLongToString(trip.getStartDate(), DateFormat.is24HourFormat(SocialCarApplication.getContext()));
        String endDateString = DateUtils.hourLongToString(trip.getEndDate(), DateFormat.is24HourFormat(SocialCarApplication.getContext()));

        return String.format("%s - %s", startDateString, endDateString);
    }

    @Override
    public int getItemCount() {
        return this.trips.size();
    }
}
