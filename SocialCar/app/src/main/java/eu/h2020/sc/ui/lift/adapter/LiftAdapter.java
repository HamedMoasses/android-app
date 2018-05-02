package eu.h2020.sc.ui.lift.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;
import eu.h2020.sc.utils.DateUtils;

/**
 * Created by Pietro on 19/07/16.
 */
public class LiftAdapter extends RecyclerView.Adapter<LiftViewHolder> implements View.OnClickListener {

    public static final String TRIP_DATE_FORMAT = "dd/MM/yyyy";
    public static final String TRIP_TIME_12H_FORMAT = "hh:mm a";
    public static final String TRIP_TIME_24H_FORMAT = "HH:mm";

    private List<Lift> lifts;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    private Resources resources;
    private Context context;

    public LiftAdapter(List<Lift> lifts) {
        this.lifts = lifts;
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.context = SocialCarApplication.getContext();
        this.resources = SocialCarApplication.getContext().getResources();
    }

    @Override
    public LiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewMyLiftRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_lifts_row, parent, false);
        return new LiftViewHolder(viewMyLiftRow);
    }

    @Override
    public void onBindViewHolder(LiftViewHolder liftViewHolder, int position) {
        AnimationHelper.runEnterAnimationRow(liftViewHolder.itemView, position);

        Lift lift = this.getItem(position);

        liftViewHolder.relativeLayoutRowLiftStatusMsg.setVisibility(View.GONE);
        liftViewHolder.textViewLiftDepartureArrival.setText(String.format("%s - %s", lift.getStartPoint().getAddress(), lift.getEndPoint().getAddress()));

        String tripDate = DateFormat.getDateFormat(this.context).format(lift.getStartPoint().getDate());
        String tripTime = DateFormat.getTimeFormat(this.context).format(lift.getStartPoint().getDate());

        liftViewHolder.textViewLiftDate.setText(String.format("%s: %s %s %s", this.resources.getString(R.string.departure), tripDate, this.resources.getString(R.string.at_date), tripTime));
        GradientDrawable gd = (GradientDrawable) liftViewHolder.imageViewLiftStatus.getDrawable();
        gd.setStroke(0, 0);

        switch (lift.getStatus()) {
            case ACTIVE:
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.active_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.green));
                break;
            case CANCELLED:
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.cancelled_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.white));
                gd.setStroke(4, ContextCompat.getColor(this.context, R.color.red));
                break;
            case REVIEWED:
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.completed_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.medium_grey));
                break;
            case COMPLETED:
                liftViewHolder.relativeLayoutRowLiftStatusMsg.setVisibility(View.VISIBLE);
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.completed_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.medium_grey));
                break;
            case PENDING:
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.pending_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.yellow));
                break;
            case REFUSED:
                liftViewHolder.textViewLiftStatus.setText(this.resources.getString(R.string.declined_trip));
                gd.setColor(ContextCompat.getColor(this.context, R.color.red));
                break;
            default:
                break;
        }

        liftViewHolder.relativeLayoutRowLiftStatus.setTag(lift);
        liftViewHolder.relativeLayoutRowLiftStatus.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return this.lifts.size();
    }


    public void refreshRecyclerView(List<Lift> lifts) {
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.lifts.clear();
        this.lifts.addAll(lifts);
        this.notifyDataSetChanged();
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    public Lift getItem(int position) {
        return this.lifts.get(position);
    }

    @Override
    public void onClick(View v) {
        Lift item = (Lift) v.getTag();
        int position = lifts.indexOf(item);
        recyclerViewItemClickListener.onItemClickListener(v, position);
    }
}
