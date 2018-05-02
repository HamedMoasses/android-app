package eu.h2020.sc.ui.ride.creation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import eu.h2020.sc.R;
import eu.h2020.sc.utils.TextViewCustom;

public class WayPointViewHolder extends RecyclerView.ViewHolder {

    public TextViewCustom textViewWayPointAddress;
    public ImageView imageViewCancel;

    public WayPointViewHolder(View itemView) {
        super(itemView);

        this.textViewWayPointAddress = (TextViewCustom) itemView.findViewById(R.id.text_view_way_point_address);
        this.imageViewCancel = (ImageView) itemView.findViewById(R.id.image_view_cancel);
    }
}
