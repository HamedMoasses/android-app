package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class RatingFeature {

    @Expose
    @SerializedName(value = "comfort_level")
    private Integer comfortLevel;

    @Expose
    private Integer route;

    @Expose
    @SerializedName(value = "driving_behaviour")
    private Integer drivingBehaviour;

    @Expose
    private Integer duration;

    @Expose
    @SerializedName(value = "satisfaction_level")
    private Integer satisfactionLevel;

    @Expose
    private Integer punctuation;

    @Expose
    @SerializedName(value = "carpooler_behaviour")
    private Integer carpoolerBehaviour;


    public Integer getComfortLevel() {
        return comfortLevel;
    }

    public Integer getRoute() {
        return route;
    }

    public Integer getDrivingBehaviour() {
        return drivingBehaviour;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getSatisfactionLevel() {
        return satisfactionLevel;
    }

    public Integer getPunctuation() {
        return punctuation;
    }

    public Integer getCarpoolerBehaviour() {
        return carpoolerBehaviour;
    }

    public void setCarpoolerBehaviour(Integer carpoolerBehaviour) {
        this.carpoolerBehaviour = carpoolerBehaviour;
    }

    public void setComfortLevel(Integer comfortLevel) {
        this.comfortLevel = comfortLevel;
    }

    public void setRoute(Integer route) {
        this.route = route;
    }

    public void setDrivingBehaviour(Integer drivingBehaviour) {
        this.drivingBehaviour = drivingBehaviour;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setSatisfactionLevel(Integer satisfactionLevel) {
        this.satisfactionLevel = satisfactionLevel;
    }

    public void setPunctuation(Integer punctuation) {
        this.punctuation = punctuation;
    }
}
