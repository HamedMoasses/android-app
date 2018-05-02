package eu.h2020.sc.ui.ride.myrides;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.views.MapView;

import eu.h2020.sc.R;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class MyRidesViewHolder extends RecyclerView.ViewHolder {

    public CardView card;
    public TextView textViewTitle;
    public TextView textViewSubTitle;
    public TextView rideMessage;
    public TextView textViewCreateRide;
    public SwitchCompat rideSwitch;
    public ImageView imageViewExpandMap;
    public TextView textViewDeleteTrip;
    public MapView mapView;
    public LinearLayout passengerAvatarLayout;
    public View viewOnSwitch;

    public MyRidesViewHolder(View itemView) {
        super(itemView);
        this.card = (CardView) itemView.findViewById(R.id.card);
        this.textViewTitle = (TextView) itemView.findViewById(R.id.ride_title);
        this.textViewSubTitle = (TextView) itemView.findViewById(R.id.ride_subTitle);
        this.rideMessage = (TextView) itemView.findViewById(R.id.message);
        this.textViewCreateRide = (TextView) itemView.findViewById(R.id.text_create_ride);
        this.rideSwitch = (SwitchCompat) itemView.findViewById(R.id.ride_switch);
        this.mapView = (MapView) itemView.findViewById(R.id.mapView);
        this.imageViewExpandMap = (ImageView) itemView.findViewById(R.id.expande_map_icon);
        this.textViewDeleteTrip = (TextView) itemView.findViewById(R.id.delete_trip_action);
        this.passengerAvatarLayout = (LinearLayout) itemView.findViewById(R.id.passengerAvatarLayout);
        this.viewOnSwitch = itemView.findViewById(R.id.viewOnSwitch);
    }

}
