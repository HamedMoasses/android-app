/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.protocol.FindLiftsRequest;

/**
 * @author fminori
 */
public class Lift implements Serializable {

    @SerializedName(value = "_id")
    @Expose(serialize = false)
    private String id;

    @SerializedName(value = "passenger_id")
    @Expose
    private String passengerID;

    @SerializedName(value = "ride_id")
    @Expose
    private String rideID;

    @SerializedName(value = "car_id")
    @Expose
    private String carID;

    @SerializedName(value = "driver_id")
    @Expose
    private String driverID;

    @SerializedName(value = "passenger_img")
    @Expose
    private String passengerImgPath;

    @SerializedName(value = "trip")
    @Expose
    private Trip trip;

    @SerializedName(value = "status")
    @Expose
    private LiftStatus status;

    @SerializedName(value = "start_point")
    @Expose
    private TimeSpacePoint startPoint;

    @SerializedName(value = "end_point")
    @Expose
    private TimeSpacePoint endPoint;

    public Lift(String passengerID, Trip trip, TimeSpacePoint startPoint, TimeSpacePoint endPoint, String rideID, String carID, String driverID) {
        this.status = LiftStatus.PENDING;
        this.passengerID = passengerID;
        this.trip = trip;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.rideID = rideID;
        this.carID = carID;
        this.driverID = driverID;
    }

    public static Lift fromJson(String jsonLift) {
        return SocialCarApplication.getGson().fromJson(jsonLift, Lift.class);
    }

    public static List<Lift> fromJsonToLiftList(String jsonLiftList) throws JSONException {

        JSONObject jsonResponse = new JSONObject(jsonLiftList);
        JSONArray liftsJson = jsonResponse.getJSONArray(FindLiftsRequest.LIFTS);

        return SocialCarApplication.getGson().fromJson(liftsJson.toString(), new TypeToken<List<Lift>>() {
        }.getType());
    }

    public LiftStatus getStatus() {
        return status;
    }

    public void setStatus(LiftStatus status) {
        this.status = status;
    }

    public String getID() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public TimeSpacePoint getStartPoint() {
        return startPoint;
    }

    public TimeSpacePoint getEndPoint() {
        return endPoint;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getDriverID() {
        return driverID;
    }

    public String getPassengerImgPath() {
        return passengerImgPath;
    }

    public String toJson() {
        return SocialCarApplication.getGson().toJson(this);
    }

    public String toJsonForRequest() {
        SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return SocialCarApplication.getGsonBuilder().create().toJson(this);
    }

    public boolean isActive() {
        return this.status.equals(LiftStatus.ACTIVE);
    }

    public boolean isCompleted() {
        return this.status.equals(LiftStatus.COMPLETED);
    }

    @Override
    public String toString() {
        return "Lift{" +
                "id='" + id + '\'' +
                ", passengerID='" + passengerID + '\'' +
                ", rideID='" + rideID + '\'' +
                ", carID='" + carID + '\'' +
                ", driverID='" + driverID + '\'' +
                ", passengerImgPath='" + passengerImgPath + '\'' +
                ", trip=" + trip +
                ", status=" + status +
                ", startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }
}
