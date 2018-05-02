package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.protocol.GetRidesRequest;

/**
 * Created by alam on 01/09/16.
 */
public class Ride {

    public static final String ID = "_id";
    public static final String RIDE_ID = "ride_id";

    public static final String NAME = "name";
    public static final String POLYLINE = "polyline";
    public static final String LIFTS = "lifts";
    public static final String DATE = "date";
    public static final String START_POINT = "start_point";
    public static final String END_POINT = "end_point";
    public static final String DRIVER_ID = "driver_id";
    public static final String CAR_ID = "car_id";
    public static final String ACTIVATED = "activated";

    @Expose(serialize = false)
    private String id;

    @Expose
    private String name;

    private Route route;

    @Expose
    private String polyline;

    @Expose
    private boolean activated;

    @Expose
    private String carId;

    @Expose
    private List<Lift> lifts;

    @Expose
    private String driverId;

    @Deprecated
    public Ride() {
    }

    public Ride(String name, Route route, String polyline, String carId, String driverId) {
        this.driverId = driverId;
        this.route = route;
        this.polyline = polyline;
        this.carId = carId;
        this.lifts = new ArrayList<>();
        this.name = name;
    }

    public static Ride fromJson(String jsonRide) {
        return SocialCarApplication.getGson().fromJson(jsonRide, Ride.class);
    }

    public static List<Ride> fromJsonToRideList(String jsonRideList) throws JSONException {

        JSONObject jsonResponse = new JSONObject(jsonRideList);
        JSONArray ridesJson = jsonResponse.getJSONArray(GetRidesRequest.RIDES);

        return SocialCarApplication.getGson().fromJson(ridesJson.toString(), new TypeToken<List<Ride>>() {
        }.getType());
    }

    public String toJsonForRequest() {
        SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return SocialCarApplication.getGsonBuilder().create().toJson(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Route getRoute() {
        return route;
    }

    public String getPolyline() {
        return polyline;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isNameValid() {
        return !this.name.isEmpty();
    }

    public boolean isAfterNow() {
        return this.route.getStartPoint().getDate().after(new Date());
    }

    public String getDriverId() {
        return driverId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Lift> getLifts() {
        return lifts;
    }

    public void deleteAllLifts() {
        this.lifts.clear();
    }

    public void addAll(List<Lift> newLifts) {
        this.lifts.clear();
        this.lifts.addAll(newLifts);
    }

    public String getCarId() {
        return carId;
    }


    public String retrievePassengerImagePathByLift(Lift lift) {
        return lift.getPassengerImgPath();
    }

    public List<String> retrievePassengerIDs() {

        List<String> passengersID = new ArrayList<String>();

        for (Lift lift : this.lifts)
            passengersID.add(lift.getPassengerID());

        return passengersID;
    }

    public boolean hasOneActiveLift() {

        for (Lift lift : this.lifts) {

            if (lift.isActive())
                return true;
        }

        return false;
    }
}
