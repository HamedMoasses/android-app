package eu.h2020.sc.ui.ride.creation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.WayPoint;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;

public class WayPointAdapter extends RecyclerView.Adapter<WayPointViewHolder> implements View.OnClickListener {

    private List<WayPoint> wayPoints;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public WayPointAdapter() {
        this.wayPoints = new ArrayList<>();
    }

    @Override
    public WayPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewWayPointRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_way_point, parent, false);
        return new WayPointViewHolder(viewWayPointRow);
    }

    @Override
    public void onBindViewHolder(WayPointViewHolder wayPointViewHolder, int position) {

        AnimationHelper.runEnterAnimationRow(wayPointViewHolder.itemView, position);

        position = wayPointViewHolder.getAdapterPosition();

        WayPoint wayPoint = this.getItem(position);

        wayPointViewHolder.textViewWayPointAddress.setText(wayPoint.getAddress());
        wayPointViewHolder.textViewWayPointAddress.setTag(wayPoint);

        wayPointViewHolder.imageViewCancel.setTag(wayPoint);
        wayPointViewHolder.imageViewCancel.setOnClickListener(this);
    }

    public void addItem(WayPoint item, int position) {
        this.wayPoints.add(position, item);
        this.notifyItemInserted(position);
    }

    public void removeItem(int pos) {
        this.wayPoints.remove(pos);
        this.notifyItemRemoved(pos);
    }

    public void refreshRecyclerView(List<WayPoint> wayPoints) {
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.wayPoints.clear();
        this.wayPoints.addAll(wayPoints);
        this.notifyDataSetChanged();
    }

    public void refreshItems() {
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        WayPoint item = (WayPoint) v.getTag();
        int position = this.wayPoints.indexOf(item);
        this.recyclerViewItemClickListener.onItemClickListener(v, position);
    }

    public int getPositionByItem(WayPoint wayPoint) {
        return this.wayPoints.indexOf(wayPoint);
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    public WayPoint getItem(int position) {
        return this.wayPoints.get(position);
    }

    @Override
    public int getItemCount() {
        return this.wayPoints.size();
    }
}
