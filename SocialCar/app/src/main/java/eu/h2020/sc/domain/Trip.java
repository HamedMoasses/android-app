/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;

/**
 * @author fminori
 */
public class Trip implements Serializable {

    private static final String TAG = Trip.class.getCanonicalName();

    public static final String TRIPS = "trips";
    public static final String TRIP = "trip";

    @SerializedName(value = "steps")
    @Expose
    private List<Step> steps;

    public Trip() {
        this.steps = new ArrayList<>();
    }

    public int getTripDurationInMinutes() {
        long diffInMillisec = steps.get(steps.size() - 1).getRoute().getEndPoint().getDate().getTime() - steps.get(0).getRoute().getStartPoint().getDate().getTime();
        return (int) diffInMillisec / (60 * 1000);
    }

    public Date getStartDate() {
        return this.steps.get(0).getRoute().getStartPoint().getDate();
    }

    public Date getEndDate() {
        return new Date(getStartDate().getTime() + getTripDurationInMinutes() * 60 * 1000);
    }

    public List<Step> getSteps() {
        return Collections.unmodifiableList(this.steps);
    }

    public boolean hasDriverStep() {
        for (Step step : this.getSteps()) {

            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING))
                return true;
        }
        return false;
    }

    public boolean hasExternalDriverStep() {
        for (Step step : this.getSteps()) {
            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING) && step.getPrivateTransport().getPublicURI() != null)
                return true;
        }
        return false;
    }

    public Step getDriverStep() {
        for (Step step : this.getSteps()) {
            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING))
                return step;
        }
        return null;
    }

    public Car retrieveCar(String carID) {
        for (Step step : this.getSteps()) {
            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING) && step.getPrivateTransport().getCar().getId().equalsIgnoreCase(carID))
                return step.getPrivateTransport().getCar();
        }
        return null;
    }

    public User retrieveDriver(String driverID) {
        for (Step step : this.getSteps()) {
            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING) && step.getPrivateTransport().getDriver().getId().equalsIgnoreCase(driverID)) {
                return step.getPrivateTransport().getDriver();
            }

        }
        return null;
    }

    public boolean areStepsEmpty() {
        return this.steps.isEmpty();
    }

    public static Trip fromJson(String jsonTrip) {
        return SocialCarApplication.getGson().fromJson(jsonTrip, Trip.class);
    }

    public List<PrivateTransport> retrievePrivateTransportList() {
        List<PrivateTransport> listPrivate = new ArrayList<>();
        for (Step step : this.getSteps()) {
            if (step.getTransport().getTravelMode().equals(TravelMode.CAR_POOLING))
                listPrivate.add(step.getPrivateTransport());
        }
        return listPrivate;
    }

    public static List<Trip> filterByTravelModes(List<Trip> trips, List<TravelMode> travelModesFilter) {
        List<Trip> filteredList = new ArrayList<>();
        HashSet<TravelMode> travelModesInTrip = new HashSet<>();

        for (Trip trip : trips) {
            for (Step step : trip.getSteps()) {
                TravelMode travelMode = step.getTransport().getTravelMode();
                if (!travelMode.equals(TravelMode.FEET)) {
                    travelModesInTrip.add(travelMode);
                }
            }

            for (TravelMode travelModeInTrip : travelModesInTrip) {
                if (travelModesFilter.contains(travelModeInTrip)) {

                    filteredList.add(trip);
                    break;
                }
            }

            travelModesInTrip.clear();
        }
        return filteredList;
    }

    public static List<Trip> orderByCheapestRoute(List<Trip> trips) {

        Collections.sort(trips, new Comparator<Trip>() {
            @Override
            public int compare(Trip trip, Trip trip2) {
                return trip.getPrice().getAmountTwoDecimals().compareTo(trip2.getPrice().getAmountTwoDecimals());
            }
        });

        return trips;
    }

    public static List<Trip> orderByFasterRoute(List<Trip> trips) {

        Collections.sort(trips, new Comparator<Trip>() {
            @Override
            public int compare(Trip trip, Trip trip2) {
                return trip.getTripDurationInMinutes() - trip2.getTripDurationInMinutes();
            }
        });

        return trips;
    }

    public Price getPrice() {
        Price totalPrice = new Price(new BigDecimal(0));

        for (Step step : this.steps) {

            if (TravelMode.FEET != step.getTransport().getTravelMode()) {

                if (step.getPrice().getAmount().equals(new BigDecimal(-1))) {
                    totalPrice.setAmount(new BigDecimal(-1));
                    totalPrice.setCurrency(step.getPrice().getCurrency());
                    break;
                }
                totalPrice.setAmount(totalPrice.getAmount().add(step.getPrice().getAmount()));
                totalPrice.setCurrency(step.getPrice().getCurrency());

            }
        }

        return totalPrice;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "steps=" + steps +
                '}';
    }
}
