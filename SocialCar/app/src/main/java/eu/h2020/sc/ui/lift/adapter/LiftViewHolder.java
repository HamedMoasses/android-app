package eu.h2020.sc.ui.lift.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.utils.MaterialRippleLayout;

/**
 * Created by Pietro on 19/07/16.
 */
public class LiftViewHolder extends RecyclerView.ViewHolder {


    public RelativeLayout relativeLayoutRowLiftStatus;
    public RelativeLayout relativeLayoutRowLiftStatusMsg;
    public TextView textViewLiftStatus;
    public TextView textViewLiftDepartureArrival;
    public TextView textViewLiftDate;
    public TextView textViewLiftStatusMessage;
    public ImageView imageViewLiftStatus;

    public MaterialRippleLayout materialRippleLayout;

    public LiftViewHolder(View itemView) {
        super(itemView);

        relativeLayoutRowLiftStatusMsg = (RelativeLayout) itemView.findViewById(R.id.relative_layout_row_lift_status_msg);
        relativeLayoutRowLiftStatus = (RelativeLayout) itemView.findViewById(R.id.relative_layout_row_lift_status_container);
        textViewLiftStatus = (TextView) itemView.findViewById(R.id.text_view_lift_status);
        textViewLiftDepartureArrival = (TextView) itemView.findViewById(R.id.text_view_lift_departure_arrival);
        textViewLiftDate = (TextView) itemView.findViewById(R.id.text_view_lift_date);
        textViewLiftStatusMessage = (TextView) itemView.findViewById(R.id.text_view_lift_status_msg);
        imageViewLiftStatus = (ImageView) itemView.findViewById(R.id.image_view_lift_status);
        materialRippleLayout = (MaterialRippleLayout)itemView.findViewById(R.id.lift_row);
    }
}
