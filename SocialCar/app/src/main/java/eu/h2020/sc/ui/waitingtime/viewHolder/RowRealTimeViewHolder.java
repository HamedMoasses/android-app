package eu.h2020.sc.ui.waitingtime.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.SimpleTime;
import eu.h2020.sc.domain.TransitItem;
import eu.h2020.sc.domain.TravelMode;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class RowRealTimeViewHolder extends RecyclerView.ViewHolder {

    protected TextView textViewLineRoute;
    protected TextView textViewWaitingTime;
    protected TextView textViewRemainingStops;
    protected TextView textViewPastTerminalDeparture;
    protected TextView textViewNextTerminalDeparture;
    protected TextView textViewFutureTerminalDeparture;
    protected View viewDividerBoxRealTime;
    private LinearLayout linearLayoutTerminalDepartures;
    private View itemView;
    private ImageView transportImageView;


    public RowRealTimeViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.linearLayoutTerminalDepartures = (LinearLayout) this.itemView.findViewById(R.id.layout_terminal_departures);
        this.textViewLineRoute = (TextView) this.itemView.findViewById(R.id.text_view_line_route_name);
        this.textViewWaitingTime = (TextView) this.itemView.findViewById(R.id.text_view_waiting_time_minutes);
        this.textViewRemainingStops = (TextView) this.itemView.findViewById(R.id.text_view_waiting_time_stops_remaining);
        this.textViewPastTerminalDeparture = (TextView) this.itemView.findViewById(R.id.text_view_past_departure);
        this.textViewNextTerminalDeparture = (TextView) this.itemView.findViewById(R.id.text_view_next_departure);
        this.textViewFutureTerminalDeparture = (TextView) this.itemView.findViewById(R.id.text_view_future_departure);
        this.viewDividerBoxRealTime = this.itemView.findViewById(R.id.view_divider_box_real_time);
        this.transportImageView = (ImageView) this.itemView.findViewById(R.id.img_public_transport);
    }

    public void init(TransitItem transit) {

        this.textViewLineRoute.setText(String.format("%s %s", transit.getPublicTransport().getShortName(), transit.getPublicTransport().getLongName()));

        if (transit.getPublicTransport().getTravelMode() == TravelMode.BUS) {
            transportImageView.setImageResource(R.drawable.ic_directions_bus_black_24dp);
        } else if (transit.getPublicTransport().getTravelMode() == TravelMode.METRO) {
            transportImageView.setImageResource(R.drawable.ic_directions_subway_black_24dp);
        } else if (transit.getPublicTransport().getTravelMode() == TravelMode.TRAM) {
            transportImageView.setImageResource(R.drawable.ic_directions_tram_24dp);
        } else if (transit.getPublicTransport().getTravelMode() == TravelMode.RAIL) {
            transportImageView.setImageResource(R.drawable.ic_directions_railway_black_24dp);
        }

        if (transit.isRealTime()) {

            this.textViewWaitingTime.setVisibility(View.VISIBLE);
            this.textViewRemainingStops.setVisibility(View.VISIBLE);
            this.viewDividerBoxRealTime.setVisibility(View.VISIBLE);

            if (transit.isArriving()) {
                this.textViewWaitingTime.setText(itemView.getContext().getString(R.string.arriving));
                this.textViewRemainingStops.setVisibility(View.GONE);
            } else {
                this.textViewWaitingTime.setText(String.format("%s %s", transit.getWaitingTime(), itemView.getContext().getString(R.string.min)));
                this.textViewRemainingStops.setText(String.format("%s %s", String.valueOf(transit.getStopDistance()), itemView.getContext().getString(R.string.stops)));
            }

        } else if (transit.isOffline()) {
            this.textViewWaitingTime.setVisibility(View.GONE);
            this.textViewRemainingStops.setVisibility(View.GONE);
            this.viewDividerBoxRealTime.setVisibility(View.GONE);
        }

        this.checkTerminalDepartures(transit);
    }

    private void checkTerminalDepartures(TransitItem transit) {
        if (transit.isNotMonitored()) {
            this.linearLayoutTerminalDepartures.setVisibility(View.GONE);
        } else {
            this.showTerminalDepartures(transit);
        }
    }

    private void showTerminalDepartures(TransitItem transit) {
        int i = 0;
        for (SimpleTime time : transit.getTerminusDepartures()) {
            if (i == 0)
                this.textViewPastTerminalDeparture.setText(String.format("%s", time.toString()));
            else if (i == 1)
                this.textViewNextTerminalDeparture.setText(String.format("%s", time.toString()));
            else if (i == 2)
                this.textViewFutureTerminalDeparture.setText(String.format("%s", time.toString()));

            i++;
        }
    }

}
