package eu.h2020.sc.ui.trip.solution.details.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class TripSolutionDetailsAdapter extends RecyclerView.Adapter {

    private RecyclerViewItemClickListener onItemClickListener;
    private Trip trip;

    public TripSolutionDetailsAdapter(Trip trip) {
        this.trip = trip;
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_solution_details_row, parent, false);
        return new GenericStepViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AnimationHelper.runEnterAnimationRow(holder.itemView, position);
        if (holder instanceof GenericStepViewHolder){
            ((GenericStepViewHolder) holder).init(this.trip, getItem(position));
            ((GenericStepViewHolder) holder).setOnItemClickListener(onItemClickListener);
        }
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return trip.getSteps().size();
    }

    public Object getItem(int position) {
        return trip.getSteps().get(position);
    }
}
