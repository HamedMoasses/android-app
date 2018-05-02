package eu.h2020.sc.ui.waitingtime.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.h2020.sc.R;

import eu.h2020.sc.domain.TransitItem;
import eu.h2020.sc.ui.waitingtime.viewHolder.RowRealTimeViewHolder;
import eu.h2020.sc.utils.AnimationHelper;

import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class WaitingTimeAdapter extends RecyclerView.Adapter {

    private static final String TAG = WaitingTimeAdapter.class.getName();

    private static int ROW_REAL_TIME = 1;

    private List<TransitItem> transitItems;

    public WaitingTimeAdapter(List<TransitItem> transitItems) {
        this.transitItems = transitItems;
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;
    }

    @Override
    public int getItemViewType(int position) {
        return ROW_REAL_TIME;
    }


    public void refreshRecyclerView(List<TransitItem> transitList) {

        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.transitItems.clear();
        this.transitItems.addAll(transitList);
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ROW_REAL_TIME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_realtime, parent, false);
            return new RowRealTimeViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AnimationHelper.runEnterAnimationRow(holder.itemView, position);
        try {
            if (holder instanceof RowRealTimeViewHolder) {
                RowRealTimeViewHolder h = (RowRealTimeViewHolder) holder;
                h.init(this.getItem(position));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error...", e);
        }
    }

    @Override
    public int getItemCount() {
        return this.transitItems.size();
    }

    public TransitItem getItem(int position) {
        return (this.transitItems != null ? this.transitItems.get(position) : null);
    }
}
