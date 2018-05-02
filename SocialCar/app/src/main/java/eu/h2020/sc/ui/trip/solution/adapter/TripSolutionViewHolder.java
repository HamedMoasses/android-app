package eu.h2020.sc.ui.trip.solution.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.h2020.sc.R;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class TripSolutionViewHolder extends RecyclerView.ViewHolder {

    public TextView minuteTextView;
    public TextView timeStartStopTextView;
    public TextView priceTextView;
    public LinearLayout stepsLinearLayout;
    public FrameLayout frameLayoutTripSolution;
    public LinearLayout childLinearLayout;

    public TripSolutionViewHolder(View itemView) {
        super(itemView);
        minuteTextView = (TextView) itemView.findViewById(R.id.text_view_line_route_name);
        timeStartStopTextView = (TextView) itemView.findViewById(R.id.text_view_remaining_stops);
        priceTextView = (TextView) itemView.findViewById(R.id.text_view_waiting_time_minutes);
        stepsLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_steps);
        frameLayoutTripSolution = (FrameLayout) itemView.findViewById(R.id.frameLayoutRowRealtime);
        childLinearLayout = (LinearLayout) itemView.findViewById(R.id.childLinearLayout);
    }
}