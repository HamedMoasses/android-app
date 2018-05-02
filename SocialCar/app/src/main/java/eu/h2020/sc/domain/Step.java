/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;

/**
 * @author fminori
 */
public class Step implements Serializable {

    @Expose
    private Route route;
    @Expose
    private Transport transport;
    @Expose
    private Price price;
    @Expose
    private Integer distance;

    public Step(Route route, Transport transport) {
        this.route = route;
        this.transport = transport;
    }

    public Route getRoute() {
        return route;
    }

    public Price getPrice() {
        return price;
    }

    public Transport getTransport() {
        return transport;
    }

    public PrivateTransport getPrivateTransport() {
        return ((PrivateTransport) this.getTransport());
    }

    public PublicTransport getPublicTransport() {
        return ((PublicTransport) this.getTransport());
    }

    public int getDurationInMin() {
        long durationMillis = this.getRoute().getEndPoint().getDate().getTime() - this.getRoute().getStartPoint().getDate().getTime();
        double stepIntervalMin = (double) durationMillis / (60 * 1000);
        return (int) Math.round(stepIntervalMin);
    }

    public String getDistance() {

        if (this.transport.getTravelMode().equals(TravelMode.FEET) || this.transport.getTravelMode().equals(TravelMode.CAR_POOLING))
            return this.getDistance(this.distance);
        else
            return String.format("%s %s", distance, SocialCarApplication.getContext().getString(R.string.stops));
    }

    private String getDistance(int value) {
        try {
            DecimalFormat df = new DecimalFormat("##.##");

            if (Locale.getDefault().equals(Locale.UK)) {

                return String.format("%s miles",
                        new DecimalFormat("#.##").format((double) value * 0.000621371));

            } else {
                if (value > 1000) {
                    double km = (double) value / 1000;

                    if (km > 0)
                        return String.format("%s km", df.format(km));
                }
            }

        } catch (NumberFormatException e) {
            return "- -";
        }
        return String.format("%s %s", value, SocialCarApplication.getContext().getString(R.string.distance_meters));
    }


    public boolean isPublicTransportStep() {
        return transport.getTravelMode().equals(TravelMode.BUS)
                || transport.getTravelMode().equals(TravelMode.METRO)
                || transport.getTravelMode().equals(TravelMode.RAIL)
                || transport.getTravelMode().equals(TravelMode.TRAM);
    }


    public Drawable getTransportIcon(Context context, Step step) {

        int resource = 0;

        Transport transport = step.getTransport();

        if (transport.getTravelMode().equals(TravelMode.FEET))
            resource = R.drawable.ic_directions_walk_24dp;

        else if (transport.getTravelMode().equals(TravelMode.CAR_POOLING))
            resource = R.drawable.ic_directions_carpooler_grey_24dp;

        else if (transport.getTravelMode().equals(TravelMode.BUS))
            resource = R.drawable.ic_directions_bus_black_24dp;

        else if (transport.getTravelMode().equals(TravelMode.METRO))
            resource = R.drawable.ic_directions_subway_black_24dp;

        else if (transport.getTravelMode().equals(TravelMode.TRAM))
            resource = R.drawable.ic_directions_tram_24dp;

        else if (transport.getTravelMode().equals(TravelMode.RAIL))
            resource = R.drawable.ic_directions_railway_black_24dp;

        return ContextCompat.getDrawable(context, resource);
    }


    @Override
    public String toString() {
        return "Step{" +
                "route=" + route +
                ", transport=" + transport +
                ", price=" + price +
                '}';
    }
}
